package com.example.bank.dao;

import com.example.bank.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Component
@ConditionalOnBean(AccountRepository.class)
@RequiredArgsConstructor
public class AccountDao {

    private final AccountRepository accountRepository;

    public Optional<Account> findByUserId(Long userId) {
        return accountRepository.findByUser_Id(userId);
    }

    public Optional<Account> findByUserIdForUpdate(Long userId) {
        return accountRepository.findByUserIdForUpdate(userId);
    }

    /**
     * Locks accounts in ascending id order to avoid deadlocks during transfers.
     */
    public List<Account> findByIdsForUpdateOrdered(Long firstId, Long secondId) {
        List<Long> ids = new ArrayList<>();
        ids.add(firstId);
        if (!firstId.equals(secondId)) {
            ids.add(secondId);
        }
        ids.sort(Comparator.naturalOrder());
        return accountRepository.findAllByIdInForUpdate(ids);
    }

    public Account save(Account account) {
        return accountRepository.save(account);
    }

    public List<Account> findAll() {
        return accountRepository.findAll();
    }
}
