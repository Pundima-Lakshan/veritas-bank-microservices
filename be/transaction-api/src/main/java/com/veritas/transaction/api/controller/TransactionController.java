package com.veritas.transaction.api.controller;

import com.veritas.transaction.api.dto.TransactionRequest;
import com.veritas.transaction.api.service.TransactionService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import jakarta.servlet.http.HttpServletRequest;
import com.veritas.transaction.api.model.Transaction;
import com.veritas.transaction.api.util.JwtUtil;

/**
 * Controller class that handles HTTP requests related to transactions.
 */
@RestController
@RequestMapping("/api/transaction")
@RequiredArgsConstructor
@Slf4j
public class TransactionController {

    private final TransactionService transactionService;

    /**
     *
     * Processes a transaction asynchronously.
     * @param transactionRequest The transaction request object received in the request body (expects assetCode and amount).
     * @return A CompletableFuture representing the result of the transaction processing.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompletableFuture<String> processTransaction(@RequestBody TransactionRequest transactionRequest, HttpServletRequest request) {
        log.info("Transaction processed.");
        String userId = transactionRequest.getUserId();
        if (userId == null || userId.isEmpty()) {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                userId = JwtUtil.extractUserId(token);
            }
            transactionRequest.setUserId(userId);
        }
        return CompletableFuture.supplyAsync(() -> transactionService.processTransaction(transactionRequest));
    }

    @GetMapping
    public List<Transaction> getUserTransactions(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        String userId = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            userId = JwtUtil.extractUserId(token);
        }
        
        if (userId == null || userId.isEmpty()) {
            log.warn("User ID not found in request");
            return List.of(); // Return empty list if user is not authenticated
        }
        
        return transactionService.getTransactionsForUser(userId);
    }

    /**
     *
     *Circuit breaker implementation. Fallback method to handle exceptions during transaction processing.
     *@param transactionRequest The transaction request object.
     *@param runtimeException The exception that occurred during transaction processing.
     *@return A CompletableFuture representing a fallback message.
     */
    public CompletableFuture<String> fallbackMethod(TransactionRequest transactionRequest, RuntimeException runtimeException) {
        log.info("Transaction can't be processed. Executing fallback logic.");
        return CompletableFuture.supplyAsync(() -> "Oops! Something went wrong, please try again later!");
    }
}
