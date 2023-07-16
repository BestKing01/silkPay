package com.example.silkpay.service;

import com.example.silkpay.model.Account;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


public interface AccountService {
    public List<Account> allAccount();
    public Account createAccount(Account account);

    public Optional<Account> getAccountById(Long accountId);

    public BigDecimal getAccountBalanceByNumber(String accountNumber);

    @Transactional
    public void transferFunds(Long sourceAccountId, Long targetAccountId, BigDecimal amount);
}
