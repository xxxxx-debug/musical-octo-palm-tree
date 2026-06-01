package com.example.bank.api.dto;

import com.example.bank.util.ValidationPatterns;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
public class ContactPhoneRequest {

    @NotBlank(message = "phone is required")
    @Pattern(regexp = ValidationPatterns.PHONE, message = "phone must be 13 digits starting with 7")
    @Size(min = 13, max = 13, message = "phone must be 13 characters")
    private String phone;
}
