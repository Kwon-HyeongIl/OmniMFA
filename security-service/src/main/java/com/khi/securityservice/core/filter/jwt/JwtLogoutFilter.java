package com.khi.securityservice.core.filter.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.khi.securityservice.common.api.ApiResponse;
import com.khi.securityservice.core.enumeration.JwtTokenType;
import com.khi.securityservice.core.exception.type.SecurityAuthenticationException;
import com.khi.securityservice.core.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtLogoutFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    private final RedisTemplate<String, Object> redisTemplate;

    private final ObjectMapper objectMapper;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {

        return !(request.getRequestURI().equals("/security/logout") && "POST".equalsIgnoreCase(request.getMethod()));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        log.info("JwtLogoutFilter 실행");

        String refreshToken = null;

        // 쿠키에서 Refresh 토큰 추출
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
        String loginId = jwtUtil.getLoginId(refreshToken);

        Object redisRefreshToken = redisTemplate.opsForValue().get(loginId);

        if (redisRefreshToken == null || !redisRefreshToken.toString().equals(refreshToken)) {

            throw new SecurityAuthenticationException("서버에 일치하는 리프레시 토큰이 존재하지 않습니다.");
        }

        log.info("Refresh 토큰 검증 완료");

        // Redis에 기존에 존재하는 Refresh 토큰 삭제
        redisTemplate.delete(loginId);

        log.info("Redis에서 Refresh 토큰 삭제 완료");

        Cookie cookie = new Cookie("Refresh-Token", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");

        ApiResponse<?> apiResponse = ApiResponse.success();

        String jsonApiResponse = objectMapper.writeValueAsString(apiResponse);

        response.addCookie(cookie);
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(jsonApiResponse);
    }
}