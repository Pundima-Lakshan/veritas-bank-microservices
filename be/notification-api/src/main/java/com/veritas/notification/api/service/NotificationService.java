package com.veritas.notification.api.service;

import com.veritas.notification.api.client.AccountClient;
import com.veritas.notification.api.dto.AccountResponse;
import com.veritas.notification.api.event.TransactionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * Service class for handling notification logic.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;
    private final AccountClient accountClient;

    /**
     * Sends notifications to all users involved in a transaction.
     * 
     * @param transactionEvent The transaction event containing transaction details.
     */
    public void sendTransactionNotifications(TransactionEvent transactionEvent) {
        Set<String> userIdsToNotify = new HashSet<>();
        
        // Always add the user who initiated the transaction
        if (transactionEvent.getUserId() != null && !transactionEvent.getUserId().isEmpty()) {
            userIdsToNotify.add(transactionEvent.getUserId());
        }
        
        // For transfers, add the destination account owner
        if ("transfer".equalsIgnoreCase(transactionEvent.getType()) && 
            transactionEvent.getDestinationAccountId() != null) {
            try {
                AccountResponse destAccount = accountClient.getAccountById(transactionEvent.getDestinationAccountId());
                if (destAccount != null && destAccount.getUserId() != null) {
                    userIdsToNotify.add(destAccount.getUserId());
                }
            } catch (Exception e) {
                log.warn("Failed to fetch destination account details for notification: {}", 
                        transactionEvent.getDestinationAccountId(), e);
            }
        }
        
        // For deposits, add the destination account owner (if different from initiator)
        if ("deposit".equalsIgnoreCase(transactionEvent.getType()) && 
            transactionEvent.getDestinationAccountId() != null) {
            try {
                AccountResponse destAccount = accountClient.getAccountById(transactionEvent.getDestinationAccountId());
                if (destAccount != null && destAccount.getUserId() != null && 
                    !destAccount.getUserId().equals(transactionEvent.getUserId())) {
                    userIdsToNotify.add(destAccount.getUserId());
                }
            } catch (Exception e) {
                log.warn("Failed to fetch destination account details for notification: {}", 
                        transactionEvent.getDestinationAccountId(), e);
            }
        }
        
        // Send notifications to all relevant users
        for (String userId : userIdsToNotify) {
            try {
                messagingTemplate.convertAndSend("/topic/notifications/" + userId, transactionEvent);
                log.info("Sent notification for transaction {} to user {}", 
                        transactionEvent.getTransactionId(), userId);
            } catch (Exception e) {
                log.error("Failed to send notification to user {} for transaction {}", 
                        userId, transactionEvent.getTransactionId(), e);
            }
        }
    }
} 