package com.veritas.account.api.service;

import com.veritas.account.api.model.Account;
import com.veritas.account.api.repository.AccountRepository;
import com.veritas.account.api.dto.AccountResponse;
import com.veritas.account.api.dto.AccountRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import com.veritas.account.api.client.TransactionApiClient;
import com.veritas.account.api.dto.TransactionRequest;

/**
 * Service class that provides operations for managing bank accounts.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;

    private final RedisTemplate<String, List<AccountResponse>> redisTemplate;

    private final TransactionApiClient transactionApiClient;

    private static final String CACHE_KEY = "accounts";

    private static final Random random = new Random();

    /**
     * Creates a new bank account.
     *
     * @param accountRequest The account request containing account details.
     */
    public void createAccount(AccountRequest accountRequest) {
        Account account = Account.builder()
                .accountNumber(generateIBAN())
                .accountName(accountRequest.getAccountName())
                .accountHolderName(accountRequest.getAccountHolderName())
                .balance(BigDecimal.valueOf(0)) // this will be updated at transaction level
                .currency(accountRequest.getCurrency())
                .userId(accountRequest.getUserId()) // set userId from request
                .build();

        accountRepository.save(account);
        redisTemplate.delete(CACHE_KEY);
        log.info("Account for {} is created", account.getAccountHolderName());

        // Call transaction API for initial deposit
        if (accountRequest.getBalance() != null && accountRequest.getBalance().compareTo(java.math.BigDecimal.ZERO) > 0) {
            TransactionRequest txRequest = new TransactionRequest();
            txRequest.setUserId(accountRequest.getUserId());
            txRequest.setDestinationAccountId(account.getId());
            txRequest.setType("deposit");
            txRequest.setAssetCode(accountRequest.getCurrency().toString()); // or another asset code logic
            txRequest.setAmount(accountRequest.getBalance());
            transactionApiClient.createTransaction(txRequest);
        }
    }

    /**
     * Generates a random International Bank Account Number (IBAN).
     *
     * @return The generated IBAN.
     */
    public static String generateIBAN() {
        String[] countryCodes = Locale.getISOCountries();
        int index = random.nextInt(countryCodes.length);
        String countryCode = countryCodes[index];
        String accountNumber = String.format("%02d-%04d-%04d-%04d-%04d",
                random.nextInt(100),
                random.nextInt(10000),
                random.nextInt(10000),
                random.nextInt(10000),
                random.nextInt(10000));
        return countryCode + accountNumber;
    }

    /**
     * Retrieves all bank accounts.
     *
     * @return A list of AccountResponse objects representing the bank accounts.
     */
    public List<AccountResponse> getAllAccounts() {
        List<AccountResponse> cachedAccounts = redisTemplate.opsForValue().get(CACHE_KEY);

        if (cachedAccounts != null) {
            return cachedAccounts;
        } else {
            List<Account> accounts = accountRepository.findAll();

            List<AccountResponse> accountResponses = accounts.stream()
                    .map(this::mapToAccountResponse)
                    .toList();
            redisTemplate.opsForValue().set(CACHE_KEY, accountResponses);
            return accountResponses;
        }
    }

    /**
     * Retrieves all bank accounts for a specific user.
     *
     * @param userId The ID of the user whose accounts to retrieve.
     * @return A list of AccountResponse objects representing the user's bank accounts.
     */
    public List<AccountResponse> getAllAccounts(String userId) {
        List<Account> accounts = accountRepository.findByUserId(userId);
        return accounts.stream().map(this::mapToAccountResponse).toList();
    }

    /**
     * Maps an Account object to an AccountResponse object.
     *
     * @param account The Account object to map.
     * @return The mapped AccountResponse object.
     */
    private AccountResponse mapToAccountResponse(Account account) {
        return AccountResponse.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .accountName(account.getAccountName())
                .accountHolderName(account.getAccountHolderName())
                .balance(account.getBalance())
                .currency(account.getCurrency())
                .userId(account.getUserId()) // include userId in response
                .build();
    }

    /**
     * Deletes a bank account based on the account holder name.
     *
     * @param name The account holder name.
     * @throws NoSuchElementException if the account is not found.
     */
    public void deleteAccountByAccountHolderName(String name) {
        List<Account> accounts = accountRepository.findAll();

        Optional<Account> accountToDelete = accounts.stream()
                .filter(a -> a.getAccountHolderName() != null &&
                        a.getAccountHolderName().equals(name)).findFirst();

        if (accountToDelete.isPresent()) {
            accountRepository.delete(accountToDelete.get());
            redisTemplate.delete(CACHE_KEY);
        } else {
            throw new NoSuchElementException("The bank account information for "
                    + name + " was not found.");
        }
    }

    /**
     * Deletes a bank account based on the account holder name and userId.
     *
     * @param name The account holder name.
     * @param userId The user ID from the JWT.
     * @throws NoSuchElementException if the account is not found or does not belong to the user.
     */
    public void deleteAccountByAccountHolderNameAndUserId(String name, String userId) {
        List<Account> accounts = accountRepository.findByUserId(userId);
        Optional<Account> accountToDelete = accounts.stream()
                .filter(a -> a.getAccountHolderName() != null &&
                        a.getAccountHolderName().equals(name)).findFirst();

        if (accountToDelete.isPresent()) {
            accountRepository.delete(accountToDelete.get());
            redisTemplate.delete(CACHE_KEY);
        } else {
            throw new NoSuchElementException("The bank account information for "
                    + name + " was not found or does not belong to the user.");
        }
    }

    public void debitAccount(String accountId, java.math.BigDecimal amount) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        if (account.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds");
        }
        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);
        redisTemplate.delete(CACHE_KEY);
    }

    public void creditAccount(String accountId, java.math.BigDecimal amount) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);
        redisTemplate.delete(CACHE_KEY);
    }
}
