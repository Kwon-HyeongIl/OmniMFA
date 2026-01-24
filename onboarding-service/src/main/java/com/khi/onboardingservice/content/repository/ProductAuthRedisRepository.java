package com.khi.onboardingservice.content.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductAuthRedisRepository {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String KEY_PREFIX = "product:auth:";

    public void saveProductAuth(String productClientId, String hashedClientSecret) {
        String key = KEY_PREFIX + productClientId;
        redisTemplate.opsForValue().set(key, hashedClientSecret);
    }
}
