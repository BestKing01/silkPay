package com.example.silkpay.controllers;

import com.example.silkpay.model.Account;
import com.example.silkpay.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }


    @PostMapping("/createAcc")
    public ResponseEntity<Account> createAccount(@RequestBody Account account) {
        Account createdAccount = accountService.createAccount(account);
        return ResponseEntity.ok(createdAccount);
    }

    @GetMapping("/")
    public ResponseEntity<List<Account>> allAccount(){
        List<Account> accounts = accountService.allAccount();
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<Account> getAccountId(@PathVariable Long accountId) {
        Account account = accountService.getAccountById(accountId);
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
