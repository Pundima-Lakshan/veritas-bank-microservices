package com.veritas.transaction.api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.math.BigDecimal;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name = "account-api")
@RequestMapping("/api/account")
public interface AccountClient {
    @PostMapping("/{id}/debit")
    void debitAccount(@PathVariable("id") String accountId, @RequestBody DebitCreditRequest request);

    @PostMapping("/{id}/credit")
    void creditAccount(@PathVariable("id") String accountId, @RequestBody DebitCreditRequest request);

    class DebitCreditRequest {
        public BigDecimal amount;
        public DebitCreditRequest() {}
        public DebitCreditRequest(BigDecimal amount) { this.amount = amount; }
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
    }
} 