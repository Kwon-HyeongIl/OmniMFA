package com.khi.securityservice.security.filter.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.khi.securityservice.api.api.ApiResponse;
import com.khi.securityservice.security.enumeration.JwtTokenType;
import com.khi.securityservice.security.exception.type.SecurityAuthenticationException;
import com.khi.securityservice.security.util.JwtUtil;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.khi.securityservice.security.repository.RedisRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtReissueFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    private final RedisRepository refreshTokenRedisRepository;

    private final ObjectMapper objectMapper;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {

        return !(request.getRequestURI().equals("/security/jwt/reissue")
                && "POST".equalsIgnoreCase(request.getMethod()));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        log.info("JwtReissueFilter 실행");

        String refreshToken = null;

        Cookie[] cookies = request.getCookies();

        for (Cookie cookie : cookies) {

            if (cookie.getName().equals("Refresh-Token")) {

                refreshToken = cookie.getValue();
            }
        }

        // Refresh 토큰이 비어있는지 검증
        if (refreshToken == null) {

            throw new SecurityAuthenticationException("리프레시 토큰이 존재하지 않습니다.");
        }

        // Refresh 토큰 만료 여부 검증
        try {

            jwtUtil.isExpired(refreshToken);

        } catch (ExpiredJwtException e) {

            throw new SecurityAuthenticationException("리프레시 토큰이 만료되었습니다.");
        }

        // Refresh 토큰 타입 검증
        JwtTokenType tokenType = jwtUtil.getTokenType(refreshToken);

        if (tokenType != JwtTokenType.REFRESH) {

            throw new SecurityAuthenticationException("토큰 타입이 리프레시 타입과 일치하지 않습니다.");
        }

        // DB에 Refresh 토큰이 존재하는지 검증
        String uid = jwtUtil.getUid(refreshToken);

        String storedRefreshToken = refreshTokenRedisRepository.getRefreshToken(uid);

        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {

            throw new SecurityAuthenticationException("서버에 일치하는 리프레시 토큰이 존재하지 않습니다.");
        }

        log.info("Refresh 토큰 검증 완료");

        String role = jwtUtil.getRole(refreshToken);

        String newAccessToken = jwtUtil.createJwt(JwtTokenType.ACCESS, uid, role, 600_000L);
        String newRefreshToken = jwtUtil.createJwt(JwtTokenType.REFRESH, uid, role, 86_400_000L);

        log.info("새로운 Access, Refresh 토큰 발급 완료");

        // Redis에서 기존 Refresh 토큰 삭제 및 새로운 토큰 저장
        refreshTokenRedisRepository.deleteRefreshToken(uid);
        refreshTokenRedisRepository.saveRefreshToken(uid, newRefreshToken);

        ApiResponse<?> apiResponse = ApiResponse.success();

        String jsonApiResponse = objectMapper.writeValueAsString(apiResponse);

        response.setHeader("Access-Token", newAccessToken);
        response.addCookie(createCookie("Refresh-Token", newRefreshToken));

        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(jsonApiResponse);
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24 * 60 * 60);
        // cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }
}
