package com.example.bank.service;

import com.example.bank.api.error.BusinessException;
import com.example.bank.dao.AccountDao;
import com.example.bank.domain.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@Profile("!local")
@RequiredArgsConstructor
public class TransferExecutionService {

    private final AccountDao accountDao;

    @Transactional
    public void executeTransfer(Long fromUserId, Long toUserId, BigDecimal amount,
                                Long fromAccountId, Long toAccountId) {
        List<Account> lockedAccounts = accountDao.findByIdsForUpdateOrdered(fromAccountId, toAccountId);
        Map<Long, Account> byId = lockedAccounts.stream()
                .collect(Collectors.toMap(Account::getId, Function.identity()));

        Account fromAccount = byId.get(fromAccountId);
        Account toAccount = byId.get(toAccountId);

        if (fromAccount == null || toAccount == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "Account not found");
        }

        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Insufficient funds");
        }

        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));

        if (fromAccount.getBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Insufficient funds");
        }

        accountDao.save(fromAccount);
        accountDao.save(toAccount);

        log.info("Transfer completed: fromUserId={}, toUserId={}, amount={}", fromUserId, toUserId, amount);
    }
}
