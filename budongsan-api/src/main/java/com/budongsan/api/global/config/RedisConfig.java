package com.budongsan.api.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 설정
 *
 * RedisTemplate → Redis에 데이터를 읽고 쓰는 도구
 * Key/Value 모두 String으로 직렬화 (사람이 읽기 쉽게)
 *
 * 사용 예시:
 * redisTemplate.opsForValue().set("RT:jimmy@email.com", "refreshToken", 7일);
 * redisTemplate.opsForValue().get("RT:jimmy@email.com");
 * redisTemplate.delete("RT:jimmy@email.com"); // 로그아웃 시
 */
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Key, Value 모두 String으로 직렬화
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());

        return template;
    }
}
