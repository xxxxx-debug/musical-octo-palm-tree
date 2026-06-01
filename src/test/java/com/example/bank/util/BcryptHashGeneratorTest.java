package com.example.bank.util;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

class BcryptHashGeneratorTest {

    @Test
    @Disabled("Run manually to print seed password hash")
    void printHash() {
        System.out.println(new BCryptPasswordEncoder().encode("password123"));
    }
}
