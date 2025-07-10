package com.veritas.account.api.controller;

import com.veritas.account.api.service.AccountService;
import com.veritas.account.api.dto.AccountResponse;
import com.veritas.account.api.dto.AccountRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import com.veritas.account.api.util.JwtUtil;
import com.veritas.account.api.dto.DebitCreditRequest;

/**
 * Controller class that handles HTTP requests related to bank accounts.
 */
@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    /**
     * Creates a new bank account.
     *
     * @param accountRequest The account request containing account details.
     * @return A ResponseEntity with a success message and HTTP status code 201 if the account was created successfully.
     */
    @PostMapping
    public ResponseEntity<String> createAccount(@RequestBody AccountRequest accountRequest, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        String userId = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            // Simple JWT parsing to extract 'sub' claim (user id)
            try {
                String[] parts = token.split("\\.");
                if (parts.length == 3) {
                    String payloadJson = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));
                    int subIndex = payloadJson.indexOf("\"sub\":");
                    if (subIndex != -1) {
                        int start = payloadJson.indexOf('"', subIndex + 6) + 1;
                        int end = payloadJson.indexOf('"', start);
                        userId = payloadJson.substring(start, end);
                    }
                }
            } catch (Exception e) {
                // fallback: userId remains null
            }
        }
        accountRequest.setUserId(userId);
        accountService.createAccount(accountRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body
                ("Successfully set up a new bank account for " +
                accountRequest.getAccountHolderName() + ".");
    }

    /**
     * Retrieves all bank accounts.
     *
     * @return A list of AccountResponse objects representing the bank accounts.
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<AccountResponse> getAllAccounts(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        String userId = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                String[] parts = token.split("\\.");
                if (parts.length == 3) {
                    String payloadJson = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));
                    int subIndex = payloadJson.indexOf("\"sub\":");
                    if (subIndex != -1) {
                        int start = payloadJson.indexOf('"', subIndex + 6) + 1;
                        int end = payloadJson.indexOf('"', start);
                        userId = payloadJson.substring(start, end);
                    }
                }
            } catch (Exception e) {
                // fallback: userId remains null
            }
        }
        return accountService.getAllAccounts(userId);
    }

    /**
     * Deletes a bank account based on the account holder name.
     *
     * @param accountRequest The account request containing the account holder name.
     * @return A ResponseEntity with a success message and HTTP status code 200 if the account was deleted successfully,
     *         or a ResponseEntity with an error message and HTTP status code 404 if the account was not found.
     */
    @DeleteMapping
    public ResponseEntity<String> deleteAccount(@RequestBody AccountRequest accountRequest, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        String userId = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            userId = JwtUtil.extractUserId(token);
        }
        try {
            accountService.deleteAccountByAccountHolderNameAndUserId(accountRequest.getAccountHolderName(), userId);
            return ResponseEntity.status(HttpStatus.OK).body("Successfully deleted " +
                    accountRequest.getAccountHolderName() + "'s account.");
        } catch (RuntimeException runtimeException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(runtimeException.getMessage());
        }
    }

    @PostMapping("/{id}/debit")
    public ResponseEntity<String> debitAccount(@PathVariable String id, @RequestBody DebitCreditRequest request) {
        try {
            accountService.debitAccount(id, request.getAmount());
            return ResponseEntity.ok("Account debited successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/credit")
    public ResponseEntity<String> creditAccount(@PathVariable String id, @RequestBody DebitCreditRequest request) {
        accountService.creditAccount(id, request.getAmount());
        return ResponseEntity.ok("Account credited successfully.");
    }
}
