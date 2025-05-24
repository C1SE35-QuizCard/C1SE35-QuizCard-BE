package com.example.quizcards.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.lettuce.core.RedisException;
import org.aopalliance.intercept.MethodInterceptor;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        // Tạo ObjectMapper và đăng ký JavaTimeModule
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Sử dụng ObjectMapper đã cấu hình trong GenericJackson2JsonRedisSerializer
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);

        ProxyFactory pf = new ProxyFactory(template);
        pf.setProxyTargetClass(true);
        pf.addAdvice((MethodInterceptor) inv -> {
            try {
                return inv.proceed();
            } catch (RedisConnectionFailureException | RedisException ex) {
                LoggerFactory.getLogger(RedisConfig.class)
                        .warn("Redis down, skip {}: {}",
                                inv.getMethod().getName(), ex.getMessage());
                return null;
            }
        });

        return (RedisTemplate<String, Object>) pf.getProxy();
//        return template;
    }

    @Bean
    public RedisMessageListenerContainer listenerContainer(RedisConnectionFactory cf) {
        RedisMessageListenerContainer c = new RedisMessageListenerContainer();
        c.setConnectionFactory(cf);
        return c;
    }
}