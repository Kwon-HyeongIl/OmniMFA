package com.khi.onboardingservice.security.filter;

import com.khi.onboardingservice.security.entity.SecurityUserPrincipalEntity;
import com.khi.onboardingservice.security.principal.SecurityUserPrincipal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
public class SecurityContextGatewayFilter extends OncePerRequestFilter {

    private static final Map<String, Set<HttpMethod>> FILTERED_PATHS = Map.ofEntries(
            Map.entry("/onboarding/enroll", Set.of(HttpMethod.POST)));

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        HttpMethod method = HttpMethod.valueOf(request.getMethod());

        return !FILTERED_PATHS.getOrDefault(path, Collections.emptySet()).contains(method);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        log.info("SecurityGatewayFilter 실행");

        Long uid = Long.valueOf(request.getHeader("Uid"));
        String role = request.getHeader("Role");

        SecurityUserPrincipalEntity userPrincipalEntity = new SecurityUserPrincipalEntity();

        userPrincipalEntity.setUid(uid);
        userPrincipalEntity.setRole(role);

        UserDetails userDetails = new SecurityUserPrincipal(userPrincipalEntity);

        Authentication authToken = new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authToken);

        log.info("SecurityContextHolder에 유저 저장 완료");

        filterChain.doFilter(request, response);
    }
}
