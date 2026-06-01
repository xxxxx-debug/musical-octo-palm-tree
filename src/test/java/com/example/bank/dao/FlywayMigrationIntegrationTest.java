package com.example.bank.dao;

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

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest
@ActiveProfiles("it")
class FlywayMigrationIntegrationTest {

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
    private UserRepository userRepository;

    @Autowired
    private AccountDao accountDao;

    @Autowired
    private EmailDataRepository emailDataRepository;

    @Test
    void flywayCreatesSchemaAndSeedUsers() {
        assertThat(userRepository.count()).isEqualTo(3);
        assertThat(accountDao.findByUserId(1L)).isPresent()
                .get()
                .satisfies(account -> {
                    assertThat(account.getBalance()).isEqualByComparingTo(new BigDecimal("100.00"));
                    assertThat(account.getMaxBalance()).isEqualByComparingTo(new BigDecimal("207.00"));
                });
        assertThat(emailDataRepository.findByUser_Id(1L)).hasSize(2);
    }
}
