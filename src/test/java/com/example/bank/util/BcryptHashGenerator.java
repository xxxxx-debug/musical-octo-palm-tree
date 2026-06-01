package com.example.bank.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/** One-off helper to print BCrypt hash for seed migrations. */
public class BcryptHashGenerator {

    public static void main(String[] args) {
        System.out.println(new BCryptPasswordEncoder().encode("password123"));
    }
}
