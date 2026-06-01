package com.example.bank.service.lock;

import com.example.bank.api.error.BusinessException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Component
@ConditionalOnMissingBean(name = "redisDistributedLockExecutor")
public class InMemoryDistributedLockExecutor implements DistributedLockExecutor {

    private final ConcurrentHashMap<String, ReentrantLock> locks = new ConcurrentHashMap<>();

    @Override
    public void executeWithLock(String lockKey, Runnable action) {
        ReentrantLock lock = locks.computeIfAbsent(lockKey, key -> new ReentrantLock());
        boolean acquired = lock.tryLock();
        if (!acquired) {
            throw new BusinessException(HttpStatus.CONFLICT, "Operation is already in progress");
        }
        try {
            action.run();
        } finally {
            lock.unlock();
        }
    }
}
