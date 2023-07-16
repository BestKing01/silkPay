package com.example.silkpay.service.impl;

import com.example.silkpay.model.Account;
import com.example.silkpay.repository.AccountRepository;
import com.example.silkpay.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountRepository accountRepository;

    @Override
    public List<Account> allAccount() {
        return accountRepository.findAll();
    }

    @Override
    public Account createAccount(Account account) {
        return accountRepository.save(account);
    }

    @Override
    public Optional<Account> getAccountById(Long accountId) {
        return accountRepository.findById(accountId);
    }

    @Override
    public BigDecimal getAccountBalanceByNumber(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Invalid account number"));
        return account.getBalance();
    }

    @Override
    public void transferFunds(Long sourceAccountId, Long targetAccountId, BigDecimal amount) {
        Optional<Account> sourceAccountOptional = accountRepository.findById(sourceAccountId);
        Optional<Account> targetAccountOptional = accountRepository.findById(targetAccountId);

        if(sourceAccountOptional.isEmpty() & targetAccountOptional.isEmpty()){
            throw new RuntimeException("Source ot Target Account not found");
        }

        Account sourceAccount = sourceAccountOptional.get();
        Account targetAccount = targetAccountOptional.get();

        if (sourceAccount.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance in the source account");
        }

        sourceAccount.setBalance(sourceAccount.getBalance().subtract(amount));
        targetAccount.setBalance(targetAccount.getBalance().add(amount));

        accountRepository.save(sourceAccount);
        accountRepository.save(targetAccount);
    }
}
