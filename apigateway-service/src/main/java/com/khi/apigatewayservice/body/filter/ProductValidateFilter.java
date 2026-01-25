package com.khi.apigatewayservice.body.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;

import com.khi.apigatewayservice.body.repository.RedisRepository;

import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductValidateFilter implements GlobalFilter {

    private final RedisRepository redisRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final WebClient.Builder webClientBuilder;

    @Value("${onboarding.service.url}")
    private String onboardingServiceUrl;

    private static final Map<String, Set<HttpMethod>> VALIDATE_PATHS = Map.ofEntries(

            Map.entry("/totp/setup", Set.of(HttpMethod.POST)),
            Map.entry("/totp/verify", Set.of(HttpMethod.POST)));

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getRawPath();
        HttpMethod method = exchange.getRequest().getMethod();

        if (!isValidationRequired(path, method)) {

            log.info("ProductValidateFilter - 허용된 URL: {} {}", method, path);
            return chain.filter(exchange);
        }

        log.info("ProductValidateFilter 실행");

        // 헤더에서 제품 인증 정보 추출
        String productId = exchange.getRequest().getHeaders().getFirst("Product-Id");
        String productSecret = exchange.getRequest().getHeaders().getFirst("Product-Secret");

        if (productId == null || productSecret == null) {
            throw new RuntimeException("제품 인증 정보가 비었습니다.");
        }

        // Redis에서 제품 ID에 따른 해시된 제품 키 조회 (캐시 미스 시 onboarding-service에서 조회)
        return getHashedSecretWithFallback(productId)
                .switchIfEmpty(Mono.error(new RuntimeException("일치하는 제품 ID가 존재하지 않습니다.")))
                .flatMap(hashedSecret -> {
                    boolean result = bCryptPasswordEncoder.matches(productSecret, hashedSecret);

                    if (result) {

                        log.info("제품 인증 성공, 제품 아이디: {}", productId);
                        return chain.filter(exchange);

                    } else {

                        log.info("제품 인증 실패, 제품 아이디: {}", productId);
                        return Mono.error(new RuntimeException("제품 Secret이 일치하지 않습니다."));
                    }
                });
    }

    // Redis에서 해시된 제품 시크릿을 조회하고, 캐시 미스 시 onboarding-service에서 조회하여 Redis에 저장
    private Mono<String> getHashedSecretWithFallback(String productId) {

        return redisRepository.getHashedSecretByProductId(productId)
                .doOnNext(secret -> log.info("Redis 캐시 히트, productId: {}", productId))
                .switchIfEmpty(
                        fetchFromOnboardingService(productId)
                                .doOnNext(secret -> log.info("onboarding-service에서 조회 성공, productId: {}", productId))
                                .flatMap(secret -> redisRepository.saveHashedSecretByProductId(productId, secret)
                                        .doOnNext(saved -> log.info("Redis에 캐시 저장 완료, productId: {}", productId))
                                        .thenReturn(secret)));
    }

    // onboarding-service에서 해시된 제품 시크릿 조회
    private Mono<String> fetchFromOnboardingService(String productId) {

        log.info("Redis 캐시 미스, onboarding-service에서 조회, productId: {}", productId);

        return webClientBuilder.build()
                .get()
                .uri(onboardingServiceUrl + "/onboarding/product/internal/{productId}/secret", productId)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(e -> {
                    log.error("onboarding-service 조회 실패, productId: {}, error: {}", productId, e.getMessage());
                    return Mono.empty();
                });
    }

    private boolean isValidationRequired(String path, HttpMethod method) {

        if (VALIDATE_PATHS.getOrDefault(path, Collections.emptySet()).contains(method)) {
            return true;
        }

        return false;
    }
}
