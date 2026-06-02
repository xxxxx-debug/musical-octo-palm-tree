package com.example.bank.api.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Prepared for transfer API (stage 9).
 */
@Getter
@Setter
public class TransferRequest {

    @NotNull(message = "toUserId is required")
    private Long toUserId;

    @NotNull(message = "amount is required")
    @DecimalMin(value = "0.01", message = "amount must be at least 0.01")
    @Digits(integer = 17, fraction = 2, message = "amount must have at most 2 decimal places")
    private BigDecimal amount;
}
