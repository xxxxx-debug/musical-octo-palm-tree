package com.example.bank.api.dto;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class UserSearchItemResponse {

    Long id;
    String name;
    String dateOfBirth;
    List<String> emails;
    List<String> phones;
}
