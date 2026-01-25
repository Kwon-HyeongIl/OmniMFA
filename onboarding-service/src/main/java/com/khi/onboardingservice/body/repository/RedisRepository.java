package com.khi.onboardingservice.body.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RedisRepository {

    private final StringRedisTemplate redisTemplate;

    public void saveProductAuth(String productId, String hashedProductSecret) {
        redisTemplate.opsForValue().set(productId, hashedProductSecret);
    }
}
