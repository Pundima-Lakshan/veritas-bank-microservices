package com.veritas.account.api.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class DebitCreditRequest {
    private BigDecimal amount;
} 