package com.veritas.transaction.api.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a transaction event between Transaction API and Notification API.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionEvent {
    private String transactionId;
    private String userId;
}
