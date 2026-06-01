package com.example.bank.dao.spec;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class UserSearchCriteria {

    LocalDate dateOfBirthAfter;
    String phone;
    String namePrefix;
    String email;
}
