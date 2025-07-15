package com.veritas.account.api.client;

import com.veritas.account.api.dto.TransactionRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "transaction-api")
public interface TransactionApiClient {
    @PostMapping("/api/transaction")
    void createTransaction(@RequestBody TransactionRequest transactionRequest);
} 