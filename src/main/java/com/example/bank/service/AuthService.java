package com.example.bank.service;

import com.example.bank.api.dto.LoginRequest;
import com.example.bank.dao.UserRepository;
import com.example.bank.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@ConditionalOnBean(UserRepository.class)
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional(readOnly = true)
    public String login(LoginRequest request) {
        User user = resolveUser(request).orElse(null);

        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Login failed for identifier={}", maskIdentifier(request));
            throw new BadCredentialsException("Invalid credentials");
        }

        log.info("Login successful for userId={}", user.getId());
        return jwtService.generateToken(user.getId());
    }

    private java.util.Optional<User> resolveUser(LoginRequest request) {
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            return userRepository.findByEmail(request.getEmail().trim());
        }
        return userRepository.findByPhone(request.getPhone().trim());
    }

    private String maskIdentifier(LoginRequest request) {
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            return "email:" + request.getEmail().trim();
        }
        if (request.getPhone() != null && !request.getPhone().isBlank()) {
            String phone = request.getPhone().trim();
            return "phone:" + phone.substring(0, Math.min(4, phone.length())) + "***";
        }
        return "unknown";
    }
}
