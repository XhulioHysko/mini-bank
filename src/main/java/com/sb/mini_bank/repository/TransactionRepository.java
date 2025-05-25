package com.sb.mini_bank.repository;

import com.sb.mini_bank.model.entity.Account;
import com.sb.mini_bank.model.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByAccountId(Long accountId);
    List<Transaction> findByAccount(Account account);
    List<Transaction> findByAccountAndTimeStampBetween(Account account, Date start, Date end);
    List<Transaction> findByAccountAndTypeIgnoreCase(Account account, String type);
    List<Transaction> findByAccountAndTimeStampBetweenAndTypeIgnoreCase(Account account, Date start, Date end, String type);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.account.id = " +
            ":accountId AND t.timeStamp >= :startOfDay AND t.timeStamp < :endOfDay")
    BigDecimal getTotalAmountForToday(@Param("accountId") Long accountId,
                                      @Param("startOfDay") Date startOfDay,
                                      @Param("endOfDay") Date endOfDay);


}
