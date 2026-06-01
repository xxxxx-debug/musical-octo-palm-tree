package com.example.bank.scheduler;

import com.example.bank.service.BalanceAccrualService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("!local")
@RequiredArgsConstructor
public class BalanceAccrualScheduler {

    private final BalanceAccrualService balanceAccrualService;

    @Scheduled(fixedRateString = "${app.scheduler.balance-accrual-rate-ms:30000}")
    @SchedulerLock(name = "balanceAccrual", lockAtLeastFor = "PT5S", lockAtMostFor = "PT25S")
    public void accrueBalances() {
        try {
            balanceAccrualService.processAccrual();
        } catch (Exception ex) {
            log.error("Balance accrual failed", ex);
        }
    }
}
