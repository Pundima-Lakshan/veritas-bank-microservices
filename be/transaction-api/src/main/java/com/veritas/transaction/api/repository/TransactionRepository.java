package com.veritas.transaction.api.repository;

import com.veritas.transaction.api.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository interface for storing assets in the database.
 * It provides CRUD operations and other database-related functionality.
 */
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserId(String userId);
    List<Transaction> findBySourceAccountIdIn(List<String> accountIds);
    List<Transaction> findByDestinationAccountIdIn(List<String> accountIds);
}
