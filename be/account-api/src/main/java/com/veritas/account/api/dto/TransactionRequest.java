package com.veritas.account.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequest {
    private String userId;
    private String sourceAccountId;
    private String destinationAccountId;
    private String type;
    private String assetCode;
    private BigDecimal amount;
} 