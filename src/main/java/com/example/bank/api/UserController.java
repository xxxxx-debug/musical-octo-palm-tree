package com.example.bank.api;

import com.example.bank.api.dto.ContactEmailRequest;
import com.example.bank.api.dto.ContactPhoneRequest;
import com.example.bank.api.dto.EmailItemResponse;
import com.example.bank.api.dto.PageResponse;
import com.example.bank.api.dto.PhoneItemResponse;
import com.example.bank.api.dto.UserProfileResponse;
import com.example.bank.api.dto.UserSearchItemResponse;
import com.example.bank.dao.UserRepository;
import com.example.bank.security.SecurityUtils;
import com.example.bank.service.UserProfileService;
import com.example.bank.service.UserSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/users")
@ConditionalOnBean(UserRepository.class)
@RequiredArgsConstructor
public class UserController {

    private final UserProfileService userProfileService;
    private final UserSearchService userSearchService;

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getProfile() {
        return ResponseEntity.ok(userProfileService.getProfile(SecurityUtils.getCurrentUserId()));
    }

    @GetMapping("/search")
    public ResponseEntity<PageResponse<UserSearchItemResponse>> search(
            @RequestParam(required = false) String dateOfBirth,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        return ResponseEntity.ok(userSearchService.search(dateOfBirth, phone, name, email, page, size));
    }

    @PostMapping("/me/emails")
    public ResponseEntity<EmailItemResponse> addEmail(@Valid @RequestBody ContactEmailRequest request) {
        EmailItemResponse response = userProfileService.addEmail(SecurityUtils.getCurrentUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/me/emails/{id}")
    public ResponseEntity<EmailItemResponse> updateEmail(@PathVariable Long id,
                                                         @Valid @RequestBody ContactEmailRequest request) {
        return ResponseEntity.ok(userProfileService.updateEmail(SecurityUtils.getCurrentUserId(), id, request));
    }

    @DeleteMapping("/me/emails/{id}")
    public ResponseEntity<Void> deleteEmail(@PathVariable Long id) {
        userProfileService.deleteEmail(SecurityUtils.getCurrentUserId(), id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/me/phones")
    public ResponseEntity<PhoneItemResponse> addPhone(@Valid @RequestBody ContactPhoneRequest request) {
        PhoneItemResponse response = userProfileService.addPhone(SecurityUtils.getCurrentUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/me/phones/{id}")
    public ResponseEntity<PhoneItemResponse> updatePhone(@PathVariable Long id,
                                                         @Valid @RequestBody ContactPhoneRequest request) {
        return ResponseEntity.ok(userProfileService.updatePhone(SecurityUtils.getCurrentUserId(), id, request));
    }

    @DeleteMapping("/me/phones/{id}")
    public ResponseEntity<Void> deletePhone(@PathVariable Long id) {
        userProfileService.deletePhone(SecurityUtils.getCurrentUserId(), id);
        return ResponseEntity.noContent().build();
    }
}
