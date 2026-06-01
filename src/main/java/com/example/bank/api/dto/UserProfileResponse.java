package com.example.bank.api.dto;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.util.List;

@Value
@Builder
public class UserProfileResponse {

    Long id;
    String name;
    String dateOfBirth;
    BigDecimal balance;
    List<EmailItemResponse> emails;
    List<PhoneItemResponse> phones;
}
