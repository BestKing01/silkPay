package kz.temirbayev.silkpay;

import kz.temirbayev.silkpay.controllers.AccountController;
import kz.temirbayev.silkpay.model.Account;
import kz.temirbayev.silkpay.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AccountControllerTests {
    private MockMvc mockMvc;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController accountController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(accountController).build();
    }

    @Test
    public void testGetAllAccounts() throws Exception {
        List<Account> accounts = new ArrayList<>();
        accounts.add(new Account(1L, "123456789", new BigDecimal("1000.00")));
        accounts.add(new Account(2L, "987654321", new BigDecimal("500.00")));

        when(accountService.allAccount()).thenReturn(accounts);

        mockMvc.perform(get("/accounts"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[0].accountNumber").value("123456789"))
                .andExpect(jsonPath("$[0].balance").value("1000.00"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Jane Smith"))
                .andExpect(jsonPath("$[1].accountNumber").value("987654321"))
                .andExpect(jsonPath("$[1].balance").value("500.00"));

        verify(accountService, times(1)).allAccount();
        verifyNoMoreInteractions(accountService);
    }

    @Test
    public void testGetAccountById() throws Exception {
        Account account = new Account(1L, "123456789", new BigDecimal("1000.00"));

        when(accountService.getAccountById(1L)).thenReturn(Optional.of(account));

        mockMvc.perform(get("/accounts/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.accountNumber").value("123456789"))
                .andExpect(jsonPath("$.balance").value("1000.00"));

        verify(accountService, times(1)).getAccountById(1L);
        verifyNoMoreInteractions(accountService);
    }

    @Test
    public void testGetAccountById_AccountNotFound() throws Exception {
        when(accountService.getAccountById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/accounts/1"))
                .andExpect(status().isNotFound());

        verify(accountService, times(1)).getAccountById(1L);
        verifyNoMoreInteractions(accountService);
    }

    @Test
    public void testCreateAccount() throws Exception {
        Account account = new Account(null, "123456789", new BigDecimal("1000.00"));
        Account savedAccount = new Account(1L, "123456789", new BigDecimal("1000.00"));

        when(accountService.createAccount(any(Account.class))).thenReturn(savedAccount);

        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"John Doe\",\"accountNumber\":\"123456789\",\"balance\":\"1000.00\"}"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.accountNumber").value("123456789"))
                .andExpect(jsonPath("$.balance").value("1000.00"));

        verify(accountService, times(1)).createAccount(any(Account.class));
        verifyNoMoreInteractions(accountService);
    }

    @Test
    public void testTransferFunds_Successful() throws Exception {
        Account sourceAccount = new Account(1L, "123456789", new BigDecimal("1000.00"));
        Account targetAccount = new Account(2L,  "987654321", new BigDecimal("500.00"));

        when(accountService.getAccountById(1L)).thenReturn(Optional.of(sourceAccount));
        when(accountService.getAccountById(2L)).thenReturn(Optional.of(targetAccount));

        mockMvc.perform(post("/accounts/transfer")
                        .param("sourceAccountId", "1")
                        .param("targetAccountId", "2")
                        .param("amount", "500.00"))
                .andExpect(status().isOk())
                .andExpect(content().string("Funds transferred successfully"));

        verify(accountService, times(1)).getAccountById(1L);
        verify(accountService, times(1)).getAccountById(2L);
        verify(accountService, times(1)).transferFunds(1L, 2L, new BigDecimal("500.00"));
        verifyNoMoreInteractions(accountService);
    }

    @Test
    public void testTransferFunds_InsufficientBalance() throws Exception {
        Account sourceAccount = new Account(1L,  "123456789", new BigDecimal("1000.00"));

        when(accountService.getAccountById(1L)).thenReturn(Optional.of(sourceAccount));

        mockMvc.perform(post("/accounts/transfer")
                        .param("sourceAccountId", "1")
                        .param("targetAccountId", "2")
                        .param("amount", "1500.00"))
                .andExpect(status().isBadRequest());

        verify(accountService, times(1)).getAccountById(1L);
        verifyNoMoreInteractions(accountService);
    }
}
