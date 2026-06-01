package com.example.bank.service.lock;

import com.example.bank.api.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.UUID;

@Component("redisDistributedLockExecutor")
@Primary
@ConditionalOnBean(StringRedisTemplate.class)
@RequiredArgsConstructor
public class RedisDistributedLockExecutor implements DistributedLockExecutor {

    private static final Duration LOCK_TTL = Duration.ofSeconds(30);

    private final StringRedisTemplate redisTemplate;

    @Override
    public void executeWithLock(String lockKey, Runnable action) {
        String token = UUID.randomUUID().toString();
        Boolean acquired = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, token, LOCK_TTL);

        if (!Boolean.TRUE.equals(acquired)) {
            throw new BusinessException(HttpStatus.CONFLICT, "Operation is already in progress");
        }

        try {
            action.run();
        } finally {
            String current = redisTemplate.opsForValue().get(lockKey);
            if (token.equals(current)) {
                redisTemplate.delete(lockKey);
            }
        }
    }
}
