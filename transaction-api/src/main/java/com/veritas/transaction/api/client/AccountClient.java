package com.veritas.transaction.api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.math.BigDecimal;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name = "account-api")
public interface AccountClient {
    @PostMapping("/api/account/{id}/debit")
    void debitAccount(@PathVariable("id") String accountId, @RequestBody DebitCreditRequest request);

    @PostMapping("/api/account/{id}/credit")
    void creditAccount(@PathVariable("id") String accountId, @RequestBody DebitCreditRequest request);

    class DebitCreditRequest {
        public BigDecimal amount;
        public DebitCreditRequest() {}
        public DebitCreditRequest(BigDecimal amount) { this.amount = amount; }
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
    }
} 