package com.veritas.notification.api.event;

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
    private String sourceAccountId;
    private String destinationAccountId;
    private String type;
    private String amount;
    private String assetCode;
}
