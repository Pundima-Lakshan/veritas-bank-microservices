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
  
  @Value("${bank.account.id}")
  private String bankAccountId;

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
    switch (type.toLowerCase()) {
      case "deposit" -> {
        accountClient.creditAccount(transactionRequest.getDestinationAccountId(), new AccountClient.DebitCreditRequest(amount));
        accountClient.debitAccount(bankAccountId, new AccountClient.DebitCreditRequest(amount));
      }
      case "withdrawal" -> {
        accountClient.debitAccount(transactionRequest.getSourceAccountId(), new AccountClient.DebitCreditRequest(amount));
        accountClient.creditAccount(bankAccountId, new AccountClient.DebitCreditRequest(amount));
      }
      case "transfer" -> {
        accountClient.debitAccount(transactionRequest.getSourceAccountId(), new AccountClient.DebitCreditRequest(amount));
        accountClient.creditAccount(transactionRequest.getDestinationAccountId(), new AccountClient.DebitCreditRequest(amount));
      }
      default -> throw new IllegalArgumentException("Invalid transaction type: " + type);
    }

    transactionRepository.save(transaction);
    kafkaTemplate.send("notificationTopic", new TransactionEvent(transaction.getTransactionId()));
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
        return transactionRepository.findByUserId(userId);
    }
}
