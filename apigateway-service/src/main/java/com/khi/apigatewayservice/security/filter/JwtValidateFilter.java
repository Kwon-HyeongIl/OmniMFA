package com.khi.apigatewayservice.security.filter;

import com.khi.apigatewayservice.common.exception.type.JwtException;
import com.khi.apigatewayservice.security.enumeration.JwtTokenType;
import com.khi.apigatewayservice.security.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtValidateFilter implements GlobalFilter {

    private final JwtUtil jwtUtil;

    private static final Map<String, Set<HttpMethod>> ALLOWED_PATHS = Map.ofEntries(
            Map.entry("/security/join", Set.of(HttpMethod.POST)),
            Map.entry("/security/login", Set.of(HttpMethod.POST)),
            Map.entry("/security/logout", Set.of(HttpMethod.POST)),
            Map.entry("/security/jwt/reissue", Set.of(HttpMethod.POST)),

            Map.entry("/swagger-ui/index.html", Set.of(HttpMethod.GET)),
            Map.entry("/swagger-ui/swagger-ui.css", Set.of(HttpMethod.GET)),
            Map.entry("/swagger-ui/index.css", Set.of(HttpMethod.GET)),
            Map.entry("/swagger-ui/swagger-ui-bundle.js", Set.of(HttpMethod.GET)),
            Map.entry("/swagger-ui/swagger-ui-standalone-preset.js", Set.of(HttpMethod.GET)),
            Map.entry("/swagger-ui/swagger-initializer.js", Set.of(HttpMethod.GET)),
            Map.entry("/v3/api-docs/swagger-config", Set.of(HttpMethod.GET)),
            Map.entry("/v3/api-docs", Set.of(HttpMethod.GET)),

            Map.entry("/totp/setup", Set.of(HttpMethod.POST)),
            Map.entry("/totp/verify", Set.of(HttpMethod.POST)),

            Map.entry("/actuator/health", Set.of(HttpMethod.GET))
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getRawPath();
        HttpMethod method = exchange.getRequest().getMethod();

        if (isAllowed(path, method)) {

            log.info("JwtValidateFilter - 허용된 URL: {} {}", method, path);

            return chain.filter(exchange);
        }

        log.info("JwtValidateFilter 실행");

        String fullToken = exchange.getRequest().getHeaders().getFirst("Authorization");
        String accessToken;

        // Access 토큰 존재 유무 및 형식 확인
        if (fullToken != null && fullToken.startsWith("Bearer ")) {

            accessToken = fullToken.substring(7);

        } else {

            throw new JwtException("액세스 토큰이 존재하지 않거나 형식이 잘못 되었습니다.");
        }

        // Access 토큰 만료 여부 확인
        try {

            jwtUtil.isExpired(accessToken);

        } catch (ExpiredJwtException e) {

            throw new JwtException("액세스 토큰이 만료되었습니다.");
        }

        // Access 토큰 타입 확인
        JwtTokenType tokenType = jwtUtil.getTokenType(accessToken);

        if (tokenType != JwtTokenType.ACCESS) {

            throw new JwtException("토큰 타입이 액세스 타입과 일치하지 않습니다.");
        }

        log.info("Access 토큰 검증 완료");

        String uid = jwtUtil.getUid(accessToken);
        String role = jwtUtil.getRole(accessToken);

        // 인증 정보를 마이크로 서비스로 송신
        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .header("Uid", uid)
                .header("Role", role)
                .build();

        ServerWebExchange mutatedExchange = exchange.mutate().request(mutatedRequest).build();

        return chain.filter(mutatedExchange);
    }

    private boolean isAllowed(String path, HttpMethod method) {

        if (ALLOWED_PATHS.getOrDefault(path, Collections.emptySet()).contains(method)) {

            return true;
        }

        return false;
    }
}
