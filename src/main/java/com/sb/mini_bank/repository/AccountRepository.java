package com.sb.mini_bank.repository;

import com.sb.mini_bank.model.entity.Account;
import com.sb.mini_bank.model.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findByAccountNumber(String accountNumber);

    List<Account> findByClient(Client client);


}
