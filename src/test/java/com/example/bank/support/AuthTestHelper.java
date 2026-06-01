package com.example.bank.support;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public final class AuthTestHelper {

    private AuthTestHelper() {
    }

    public static String loginByEmail(MockMvc mockMvc, ObjectMapper objectMapper, String email, String password)
            throws Exception {
        return login(mockMvc, objectMapper,
                "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}");
    }

    private static String login(MockMvc mockMvc, ObjectMapper objectMapper, String body) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return json.get("token").asText();
    }
}
