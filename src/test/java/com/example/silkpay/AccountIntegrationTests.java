package com.example.silkpay;

import com.example.silkpay.controllers.AccountController;
import com.example.silkpay.model.Account;
import com.example.silkpay.repository.AccountRepository;
import com.example.silkpay.service.AccountService;
import com.example.silkpay.service.impl.AccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.hamcrest.Matchers.*;

@WebMvcTest(AccountController.class)
@AutoConfigureMockMvc
public class AccountIntegrationTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @MockBean
    private AccountRepository accountRepository;

    @BeforeEach
    public void setup() {
        List<Account> accounts = new ArrayList<>();
        accounts.add(new Account(1L, "123456789", new BigDecimal("1000.00")));
        accounts.add(new Account(2L, "987654321", new BigDecimal("500.00")));

        when(accountService.allAccount()).thenReturn(accounts);
        when(accountService.getAccountById(1L)).thenReturn(Optional.of(accounts.get(0)));
        when(accountService.getAccountById(2L)).thenReturn(Optional.of(accounts.get(1)));
        when(accountRepository.findByAccountNumber("123456789")).thenReturn(Optional.of(accounts.get(0)));
        when(accountRepository.findByAccountNumber("987654321")).thenReturn(Optional.of(accounts.get(1)));
    }

    @Test
    public void testGetAllAccounts() throws Exception {
        mockMvc.perform(get("/accounts/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].accountNumber").value("123456789"))
                .andExpect(jsonPath("$[0].balance").value("1000.00"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].accountNumber").value("987654321"))
                .andExpect(jsonPath("$[1].balance").value("500.00"));
    }

    @Test
    public void testGetAccountById() throws Exception {
        mockMvc.perform(get("/accounts/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.accountNumber").value("123456789"))
                .andExpect(jsonPath("$.balance").value("1000.00"));
    }

}
