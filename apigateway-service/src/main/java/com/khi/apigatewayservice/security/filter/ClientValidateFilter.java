package com.khi.apigatewayservice.security.filter;

import com.khi.apigatewayservice.security.repository.ProductAuthRedisRepository;
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

    private final ProductAuthRedisRepository productAuthRedisRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private static final Map<String, Set<HttpMethod>> VALIDATE_PATHS = Map.ofEntries(

            Map.entry("/totp/setup", Set.of(HttpMethod.POST)),
            Map.entry("/totp/verify", Set.of(HttpMethod.POST)));

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getRawPath();
        HttpMethod method = exchange.getRequest().getMethod();

        if (!isValidationRequired(path, method)) {

            log.info("ClientValidateFilter - 허용된 URL: {} {}", method, path);

            return chain.filter(exchange);
        }

        log.info("ClientValidateFilter 실행");

        String productId = exchange.getRequest().getHeaders().getFirst("product-id");
        String productSecret = exchange.getRequest().getHeaders().getFirst("product-secret");

        if (productId == null || productSecret == null) {

            throw new RuntimeException("제품 인증 정보가 비었습니다.");
        }

        return productAuthRedisRepository.getHashedSecretByProductId(productId)
                .switchIfEmpty(Mono.error(new RuntimeException("일치하는 제품 ID가 존재하지 않습니다.")))
                .flatMap(hashedSecret -> {
                    boolean result = bCryptPasswordEncoder.matches(productSecret, hashedSecret);
                    if (result) {
                        log.info("제품 인증 성공");
                        return chain.filter(exchange);
                    } else {
                        return Mono.error(new RuntimeException("제품 Secret이 일치하지 않습니다."));
                    }
                });
    }

    private boolean isValidationRequired(String path, HttpMethod method) {

        if (VALIDATE_PATHS.getOrDefault(path, Collections.emptySet()).contains(method)) {

            return true;
        }

        return false;
    }
}
