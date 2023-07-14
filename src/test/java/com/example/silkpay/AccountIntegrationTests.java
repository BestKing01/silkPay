package com.example.silkpay;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import com.example.silkpay.model.Account;
import com.example.silkpay.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
public class AccountIntegrationTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @BeforeEach
    public void setup() {
        Account account1 = new Account(1L, "123456789", new BigDecimal("100.00"));
        Account account2 = new Account(2L, "987654321", new BigDecimal("50.00"));

        when(accountService.allAccount()).thenReturn(Arrays.asList(account1, account2));
    }

    @Test
    public void testGetAllAccounts() throws Exception {
        mockMvc.perform(get("/accounts/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].accountNumber", is("123456789")))
                .andExpect(jsonPath("$[0].balance", is(100.00)))
                .andExpect(jsonPath("$[1].accountNumber", is("987654321")))
                .andExpect(jsonPath("$[1].balance", is(50.00)));

        verify(accountService, times(1)).allAccount();
    }

    // Другие интеграционные тесты для взаимодействия между AccountController и AccountService...
}
