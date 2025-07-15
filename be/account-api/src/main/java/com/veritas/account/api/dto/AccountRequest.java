package com.veritas.account.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * Data Transfer Object (DTO) class that represents the request payload for a bank account holder.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountRequest {
    private String accountHolderName;
    private String accountName;
    private BigDecimal balance;
    private Currency currency;
    private String userId; // Auth0 user ID (set in backend)
}
