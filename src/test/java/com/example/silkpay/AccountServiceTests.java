package com.example.silkpay;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.silkpay.model.Account;
import com.example.silkpay.repository.AccountRepository;
import com.example.silkpay.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import java.math.BigDecimal;
import java.util.Optional;

public class AccountServiceTests {
    private AccountRepository accountRepository;
    private AccountService accountService;

    @BeforeEach
    public void setup() {
        accountRepository = mock(AccountRepository.class);
        accountService = new AccountService(accountRepository);
    }

    @Test
    public void testTransferFunds_SufficientBalance_SuccessfulTransfer() {
        Account sourceAccount = new Account(1L, "123456789", new BigDecimal("100.00"));
        Account targetAccount = new Account(2L, "987654321", new BigDecimal("50.00"));
        BigDecimal transferAmount = new BigDecimal("30.00");

        when(accountRepository.findById(1L)).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(targetAccount));

        accountService.transferFunds(1L, 2L, transferAmount);

        assertEquals(new BigDecimal("70.00"), sourceAccount.getBalance());
        assertEquals(new BigDecimal("80.00"), targetAccount.getBalance());
    }

    @Test
    public void testTransferFunds_InsufficientBalance_ThrowsException() {
        Account sourceAccount = new Account(1L, "123456789", new BigDecimal("50.00"));
        Account targetAccount = new Account(2L, "987654321", new BigDecimal("100.00"));
        BigDecimal transferAmount = new BigDecimal("70.00");

        when(accountRepository.findById(1L)).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(targetAccount));

        assertThrows(RuntimeException.class, () ->
                accountService.transferFunds(1L, 2L, transferAmount));
    }

    // Другие unit-тесты для AccountService...
}
