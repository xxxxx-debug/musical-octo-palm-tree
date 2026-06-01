package com.example.bank.api.dto;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class AuthenticatedUserResponse {

    Long userId;
}
