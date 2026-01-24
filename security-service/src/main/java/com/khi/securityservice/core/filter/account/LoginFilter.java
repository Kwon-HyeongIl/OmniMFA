package com.khi.securityservice.core.filter.account;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    /*
     * new AntPathRequestMatcher("/security/login", "POST") 조건을 만족시킬 때만
     * attemptAuthentication 호출 (폼 로그인일 경우)
     * 이외의 경우에는 attemptAuthentication 스킵
     *
     * attempAuthentication 메서드의 authenticate가 성공적으로 인증되면 현재 필터 이후의 필터는 실행되지 않음
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        log.info("LoginFilter 실행");

        String loginId = obtainUsername(request);
        String password = obtainPassword(request);

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(loginId, password, null);

        return authenticationManager.authenticate(token);
    }
}
