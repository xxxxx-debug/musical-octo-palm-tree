package com.example.bank.service;

import com.example.bank.api.error.BusinessException;
import com.example.bank.dao.AccountDao;
import com.example.bank.dao.UserRepository;
import com.example.bank.domain.Account;
import com.example.bank.service.lock.DistributedLockExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Profile("!local")
@RequiredArgsConstructor
public class TransferService {

    private static final BigDecimal MIN_AMOUNT = new BigDecimal("0.01");

    private final UserRepository userRepository;
    private final AccountDao accountDao;
    private final DistributedLockExecutor distributedLockExecutor;
    private final TransferExecutionService transferExecutionService;

    public void transfer(Long fromUserId, Long toUserId, BigDecimal amount) {
        validateRequest(fromUserId, toUserId, amount);

        Account fromPreview = accountDao.findByUserId(fromUserId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Sender account not found"));
        Account toPreview = accountDao.findByUserId(toUserId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Recipient account not found"));

        String lockKey = buildLockKey(fromPreview.getId(), toPreview.getId());
        distributedLockExecutor.executeWithLock(lockKey,
                () -> transferExecutionService.executeTransfer(
                        fromUserId, toUserId, amount, fromPreview.getId(), toPreview.getId()));
    }

    private void validateRequest(Long fromUserId, Long toUserId, BigDecimal amount) {
        if (fromUserId.equals(toUserId)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Cannot transfer to yourself");
        }
        if (amount == null || amount.compareTo(MIN_AMOUNT) < 0) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Minimum transfer amount is 0.01");
        }
        if (amount.scale() > 2) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Amount must have at most 2 decimal places");
        }
        if (!userRepository.existsById(toUserId)) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "Recipient not found");
        }
        if (!userRepository.existsById(fromUserId)) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "Sender not found");
        }
    }

    private String buildLockKey(Long firstAccountId, Long secondAccountId) {
        long min = Math.min(firstAccountId, secondAccountId);
        long max = Math.max(firstAccountId, secondAccountId);
        return "transfer:" + min + ":" + max;
    }
}
