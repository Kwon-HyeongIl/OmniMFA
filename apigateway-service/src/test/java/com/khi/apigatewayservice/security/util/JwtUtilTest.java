package com.khi.apigatewayservice.security.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class JwtUtilTest {

    private static final String secret = "0123456789012345678901234567890123456789";
    private SecretKey key;
    private JwtUtil util;

    @BeforeEach
    void setUp() {

        key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        util = new JwtUtil(secret);
    }

    @Test
    @DisplayName("유효한 토큰 검증 통과 여부")
    void isExpiredTest_validToken() {

        String validToken = Jwts.builder()
                .claim("tokenType", "ACCESS")
                .claim("uid", "u-123")
                .claim("role", "ROLE_USER")
                .expiration(new Date(System.currentTimeMillis() + 5_000))
                .signWith(key)
                .compact();

        assertThat(util.isExpired(validToken)).isFalse();
    }

    @Test
    @DisplayName("만료된 토큰 검증 실패 여부")
    void isExpiredTest_expiredToken() {

        String expiredToken = Jwts.builder()
                .claim("tokenType", "ACCESS")
                .claim("uid", "u-123")
                .claim("role", "ROLE_USER")
                .expiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(key)
                .compact();

        assertThat(util.isExpired(expiredToken)).isTrue();
    }
}
