package com.newstrike.prj.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.Duration;

@RequiredArgsConstructor
@Service
public class RedisUtil {
    private final StringRedisTemplate template;

    public String getData(String key) {
        Assert.notNull(key, "Key must not be null");
        ValueOperations<String, String> valueOperations = template.opsForValue();
        return valueOperations.get(key);
    }

    public boolean existData(String key) {
        Assert.notNull(key, "Key must not be null");
        return Boolean.TRUE.equals(template.hasKey(key));
    }

    public void setDataExpire(String key, String value, long duration) {
        Assert.notNull(key, "Key must not be null");
        ValueOperations<String, String> valueOperations = template.opsForValue();
        Duration expireDuration = Duration.ofSeconds(duration);
        valueOperations.set(key, value, expireDuration);
    }

    public void deleteData(String key) {
        Assert.notNull(key, "Key must not be null");
        template.delete(key);
    }
}