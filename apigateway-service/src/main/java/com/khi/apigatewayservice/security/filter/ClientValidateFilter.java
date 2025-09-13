package com.khi.apigatewayservice.security.filter;

import com.khi.apigatewayservice.security.entity.ProductSecretEntity;
import com.khi.apigatewayservice.security.repository.ProductSecretRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClientValidateFilter implements GlobalFilter {

    private final ProductSecretRepository productSecretRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private static final Map<String, Set<HttpMethod>> VALIDATE_PATHS = Map.ofEntries(

            Map.entry("/totp/setup", Set.of(HttpMethod.POST)),
            Map.entry("/totp/verify", Set.of(HttpMethod.POST))
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getRawPath();
        HttpMethod method = exchange.getRequest().getMethod();

        if (!isValidationRequired(path, method)) {

            log.info("ClientValidateFilter - 허용된 URL: {} {}", method, path);

            return chain.filter(exchange);
        }

        log.info("ClientValidateFilter 실행");

        String clientId = exchange.getRequest().getHeaders().getFirst("OmniMFA-Client-Id");
        String clientSecret = exchange.getRequest().getHeaders().getFirst("OmniMFA-Client-Secret");

        if (clientId == null || clientSecret == null) {

            throw new RuntimeException("클라이언트 키가 비었습니다.");
        }

        ProductSecretEntity productSecret = productSecretRepository.findByProductClientId(clientId)
                .orElseThrow(() -> new RuntimeException("일치하는 클라이언트 ID가 존재하지 않습니다."));

        boolean result = bCryptPasswordEncoder.matches(clientSecret, productSecret.getProductHashedClientSecret());

        if (result) {

            log.info("클라이언트 검증 성공");
            return chain.filter(exchange);
        } else {

            throw new RuntimeException("클라이언트 키가 일치하지 않습니다.");
        }
    }

    private boolean isValidationRequired(String path, HttpMethod method) {

        if (VALIDATE_PATHS.getOrDefault(path, Collections.emptySet()).contains(method)) {

            return true;
        }

        return false;
    }
}
