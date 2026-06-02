package com.example.bank.api.dto;

import lombok.Value;

import java.math.BigDecimal;

@Value
public class TransferResponse {

    Long fromUserId;
    Long toUserId;
    BigDecimal amount;
    String status;
}
