package com.khi.apigatewayservice.body.config;

import io.lettuce.core.ReadFrom;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

/*
 * 쓰기 작업은 master 노드로,
 * 읽기 작업은 slave(replica) 노드로 설정
 */
@Configuration
public class ReactiveRedisConfig {

        /*
         * 스프링 부트의 RedisAutoConfiguration은 RedisConnectionFactory 타입의 빈이 이미 있는지 확인하고, 없으면 RedisConnectionFactory 타입의 빈을 생성.
         * Wenflux에서는 ReactiveRedisConnectionFactory 타입을 사용하지만 이는 RedisConnectionFactory의 상속을 받지 않음. (자동 생성으로 인한 충돌 발생)
         * 따라서, RedisConnectionFactory와 ReactiveRedisConnectionFactory 둘 모두를 상속받는 LettuceConnectionFactory를 반환.
         */
        @Bean
        public LettuceConnectionFactory reactiveRedisConnectionFactory(RedisProperties redisProperties) {

                RedisSentinelConfiguration sentinelConfig = new RedisSentinelConfiguration()
                                .master(redisProperties.getSentinel().getMaster());

                redisProperties.getSentinel().getNodes()
                                .forEach(node -> sentinelConfig.sentinel(node.split(":")[0],
                                                Integer.parseInt(node.split(":")[1])));
                sentinelConfig.setPassword(redisProperties.getPassword());

                LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                                .readFrom(ReadFrom.REPLICA_PREFERRED)
                                .build();

                return new LettuceConnectionFactory(sentinelConfig, clientConfig);
        }
}
