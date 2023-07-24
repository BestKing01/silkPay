package kz.temirbayev.silkpay.controllers;

import kz.temirbayev.silkpay.model.Account;
import kz.temirbayev.silkpay.service.impl.AccountServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/accounts")
public class AccountController {
    private final AccountServiceImpl accountService;
    public AccountController(AccountServiceImpl accountService) {
        this.accountService = accountService;
    }

    @GetMapping /* как запускаем код открываем постман указываем этот url: http://localhost:8001/accounts
                        get запросом чтобы вывел все аккаунты со всеми данными */
    public ResponseEntity<List<Account>> allAccount(){
        List<Account> accounts = accountService.allAccount();
        return ResponseEntity.ok(accounts);
    }
    @PostMapping("/createAcc") /* также в постмане с post запросом можем добавить новый акканут  заходим в raw, json(с возможностью указать начальный баланс)
                                     пример заполнения { "accountNumber": "000-004", "balance": 444444.00 }*/
    public ResponseEntity<Account> createAccount(@RequestBody Account account) {
        Account createdAccount = accountService.createAccount(account);
        return ResponseEntity.ok(createdAccount);
    }

    @GetMapping("/{accountId}") /* поиск по id url: http://localhost:8001/accounts/1 */
    public ResponseEntity<Optional<Account>> getAccountId(@PathVariable Long accountId) {
        Optional<Account> account = accountService.getAccountById(accountId);
        return ResponseEntity.ok(account);
    }
    @GetMapping("/balance/{accountNumber}") /* поиск по accountNumber где показывает только счет данного accountNumber пример url:http://localhost:8001/accounts/balance/000-003*/
    public ResponseEntity<BigDecimal> getAccountBalanceByNumber(@PathVariable String accountNumber){
        BigDecimal account = accountService.getAccountBalanceByNumber(accountNumber);
        return ResponseEntity.ok(account);
    }
    @PostMapping("/{sourceAccountId}/transfer/{targetAccountId}") /* Перевод между счетами где указываем первым id с какого счета переводим и дальше склько будем переводить а вторым
                                                                        id показываем счет получателя пример url: http://localhost:8001/accounts/1/transfer/2?amount=1000000.000
                                                                        после вопросительного знака, это указывает на начало секции параметров запроса*/
    public ResponseEntity<String> transferFunds(
            @PathVariable Long sourceAccountId,
            @PathVariable Long targetAccountId,
            @RequestParam("amount") String amountStr) /// amount String что бы потом обработать на bigDecimal (не знал как сделать по другому)
    {
        BigDecimal amount = new BigDecimal(amountStr);
        accountService.transferFunds(sourceAccountId, targetAccountId, amount);
        return ResponseEntity.ok("Funds transferred successfully");
    }

}
