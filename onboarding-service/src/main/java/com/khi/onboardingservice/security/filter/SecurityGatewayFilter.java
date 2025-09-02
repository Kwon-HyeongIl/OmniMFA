package com.khi.onboardingservice.security.filter;

import com.khi.onboardingservice.security.entity.SecurityUserPrincipalEntity;
import com.khi.onboardingservice.security.principal.SecurityUserPrincipal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class SecurityGatewayFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        log.info("SecurityGatewayFilter 실행");

        String uid = request.getHeader("Uid");
        String role = request.getHeader("Role");

        SecurityUserPrincipalEntity userPrincipalEntity = new SecurityUserPrincipalEntity();

        userPrincipalEntity.setUid(uid);
        userPrincipalEntity.setRole(role);

        UserDetails userDetails = new SecurityUserPrincipal(userPrincipalEntity);

        Authentication authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authToken);

        log.info("SecurityContextHolder에 유저 저장 완료");

        filterChain.doFilter(request, response);
    }
}
