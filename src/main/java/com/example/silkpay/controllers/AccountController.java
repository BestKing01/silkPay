package com.example.silkpay.controllers;

import com.example.silkpay.model.Account;
import com.example.silkpay.service.impl.AccountServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/accounts")
public class AccountController {
    @Autowired
    private AccountServiceImpl accountService;
    @GetMapping("/")
    public ResponseEntity<List<Account>> allAccount(){
        List<Account> accounts = accountService.allAccount();
        return ResponseEntity.ok(accounts);
    }
    @PostMapping("/createAcc")
    public ResponseEntity<Account> createAccount(@RequestBody Account account) {
        Account createdAccount = accountService.createAccount(account);
        return ResponseEntity.ok(createdAccount);
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<Optional<Account>> getAccountId(@PathVariable Long accountId) {
        Optional<Account> account = accountService.getAccountById(accountId);
        return ResponseEntity.ok(account);
    }
    @GetMapping("/balance/{accountNumber}")
    public ResponseEntity<BigDecimal> getAccountBalanceByNumber(@PathVariable String accountNumber){
        BigDecimal account = accountService.getAccountBalanceByNumber(accountNumber);
        return ResponseEntity.ok(account);
    }
    @PostMapping("/{sourceAccountId}/transfer/{targetAccountId}")
    public ResponseEntity<String> transferFunds(
            @PathVariable Long sourceAccountId,
            @PathVariable Long targetAccountId,
            @RequestParam("amount") String amountStr) {
        BigDecimal amount = new BigDecimal(amountStr);
        accountService.transferFunds(sourceAccountId, targetAccountId, amount);
        return ResponseEntity.ok("Funds transferred successfully");
    }
}
