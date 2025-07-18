package com.veritas.transaction.api.service;

import com.veritas.transaction.api.client.AccountClient;
import com.veritas.transaction.api.client.AssetManagementClient;
import com.veritas.transaction.api.dto.AssetManagementResponse;
import com.veritas.transaction.api.dto.TransactionItemsDto;
import com.veritas.transaction.api.dto.TransactionRequest;
import com.veritas.transaction.api.event.TransactionEvent;
import com.veritas.transaction.api.model.Transaction;
import com.veritas.transaction.api.model.TransactionItems;
import com.veritas.transaction.api.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Service class that provides operations for managing transactions.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class TransactionService {

  private final TransactionRepository transactionRepository;
  private final AssetManagementClient assetManagementClient;
  private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;
  private final AccountClient accountClient;
  

  /**
   *
   * Process a transaction based on the provided transaction request.
   * 
   * @param transactionRequest The transaction request containing the necessary
   *                           information.
   * @return A string message indicating the result of the transaction processing.
   * @throws IllegalArgumentException If any of the requested assets are not
   *                                  available.
   */
  @CacheEvict(value = "assetAvailability", allEntries = true)
  public String processTransaction(TransactionRequest transactionRequest) {
    Transaction transaction = new Transaction();
    transaction.setTransactionId(UUID.randomUUID().toString());
    transaction.setUserId(transactionRequest.getUserId());
    transaction.setSourceAccountId(transactionRequest.getSourceAccountId());
    transaction.setDestinationAccountId(transactionRequest.getDestinationAccountId());
    transaction.setType(transactionRequest.getType());
    transaction.setAmount(transactionRequest.getAmount());

    // Asset and amount
    String assetCode = transactionRequest.getAssetCode();
    BigDecimal amount = transactionRequest.getAmount();

    if (assetCode == null || assetCode.isEmpty()) {
      throw new IllegalArgumentException("Asset code is required");
    }
    if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Transaction amount must be positive");
    }

    boolean assetIsAvailable = checkAssetAvailability(assetCode, amount);
    if (!assetIsAvailable) {
      throw new IllegalArgumentException("Asset is not available in the requested amount, please try again later");
    }

    String type = transactionRequest.getType();
    if (type == null) {
      throw new IllegalArgumentException("Transaction type is required");
    }
    // Ownership checks
    String userId = transactionRequest.getUserId();
    String sourceAccountId = transactionRequest.getSourceAccountId();
    String destinationAccountId = transactionRequest.getDestinationAccountId();

    switch (type.toLowerCase()) {
      case "deposit" -> {
        if (destinationAccountId != null) {
          var destAccount = accountClient.getAccountById(destinationAccountId);
          if (destAccount == null || !userId.equals(destAccount.getUserId())) {
            throw new IllegalArgumentException("Destination account does not belong to the user");
          }
        }
        accountClient.creditAccount(destinationAccountId, new AccountClient.DebitCreditRequest(amount));
        assetManagementClient.updateAssetAmount(assetCode, amount.intValue());
      }
      case "withdrawal" -> {
        if (sourceAccountId != null) {
          var srcAccount = accountClient.getAccountById(sourceAccountId);
          if (srcAccount == null || !userId.equals(srcAccount.getUserId())) {
            throw new IllegalArgumentException("Source account does not belong to the user");
          }
        }
        accountClient.debitAccount(sourceAccountId, new AccountClient.DebitCreditRequest(amount));
        assetManagementClient.updateAssetAmount(assetCode, -amount.intValue());
      }
      case "transfer" -> {
        if (sourceAccountId != null) {
          var srcAccount = accountClient.getAccountById(sourceAccountId);
          if (srcAccount == null || !userId.equals(srcAccount.getUserId())) {
            throw new IllegalArgumentException("Source account does not belong to the user");
          }
        }
        accountClient.debitAccount(sourceAccountId, new AccountClient.DebitCreditRequest(amount));
        accountClient.creditAccount(destinationAccountId, new AccountClient.DebitCreditRequest(amount));
      }
      default -> throw new IllegalArgumentException("Invalid transaction type: " + type);
    }

    transactionRepository.save(transaction);
    
    // Create enhanced transaction event with all details for notifications
    TransactionEvent transactionEvent = new TransactionEvent();
    transactionEvent.setTransactionId(transaction.getTransactionId());
    transactionEvent.setUserId(transaction.getUserId());
    transactionEvent.setSourceAccountId(transaction.getSourceAccountId());
    transactionEvent.setDestinationAccountId(transaction.getDestinationAccountId());
    transactionEvent.setType(transaction.getType());
    transactionEvent.setAmount(transaction.getAmount().toString());
    transactionEvent.setAssetCode(transactionRequest.getAssetCode());
    
    kafkaTemplate.send("notificationTopic", transactionEvent);
    return "Transaction completed successfully!";
  }

  /**
   * Checks the availability of an asset for a given amount.
   *
   * @param assetCode The asset code to check.
   * @param amount The amount to check for availability.
   * @return true if the asset is available in the requested amount, false otherwise.
   */
  @Cacheable("assetAvailability")
  public boolean checkAssetAvailability(String assetCode, BigDecimal amount) {
    // Now pass both assetCode and amount as required by the updated AssetManagementClient
    int amt = amount == null ? 1 : amount.intValue();
    return assetManagementClient.checkAssetAvailability(List.of(assetCode), List.of(amt))
        .stream()
        .allMatch(AssetManagementResponse::isAssetAvailable);
  }

  /**
   *
   * Maps the provided transaction items DTO to a TransactionItems object.
   * 
   * @param transactionItemsDto The transaction items DTO to be mapped.
   * @return The corresponding TransactionItems object.
   */
  private TransactionItems mapToDto(TransactionItemsDto transactionItemsDto) {
    TransactionItems transactionItems = new TransactionItems();
    transactionItems.setAssetCode(transactionItemsDto.getAssetCode());
    transactionItems.setAssetName(transactionItemsDto.getAssetName());
    transactionItems.setValue(transactionItemsDto.getValue());
    return transactionItems;
  }

    public List<Transaction> getTransactionsForUser(String userId) {
        // Get direct transactions for the user
        List<Transaction> userTransactions = transactionRepository.findByUserId(userId);
        
        // Get all accounts for the user
        List<com.veritas.transaction.api.dto.AccountResponse> userAccounts = accountClient.getAllAccountsForUser();
        
        // Extract account IDs
        List<String> accountIds = userAccounts.stream()
                .map(com.veritas.transaction.api.dto.AccountResponse::getId)
                .toList();
        
        // Get transactions where user's accounts are involved (as source or destination)
        List<Transaction> sourceAccountTransactions = transactionRepository.findBySourceAccountIdIn(accountIds);
        List<Transaction> destinationAccountTransactions = transactionRepository.findByDestinationAccountIdIn(accountIds);
        
        // Combine all transactions and remove duplicates
        List<Transaction> allTransactions = new java.util.ArrayList<>(userTransactions);
        allTransactions.addAll(sourceAccountTransactions);
        allTransactions.addAll(destinationAccountTransactions);
        
        // Remove duplicates based on transaction ID
        return allTransactions.stream()
                .collect(java.util.stream.Collectors.toMap(
                        Transaction::getTransactionId,
                        transaction -> transaction,
                        (existing, replacement) -> existing
                ))
                .values()
                .stream()
                .sorted((t1, t2) -> t2.getTransactionTime().compareTo(t1.getTransactionTime()))
                .toList();
    }
}
