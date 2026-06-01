package com.example.bank.api;

import com.example.bank.api.dto.AuthenticatedUserResponse;
import com.example.bank.api.dto.LoginRequest;
import com.example.bank.api.dto.LoginResponse;
import com.example.bank.security.SecurityUtils;
import com.example.bank.security.UserPrincipal;
import com.example.bank.service.AuthService;
import com.example.bank.dao.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@ConditionalOnBean(UserRepository.class)
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        String token = authService.login(request);
        return ResponseEntity.ok(new LoginResponse(token));
    }

    /**
     * Protected endpoint to verify JWT (used in tests and for debugging).
     */
    @GetMapping("/me")
    public ResponseEntity<AuthenticatedUserResponse> me(@AuthenticationPrincipal UserPrincipal principal) {
        Long userId = principal != null ? principal.getUserId() : SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(new AuthenticatedUserResponse(userId));
    }
}
