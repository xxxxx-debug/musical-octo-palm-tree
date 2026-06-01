package com.example.bank.dao.support;

import com.example.bank.dao.AccountRepository;
import com.example.bank.dao.EmailDataRepository;
import com.example.bank.dao.PhoneDataRepository;
import com.example.bank.dao.UserRepository;
import com.example.bank.domain.Account;
import com.example.bank.domain.EmailData;
import com.example.bank.domain.PhoneData;
import com.example.bank.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

/**
 * Creates users directly in DB for integration tests (per FSD). Active only in {@code it} profile.
 */
@Component
@Profile("it")
@RequiredArgsConstructor
public class TestUserDao {

    private static final BigDecimal CAP_MULTIPLIER = new BigDecimal("2.07");

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final EmailDataRepository emailDataRepository;
    private final PhoneDataRepository phoneDataRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User createUser(String name,
                           LocalDate dateOfBirth,
                           String rawPassword,
                           BigDecimal initialBalance,
                           String email,
                           String phone) {
        User user = new User();
        user.setName(name);
        user.setDateOfBirth(dateOfBirth);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user = userRepository.save(user);

        Account account = new Account();
        account.setUser(user);
        account.setBalance(initialBalance);
        account.setMaxBalance(initialBalance.multiply(CAP_MULTIPLIER).setScale(2, RoundingMode.HALF_UP));
        accountRepository.save(account);

        EmailData emailData = new EmailData();
        emailData.setUser(user);
        emailData.setEmail(email);
        emailDataRepository.save(emailData);

        PhoneData phoneData = new PhoneData();
        phoneData.setUser(user);
        phoneData.setPhone(phone);
        phoneDataRepository.save(phoneData);

        return user;
    }
}
