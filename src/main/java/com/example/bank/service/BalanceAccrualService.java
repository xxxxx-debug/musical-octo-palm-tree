package com.example.bank.service;

import com.example.bank.dao.AccountDao;
import com.example.bank.dao.AccountRepository;
import com.example.bank.domain.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@Profile("!local")
@RequiredArgsConstructor
public class BalanceAccrualService {

    private static final BigDecimal ACCRUAL_RATE = new BigDecimal("1.10");

    private final AccountRepository accountRepository;
    private final AccountDao accountDao;

    @Transactional
    public int processAccrual() {
        long started = System.currentTimeMillis();
        List<Account> accounts = accountRepository.findAll();
        int updated = 0;

        for (Account account : accounts) {
            Account locked = accountRepository.findAllByIdInForUpdate(
                    Collections.singletonList(account.getId())).stream()
                    .findFirst()
                    .orElse(null);
            if (locked == null) {
                continue;
            }
            if (applyAccrual(locked)) {
                accountDao.save(locked);
                updated++;
            }
        }

        long durationMs = System.currentTimeMillis() - started;
        log.info("Balance accrual finished: updatedAccounts={}, durationMs={}", updated, durationMs);
        return updated;
    }

    boolean applyAccrual(Account account) {
        BigDecimal balance = account.getBalance();
        BigDecimal maxBalance = account.getMaxBalance();

        if (balance.compareTo(maxBalance) >= 0) {
            return false;
        }

        BigDecimal increased = balance.multiply(ACCRUAL_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal newBalance = increased.min(maxBalance);

        if (newBalance.compareTo(balance) <= 0) {
            return false;
        }

        account.setBalance(newBalance);
        return true;
    }
}
