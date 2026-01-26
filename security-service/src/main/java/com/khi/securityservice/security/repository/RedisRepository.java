package com.khi.securityservice.security.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RedisRepository {

    private final StringRedisTemplate redisTemplate;

    private static final String KEY_PREFIX = "auth:refresh:";
    private static final long REFRESH_TOKEN_TTL = 86_400_000L; // 24시간

    public void saveRefreshToken(String uid, String refreshToken) {

        redisTemplate.opsForValue().set(KEY_PREFIX + uid, refreshToken, REFRESH_TOKEN_TTL, TimeUnit.MILLISECONDS);
        log.info("Redis에 새로운 Refresh 토큰 저장 완료");
    }

    public String getRefreshToken(String uid) {
        return redisTemplate.opsForValue().get(KEY_PREFIX + uid);
    }

    public void deleteRefreshToken(String uid) {

        redisTemplate.delete(KEY_PREFIX + uid);
        log.info("Redis에서 기존 Refresh 토큰 삭제 완료");
    }
}
