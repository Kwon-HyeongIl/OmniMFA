package com.khi.onboardingservice.body.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RedisRepository {

    private final StringRedisTemplate redisTemplate;

    private static final String KEY_PREFIX = "product:auth:";

    public void saveProductAuth(String productId, String hashedProductSecret) {
        String key = KEY_PREFIX + productId;
        redisTemplate.opsForValue().set(key, hashedProductSecret);
    }
}
