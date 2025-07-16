package com.veritas.notification.api.client;

import com.veritas.notification.api.dto.AccountResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Feign client interface for interacting with the Account API.
 */
@FeignClient(name = "account-api")
public interface AccountClient {
    
    /**
     * Retrieves an account by its ID.
     * 
     * @param accountId The ID of the account to retrieve.
     * @return The AccountResponse object containing account details.
     */
    @GetMapping("/api/account/{id}")
    AccountResponse getAccountById(@PathVariable("id") String accountId);
} 