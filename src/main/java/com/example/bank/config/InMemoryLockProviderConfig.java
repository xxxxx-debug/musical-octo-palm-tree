package com.example.bank.config;

import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.core.SimpleLock;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

/**
 * Fallback ShedLock provider when Redis is unavailable (e.g. integration tests).
 */
@Configuration
public class InMemoryLockProviderConfig {

    @Bean
    @ConditionalOnMissingBean(LockProvider.class)
    public LockProvider inMemoryLockProvider() {
        return lockConfiguration -> Optional.of(new SimpleLock() {
            @Override
            public void unlock() {
                // no-op
            }
        });
    }
}
