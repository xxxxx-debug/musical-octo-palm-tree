package com.example.bank.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {

    /**
     * HMAC secret (at least 32 characters for HS256).
     */
    private String secret = "change-me-change-me-change-me-change-me";

    private int expirationHours = 24;
}
