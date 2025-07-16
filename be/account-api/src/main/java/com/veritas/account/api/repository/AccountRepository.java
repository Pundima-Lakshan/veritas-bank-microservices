package com.veritas.account.api.repository;

import com.veritas.account.api.model.Account;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Repository interface for storing bank accounts in the MongoDB database.
 * It provides CRUD operations and other database-related functionality.
 */
public interface AccountRepository extends MongoRepository<Account, String> {
    List<Account> findByUserId(String userId);
}