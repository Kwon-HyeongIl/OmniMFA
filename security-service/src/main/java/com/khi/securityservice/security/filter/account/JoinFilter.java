package com.khi.securityservice.security.filter.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.khi.securityservice.api.api.ApiResponse;
import com.khi.securityservice.security.entity.domain.UserEntity;
import com.khi.securityservice.security.exception.type.SecurityAuthenticationException;
import com.khi.securityservice.security.repository.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class JoinFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    private final ObjectMapper objectMapper;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {

        return !(request.getRequestURI().equals("/security/join") && "POST".equalsIgnoreCase(request.getMethod()));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        log.info("JoinFilter 실행");

        Map<String, String> body = objectMapper.readValue(request.getInputStream(), Map.class);

        String loginId = body.get("loginId");
        String password = body.get("password");

        if (userRepository.existsByLoginId(loginId)) {

            throw new SecurityAuthenticationException("이미 존재하는 아이디입니다.");
        }

        UserEntity user = new UserEntity();

        user.setLoginId(loginId);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("ROLE_ADMIN");

        userRepository.save(user);

        ApiResponse<?> apiResponse = ApiResponse.success();

        String jsonApiResponse = objectMapper.writeValueAsString(apiResponse);

        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(jsonApiResponse);
    }
}
