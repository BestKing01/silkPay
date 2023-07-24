package kz.temirbayev.silkpay.service.impl;

import kz.temirbayev.silkpay.exception.AllRequiredException;
import kz.temirbayev.silkpay.model.Account;
import kz.temirbayev.silkpay.repository.AccountRepository;
import kz.temirbayev.silkpay.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {
    @Autowired /* Использовал spring boot аннотацию для сокращения кода и не стал писать конструктор */
    private AccountRepository accountRepository;

    @Override
    public List<Account> allAccount() {
        return accountRepository.findAll(); ///использовал jpa
    }

    @Override
    public Account createAccount(Account account) {
        return accountRepository.save(account); ///использовал jpa
    }

    @Override
    public Optional<Account> getAccountById(Long accountId) {
        return accountRepository.findById(accountId); ///использовал jpa
    }

    @Override
    public BigDecimal getAccountBalanceByNumber(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)  /// использовал jpa чтобы найти аккаунт и его данные и вывел его баланс
                .orElseThrow(() -> new AllRequiredException("Неверный номер учетной записи"));///если будет ошибка ввода
        return account.getBalance();
    }

    @Override
    public void transferFunds(Long sourceAccountId, Long targetAccountId, BigDecimal amount) {
        Optional<Account> sourceAccountOptional = accountRepository.findById(sourceAccountId); ///с id нашел данные в бд с помощью jpa
        Optional<Account> targetAccountOptional = accountRepository.findById(targetAccountId); ///и сохранил

        if(sourceAccountOptional.isEmpty() & targetAccountOptional.isEmpty()){
            throw new AllRequiredException("Учетная запись не найдена"); /// если введенные данных нету и они пустые то сработает ошибка
        }

        Account sourceAccount = sourceAccountOptional.get(); ///получить обьекты Account из Optional
        Account targetAccount = targetAccountOptional.get();

        if (sourceAccount.getBalance().compareTo(amount) < 0) /// проверка достаточно ли средств
        {
            throw new AllRequiredException("Недостаточный баланс");
        }

        /// Перевод средств
        sourceAccount.setBalance(sourceAccount.getBalance().subtract(amount)); /// минусование
        targetAccount.setBalance(targetAccount.getBalance().add(amount)); /// добавление

        /// сохранение обновленных данных
        accountRepository.save(sourceAccount);
        accountRepository.save(targetAccount);
    }
}
