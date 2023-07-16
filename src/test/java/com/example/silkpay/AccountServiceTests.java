package com.example.silkpay;

import com.example.silkpay.model.Account;
import com.example.silkpay.repository.AccountRepository;
import com.example.silkpay.service.AccountService;
import com.example.silkpay.service.impl.AccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AccountServiceTests {
    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService = new AccountServiceImpl();

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllAccounts() {
        List<Account> accounts = new ArrayList<>();
        accounts.add(new Account(1L, "123456789", new BigDecimal("1000.00")));
        accounts.add(new Account(2L,  "987654321", new BigDecimal("500.00")));

        when(accountRepository.findAll()).thenReturn(accounts);

        List<Account> result = accountService.allAccount();

        assertEquals(2, result.size());

        verify(accountRepository, times(1)).findAll();
        verifyNoMoreInteractions(accountRepository);
    }

    @Test
    public void testCreateAccount() {
        Account accountToSave = new Account(null, "123456789", new BigDecimal("1000.00"));
        Account savedAccount = new Account(1L,  "123456789", new BigDecimal("1000.00"));

        when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);

        Account result = accountService.createAccount(accountToSave);

        assertEquals(1L, result.getId());
        assertEquals("123456789", result.getAccountNumber());
        assertEquals(new BigDecimal("1000.00"), result.getBalance());

        verify(accountRepository, times(1)).save(any(Account.class));
        verifyNoMoreInteractions(accountRepository);
    }

    @Test
    public void testGetAccountById() {
        Account account = new Account(1L,  "123456789", new BigDecimal("1000.00"));

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        Optional<Account> result = accountService.getAccountById(1L);

        assertTrue(result.isPresent());
        assertEquals("123456789", result.get().getAccountNumber());
        assertEquals(new BigDecimal("1000.00"), result.get().getBalance());

        verify(accountRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(accountRepository);
    }

    @Test
    public void testGetAccountById_AccountNotFound() {
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Account> result = accountService.getAccountById(1L);

        assertFalse(result.isPresent());

        verify(accountRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(accountRepository);
    }

    @Test
    public void testGetAccountBalanceByNumber() {
        Account account = new Account(1L, "123456789", new BigDecimal("1000.00"));

        when(accountRepository.findByAccountNumber("123456789")).thenReturn(Optional.of(account));

        BigDecimal result = accountService.getAccountBalanceByNumber("123456789");

        assertEquals(new BigDecimal("1000.00"), result);

        verify(accountRepository, times(1)).findByAccountNumber("123456789");
        verifyNoMoreInteractions(accountRepository);
    }

    @Test
    public void testGetAccountBalanceByNumber_AccountNotFound() {
        when(accountRepository.findByAccountNumber("123456789")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> accountService.getAccountBalanceByNumber("123456789"));

        verify(accountRepository, times(1)).findByAccountNumber("123456789");
        verifyNoMoreInteractions(accountRepository);
    }

    @Test
    public void testTransferFunds_Successful() {
        Account sourceAccount = new Account(1L,  "123456789", new BigDecimal("1000.00"));
        Account targetAccount = new Account(2L,  "987654321", new BigDecimal("500.00"));

        when(accountRepository.findById(1L)).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(targetAccount));

        accountService.transferFunds(1L, 2L, new BigDecimal("500.00"));

        assertEquals(new BigDecimal("500.00"), sourceAccount.getBalance());
        assertEquals(new BigDecimal("1000.00"), targetAccount.getBalance());

        verify(accountRepository, times(1)).findById(1L);
        verify(accountRepository, times(1)).findById(2L);
        verify(accountRepository, times(1)).save(sourceAccount);
        verify(accountRepository, times(1)).save(targetAccount);
        verifyNoMoreInteractions(accountRepository);
    }

    @Test
    public void testTransferFunds_InsufficientBalance() {
        Account sourceAccount = new Account(1L,  "123456789", new BigDecimal("1000.00"));

        when(accountRepository.findById(1L)).thenReturn(Optional.of(sourceAccount));

        assertThrows(RuntimeException.class, () -> accountService.transferFunds(1L, 2L, new BigDecimal("1500.00")));

        verify(accountRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(accountRepository);
    }
}
