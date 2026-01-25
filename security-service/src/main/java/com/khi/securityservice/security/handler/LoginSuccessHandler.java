package com.khi.securityservice.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.khi.securityservice.api.api.ApiResponse;
import com.khi.securityservice.security.enumeration.JwtTokenType;
import com.khi.securityservice.security.principal.SecurityUserPrincipal;
import com.khi.securityservice.security.util.JwtUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;

    private final RedisTemplate<String, Object> redisTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        log.info("Form 로그인 Success Handler 실행");

        SecurityUserPrincipal userDetails = (SecurityUserPrincipal) authentication.getPrincipal();

        String uid = userDetails.getUsername();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();

        String role = auth.getAuthority();

        // 토큰 생성
        String accessToken = jwtUtil.createJwt(JwtTokenType.ACCESS, uid, role, 600_000L);
        String refreshToken = jwtUtil.createJwt(JwtTokenType.REFRESH, uid, role, 86_400_000L);

        // Redis에 Refresh 토큰 저장
        redisTemplate.opsForValue().set(uid, refreshToken, 86_400_000L, TimeUnit.MILLISECONDS);

        log.info("Redis에 Refresh 토큰 저장 완료");

        ApiResponse<?> apiResponse = ApiResponse.success();

        String jsonApiResponse = objectMapper.writeValueAsString(apiResponse);

        // 응답 설정
        response.setHeader("Access-Token", "Bearer " + accessToken);
        response.addCookie(createCookie("Refresh-Token", refreshToken));

        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(jsonApiResponse);
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);
        //cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }
}
