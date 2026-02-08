package com.khi.apigatewayservice.body.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class RedisRepository {

    private final ReactiveStringRedisTemplate reactiveRedisTemplate;

    public Mono<String> getHashedSecretByProductId(String productId) {
        return reactiveRedisTemplate.opsForValue().get("product:auth:" + productId);
    }

    public Mono<Boolean> saveHashedSecretByProductId(String productId, String hashedSecret) {
        return reactiveRedisTemplate.opsForValue().set("product:auth:" + productId, hashedSecret);
    }
}
