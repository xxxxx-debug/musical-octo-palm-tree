package com.example.bank.api;

import com.example.bank.dao.AccountDao;
import com.example.bank.support.AuthTestHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("it")
class TransferIntegrationTest {

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
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccountDao accountDao;

    @Test
    void successfulTransfer() throws Exception {
        String token = AuthTestHelper.loginByEmail(mockMvc, objectMapper, "ivan@example.com", "password123");

        BigDecimal ivanBefore = accountDao.findByUserId(1L).orElseThrow().getBalance();
        BigDecimal mariaBefore = accountDao.findByUserId(2L).orElseThrow().getBalance();

        mockMvc.perform(post("/api/transfers")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"toUserId\":2,\"amount\":10.50}"))
                .andExpect(status().isOk());

        assertThat(accountDao.findByUserId(1L).orElseThrow().getBalance())
                .isEqualByComparingTo(ivanBefore.subtract(new BigDecimal("10.50")));
        assertThat(accountDao.findByUserId(2L).orElseThrow().getBalance())
                .isEqualByComparingTo(mariaBefore.add(new BigDecimal("10.50")));
    }

    @Test
    void transferToSelfIsRejected() throws Exception {
        String token = AuthTestHelper.loginByEmail(mockMvc, objectMapper, "ivan@example.com", "password123");

        mockMvc.perform(post("/api/transfers")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"toUserId\":1,\"amount\":1.00}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void insufficientFunds() throws Exception {
        String token = AuthTestHelper.loginByEmail(mockMvc, objectMapper, "ivan@example.com", "password123");

        mockMvc.perform(post("/api/transfers")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"toUserId\":2,\"amount\":999999.99}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void parallelTransfersDoNotDriveBalanceNegative() throws Exception {
        String token = AuthTestHelper.loginByEmail(mockMvc, objectMapper, "alex@example.com", "password123");
        BigDecimal startBalance = accountDao.findByUserId(3L).orElseThrow().getBalance();

        ExecutorService executor = Executors.newFixedThreadPool(5);
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        try {
            Future<?>[] futures = new Future[5];
            for (int i = 0; i < 5; i++) {
                futures[i] = executor.submit(() -> {
                    try {
                        int status = mockMvc.perform(post("/api/transfers")
                                        .header("Authorization", "Bearer " + token)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content("{\"toUserId\":1,\"amount\":300.00}"))
                                .andReturn()
                                .getResponse()
                                .getStatus();
                        if (status == 200) {
                            successCount.incrementAndGet();
                        } else {
                            failCount.incrementAndGet();
                        }
                    } catch (Exception ex) {
                        failCount.incrementAndGet();
                    }
                });
            }
            for (Future<?> future : futures) {
                future.get(30, TimeUnit.SECONDS);
            }
        } finally {
            executor.shutdownNow();
        }

        BigDecimal endBalance = accountDao.findByUserId(3L).orElseThrow().getBalance();
        assertThat(endBalance).isGreaterThanOrEqualTo(BigDecimal.ZERO);
        assertThat(successCount.get() + failCount.get()).isEqualTo(5);
        assertThat(endBalance).isEqualByComparingTo(
                startBalance.subtract(new BigDecimal("300.00").multiply(new BigDecimal(successCount.get()))));
    }
}
