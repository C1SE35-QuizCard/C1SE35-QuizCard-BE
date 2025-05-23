package com.example.quizcards.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class RedisUtils {
    private final RedisTemplate<String, Object> redisTemplate;

    private final ObjectMapper objectMapper;

    public RedisUtils(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule()); // Hỗ trợ LocalDateTime
    }

    // Phương thức generic để deserialize dữ liệu từ Redis thành bất kỳ kiểu nào
    public <T> T getFromRedis(String key, Class<T> clazz) {
        Object data = redisTemplate.opsForValue().get(key);
        if (data == null) {
            return null;
        }
        return objectMapper.convertValue(data, clazz);
    }

    public Object getFromRedis(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    // Phương thức lưu dữ liệu vào Redis (không cần generic vì RedisTemplate đã xử lý)
    public void saveToRedis(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    public void saveToSet(String key, Object value) {
        redisTemplate.opsForSet().add(key, value);
    }

    public void saveToSet(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForSet().add(key, value);
        if (timeout > 0) {
            redisTemplate.expire(key, timeout, unit);
        }
    }

    public <T> T popFromSet(String key, Class<T> clazz) {
        Object data = redisTemplate.opsForSet().pop(key);
        if (data == null) {
            return null;
        }
        return objectMapper.convertValue(data, clazz);
    }

    public <T> Set<T> getFromSet(String key, Class<T> clazz) {
        // Lấy toàn bộ members (O(M) với M = số phần tử trong set)
        Set<Object> rawMembers = redisTemplate.opsForSet().members(key);
        if (rawMembers == null || rawMembers.isEmpty()) {
            return Collections.emptySet();
        }
        // Chuyển kiểu từng phần tử về T
        return rawMembers.stream()
                .map(item -> objectMapper.convertValue(item, clazz))
                .collect(Collectors.toSet());
    }

    public boolean isMember(String key, Object value) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, value));
    }

    // Phương thức kiểm tra xem key có tồn tại trong Redis không
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void deleteKey(String key) {
        redisTemplate.delete(key);
    }

    public void deleteKeys(String... keys) {
        redisTemplate.delete(List.of(keys));
    }

    public void deleteKeysWithPattern(String pattern) {
        redisTemplate.delete(Objects.requireNonNull(redisTemplate.keys(pattern)));
    }

    public List<Object> multiGet(List<String> keys) {
        return redisTemplate.opsForValue().multiGet(keys);
    }
}
