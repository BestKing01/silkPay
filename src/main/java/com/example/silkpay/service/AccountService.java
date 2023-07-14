package com.example.silkpay.service;

import com.example.silkpay.model.Account;
import com.example.silkpay.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountService {
    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public List<Account> allAccount(){
        return accountRepository.findAll();
    }
    @Transactional
    public Account createAccount(Account account) {
        return accountRepository.save(account);
    }

    public Account getAccountById(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }

    public BigDecimal getAccountBalanceByNumber(String accountNumber){
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Invalid account number"));
        return account.getBalance();
    }

    @Transactional
    public void transferFunds(Long sourceAccountId, Long targetAccountId, BigDecimal amount) {
        Account sourceAccount = getAccountById(sourceAccountId);
        Account targetAccount = getAccountById(targetAccountId);

        if (sourceAccount.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance in the source account");
        }

        sourceAccount.setBalance(sourceAccount.getBalance().subtract(amount));
        targetAccount.setBalance(targetAccount.getBalance().add(amount));

        accountRepository.save(sourceAccount);
        accountRepository.save(targetAccount);
    }
}
