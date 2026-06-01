package com.example.bank.api.dto;

import com.example.bank.util.ValidationPatterns;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
public class LoginRequest {

    @Email(message = "invalid email format")
    @Size(max = 200, message = "email must be at most 200 characters")
    private String email;

    @Pattern(regexp = ValidationPatterns.PHONE, message = "phone must be 13 digits starting with 7")
    @Size(min = 13, max = 13, message = "phone must be 13 characters")
    private String phone;

    @NotBlank(message = "password is required")
    @Size(min = 8, max = 500, message = "password must be between 8 and 500 characters")
    private String password;

    @AssertTrue(message = "either email or phone must be provided")
    public boolean isEmailOrPhonePresent() {
        boolean hasEmail = email != null && !email.isBlank();
        boolean hasPhone = phone != null && !phone.isBlank();
        return hasEmail ^ hasPhone;
    }
}
