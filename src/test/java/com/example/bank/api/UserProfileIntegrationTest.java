package com.example.bank.api;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("it")
class UserProfileIntegrationTest {

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

    @Test
    void getProfileReturnsBalanceAndContacts() throws Exception {
        String token = AuthTestHelper.loginByEmail(mockMvc, objectMapper, "maria@example.com", "password123");

        mockMvc.perform(get("/api/users/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.balance").value(500.00))
                .andExpect(jsonPath("$.dateOfBirth").value("22.11.1985"))
                .andExpect(jsonPath("$.emails").isArray())
                .andExpect(jsonPath("$.phones").isArray());
    }

    @Test
    void cannotDeleteLastEmail() throws Exception {
        String token = AuthTestHelper.loginByEmail(mockMvc, objectMapper, "maria@example.com", "password123");

        mockMvc.perform(delete("/api/users/me/emails/3").header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addEmailConflictWhenAlreadyUsed() throws Exception {
        String token = AuthTestHelper.loginByEmail(mockMvc, objectMapper, "ivan@example.com", "password123");

        mockMvc.perform(post("/api/users/me/emails")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"maria@example.com\"}"))
                .andExpect(status().isConflict());
    }
}
