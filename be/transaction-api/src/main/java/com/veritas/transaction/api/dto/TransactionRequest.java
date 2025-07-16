package com.veritas.transaction.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) class that represents the request payload for a transaction.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequest {
    private String userId; // Auth0 user ID (set in backend)
    private String sourceAccountId; // For transfers
    private String destinationAccountId; // For transfers
    private String type; // deposit, withdrawal, transfer
    private String assetCode; // Asset involved in the transaction
    private java.math.BigDecimal amount; // Amount for the transaction
}
