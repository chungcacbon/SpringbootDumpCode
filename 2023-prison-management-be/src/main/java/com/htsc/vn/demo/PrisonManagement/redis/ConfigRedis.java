package com.htsc.vn.demo.PrisonManagement.redis;

import com.htsc.vn.demo.PrisonManagement.redis.model.CheckingLogDTORedis;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class ConfigRedis {

    @Bean
    public RedisTemplate<String, CheckingLogDTORedis> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, CheckingLogDTORedis> redisTemplates = new RedisTemplate<>();
        redisTemplates.setConnectionFactory(connectionFactory);
        redisTemplates.setKeySerializer(new StringRedisSerializer());
        redisTemplates.setValueSerializer(new Jackson2JsonRedisSerializer<>(CheckingLogDTORedis.class));
        return redisTemplates;
    }
}
