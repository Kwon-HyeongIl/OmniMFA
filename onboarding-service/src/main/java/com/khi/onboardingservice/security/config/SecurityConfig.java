package com.khi.onboardingservice.security.config;

import com.khi.onboardingservice.security.exception.RestAccessDeniedHandler;
import com.khi.onboardingservice.security.exception.RestAuthenticationEntryPoint;
import com.khi.onboardingservice.security.filter.SecurityGatewayFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final SecurityGatewayFilter securityGatewayFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // JWT 방식은 state less 방식이므로 csrf 공격에 대비할 필요 없음
        http
                .csrf(AbstractHttpConfigurer::disable);

        // 폼 로그인 방식 disable
        http
                .formLogin(AbstractHttpConfigurer::disable);

        // http basic 인증 방식 disable
        http
                .httpBasic(AbstractHttpConfigurer::disable);

        // 시큐리티 예외 처리
        http
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(new RestAuthenticationEntryPoint())
                        .accessDeniedHandler(new RestAccessDeniedHandler())
                );

        // 인증 저장 필터
        http
                .addFilterAt(securityGatewayFilter, UsernamePasswordAuthenticationFilter.class);

        // 경로별 인가 작업
        http
                .authorizeHttpRequests((auth) -> auth

                        /* Swagger */
                        .requestMatchers(HttpMethod.GET, "/swagger-ui/index.html").permitAll()
                        .requestMatchers(HttpMethod.GET, "/swagger-ui/swagger-ui.css").permitAll()
                        .requestMatchers(HttpMethod.GET, "/swagger-ui/index.css").permitAll()
                        .requestMatchers(HttpMethod.GET, "/swagger-ui/swagger-ui-bundle.js").permitAll()
                        .requestMatchers(HttpMethod.GET, "/swagger-ui/swagger-ui-standalone-preset.js").permitAll()
                        .requestMatchers(HttpMethod.GET, "/swagger-ui/swagger-initializer.js").permitAll()
                        .requestMatchers(HttpMethod.GET, "/v3/api-docs/swagger-config").permitAll()
                        .requestMatchers(HttpMethod.GET, "/v3/api-docs").permitAll()

                        .requestMatchers("/security/admin").hasRole("ADMIN")

                        .anyRequest().authenticated());

        // 무상태 세션 설정
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
