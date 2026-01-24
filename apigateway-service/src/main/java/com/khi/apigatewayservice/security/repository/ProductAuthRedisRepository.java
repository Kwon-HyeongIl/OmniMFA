package com.khi.apigatewayservice.security.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class ProductAuthRedisRepository {

    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    private static final String KEY_PREFIX = "product:auth:";

    public Mono<String> getHashedSecretByProductId(String productId) {
        String key = KEY_PREFIX + productId;
        return reactiveRedisTemplate.opsForValue().get(key);
    }
}
