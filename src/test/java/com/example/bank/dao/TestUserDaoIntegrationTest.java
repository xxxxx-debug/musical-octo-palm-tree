package com.example.bank.dao;

import com.example.bank.dao.support.TestUserDao;
import com.example.bank.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest
@ActiveProfiles("it")
class TestUserDaoIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("bank")
            .withUsername("bank")
            .withPassword("bank");

    @DynamicPropertySource
    static void registerDataSource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private TestUserDao testUserDao;

    @Autowired
    private AccountDao accountDao;

    @Test
    void createsUserWithAccountEmailAndPhone() {
        User user = testUserDao.createUser(
                "Test User",
                LocalDate.of(2000, 1, 1),
                "password123",
                new BigDecimal("250.00"),
                "test.user@example.com",
                "79209999999"
        );

        assertThat(user.getId()).isNotNull();
        assertThat(accountDao.findByUserId(user.getId())).isPresent()
                .get()
                .satisfies(account -> {
                    assertThat(account.getBalance()).isEqualByComparingTo("250.00");
                    assertThat(account.getMaxBalance()).isEqualByComparingTo("517.50");
                });
    }
}
