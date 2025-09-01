package com.khi.securityservice.core.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.khi.securityservice.core.exception.RestAccessDeniedHandler;
import com.khi.securityservice.core.exception.RestAuthenticationEntryPoint;
import com.khi.securityservice.core.filter.account.JoinFilter;
import com.khi.securityservice.core.filter.account.LoginFilter;
import com.khi.securityservice.core.filter.jwt.JwtLogoutFilter;
import com.khi.securityservice.core.filter.jwt.JwtReissueFilter;
import com.khi.securityservice.core.handler.LoginSuccessHandler;
import com.khi.securityservice.core.repository.UserRepository;
import com.khi.securityservice.core.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;

    private final LoginSuccessHandler loginSuccessHandler;

    /* 필터 의존성 */
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, Object> redisTemplate;

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

        // 회원 가입 필터
        http
                .addFilterAfter(joinFilter(), ExceptionTranslationFilter.class);

        // 로그인 필터
        LoginFilter loginFilter = new LoginFilter(authenticationConfiguration.getAuthenticationManager());
        loginFilter.setFilterProcessesUrl("/security/login");
        loginFilter.setUsernameParameter("loginId");
        loginFilter.setAuthenticationSuccessHandler(loginSuccessHandler);

        http
                .addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class);

        // Access 토큰 재발급 필터 삽입
        http
                .addFilterAfter(jwtReissueFilter(), ExceptionTranslationFilter.class);

        // 로그아웃 필터 삽입
        http
                .addFilterAt(jwtLogoutFilter(), LogoutFilter.class);

        // 경로별 인가 작업
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers(HttpMethod.POST, "/security/join").permitAll()
                        .requestMatchers(HttpMethod.POST, "/security/jwt/reissue").permitAll()

                        /* Swagger */
                        .requestMatchers(HttpMethod.GET, "/swagger-ui/index.html").permitAll()
                        .requestMatchers(HttpMethod.GET, "/swagger-ui/swagger-ui.css").permitAll()
                        .requestMatchers(HttpMethod.GET, "/swagger-ui/index.css").permitAll()
                        .requestMatchers(HttpMethod.GET, "/swagger-ui/swagger-ui-bundle.js").permitAll()
                        .requestMatchers(HttpMethod.GET, "/swagger-ui/swagger-ui-standalone-preset.js").permitAll()
                        .requestMatchers(HttpMethod.GET, "/swagger-ui/swagger-initializer.js").permitAll()
                        .requestMatchers(HttpMethod.GET, "/v3/api-docs/swagger-config").permitAll()
                        .requestMatchers(HttpMethod.GET, "/v3/api-docs").permitAll()

//                        /* AWS */
//                        .requestMatchers(HttpMethod.GET, "/security/health").permitAll()

                        .requestMatchers("/security/admin").hasRole("ADMIN")

                        .anyRequest().authenticated());

        // 무상태 세션 설정
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    public JoinFilter joinFilter() {

        return new JoinFilter(userRepository, passwordEncoder, objectMapper);
    }

    @Bean
    public JwtReissueFilter jwtReissueFilter() {

        return new JwtReissueFilter(jwtUtil, redisTemplate, objectMapper);
    }

    @Bean
    public JwtLogoutFilter jwtLogoutFilter() {

        return new JwtLogoutFilter(jwtUtil, redisTemplate, objectMapper);
    }
}

