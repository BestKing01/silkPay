package com.example.silkpay;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.silkpay.controllers.AccountController;
import com.example.silkpay.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.math.BigDecimal;

public class AccountControllerTests {
    private AccountService accountService;
    private AccountController accountController;

    @BeforeEach
    public void setup() {
        accountService = mock(AccountService.class);
        accountController = new AccountController(accountService);
    }

    @Test
    public void testGetAccountBalanceByNumber_ValidAccountNumber_ReturnsBalance() {
        String accountNumber = "123456789";
        BigDecimal balance = new BigDecimal("100.00");

        when(accountService.getAccountBalanceByNumber(accountNumber)).thenReturn(balance);

        ResponseEntity<BigDecimal> response = accountController.getAccountBalanceByNumber(accountNumber);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(balance, response.getBody());
    }

    // Другие unit-тесты для AccountController...
}
