package com.example.silkpay.repository;

import com.example.silkpay.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountNumber(String accountNumber);

    List<Account> findAllByOrderByAccountNumberAsc();

    List<Account> findAllByBalanceGreaterThan(BigDecimal balance);


    @Query("SELECT SUM(a.balance) FROM Account a")
    BigDecimal getTotalBalance();

    @Modifying
    @Query("UPDATE Account SET balance = balance + :amount WHERE id = :accountId")
    void increaseBalance(@Param("accountId") Long accountId, @Param("amount") BigDecimal amount);

    @Modifying
    @Query("UPDATE Account SET balance = balance - :amount WHERE id = :accountId")
    void decreaseBalance(@Param("accountId") Long accountId, @Param("amount") BigDecimal amount);
}
