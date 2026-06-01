package com.example.bank.service;

import com.example.bank.domain.Account;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class BalanceAccrualServiceTest {

    private final BalanceAccrualService service = new BalanceAccrualService(null, null);

    @Test
    void applyAccrualIncreasesBalanceByTenPercent() {
        Account account = account(new BigDecimal("100.00"), new BigDecimal("207.00"));

        assertThat(service.applyAccrual(account)).isTrue();
        assertThat(account.getBalance()).isEqualByComparingTo("110.00");
    }

    @Test
    void applyAccrualDoesNotExceedMaxBalance() {
        Account account = account(new BigDecimal("200.00"), new BigDecimal("207.00"));

        assertThat(service.applyAccrual(account)).isTrue();
        assertThat(account.getBalance()).isEqualByComparingTo("207.00");
    }

    @Test
    void applyAccrualSkipsWhenAlreadyAtCap() {
        Account account = account(new BigDecimal("207.00"), new BigDecimal("207.00"));

        assertThat(service.applyAccrual(account)).isFalse();
        assertThat(account.getBalance()).isEqualByComparingTo("207.00");
    }

    private Account account(BigDecimal balance, BigDecimal maxBalance) {
        Account account = new Account();
        account.setBalance(balance);
        account.setMaxBalance(maxBalance);
        return account;
    }
}
