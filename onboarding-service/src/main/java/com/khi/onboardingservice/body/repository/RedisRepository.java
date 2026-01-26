package com.khi.onboardingservice.body.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RedisRepository {

    private final StringRedisTemplate redisTemplate;

    public void saveProductAuth(String productId, String hashedProductSecret) {

        redisTemplate.opsForValue().set(productId, hashedProductSecret);
        log.info("Redis에 새로운 제품 인증 정보 저장 완료");
    }
}
