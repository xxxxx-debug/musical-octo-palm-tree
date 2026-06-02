package com.example.bank.api.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class ContactEmailRequest {

    @NotBlank(message = "email is required")
    @Email(message = "invalid email format")
    @Size(max = 200, message = "email must be at most 200 characters")
    private String email;
}
