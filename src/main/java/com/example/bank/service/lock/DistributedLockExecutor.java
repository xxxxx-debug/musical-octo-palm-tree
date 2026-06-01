package com.example.bank.service.lock;

public interface DistributedLockExecutor {

    void executeWithLock(String lockKey, Runnable action);
}
