package com.sb.mini_bank.service;

import com.sb.mini_bank.model.dto.account.AccountRequestDTO;
import com.sb.mini_bank.model.dto.account.AccountResposneDTO;
import com.sb.mini_bank.model.entity.Account;
import com.sb.mini_bank.model.entity.Client;
import com.sb.mini_bank.model.entity.Transaction;
import com.sb.mini_bank.model.enums.Currency;
import com.sb.mini_bank.repository.AccountRepository;
import com.sb.mini_bank.repository.ClientRepository;
import com.sb.mini_bank.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;
    private final ClientRepository clientRepository;
    private final TransactionRepository transactionRepository;
    private final ModelMapper modelMapper;

    public AccountService(AccountRepository accountRepository, ClientRepository clientRepository, TransactionRepository transactionRepository, ModelMapper modelMapper) {
        this.accountRepository = accountRepository;
        this.clientRepository = clientRepository;
        this.transactionRepository = transactionRepository;
        this.modelMapper = modelMapper;
    }


    @Transactional
    public String createAccount(Long clientId, AccountRequestDTO dto) {
        try {
            Client client = clientRepository.findById(clientId)
                    .orElseThrow(() -> new RuntimeException("Client with ID " + clientId + " not found"));

            Currency currency = Currency.valueOf(dto.getCurrency().toUpperCase());

            Account account = modelMapper.map(dto, Account.class);
            account.setClient(client);
            account.setCurrency(currency);
            account.setAccountNumber(UUID.randomUUID().toString());

            accountRepository.save(account);
            log.info("Account created successfully for client ID {}", clientId);


            return "Account was created successfully.";

        } catch (IllegalArgumentException e) {
            log.warn("Invalid currency: {}", dto.getCurrency());
            return "Invalid currency. Please use: LEK, EUR, or USD.";
        } catch (RuntimeException e) {
            log.warn("Error: {}", e.getMessage());
            return e.getMessage();
        } catch (Exception e) {
            log.error("Unexpected error during account creation", e);
            return "An unexpected error occurred while creating the account.";
        }
    }

    public List<AccountResposneDTO> getAccountsByClientId(Long clientId) {
        try {
            Client client = clientRepository.findById(clientId)
                    .orElseThrow(() -> new RuntimeException("Client with ID " + clientId + " not found"));

            List<Account> accounts = accountRepository.findByClient(client);

            return accounts.stream()
                    .map(account -> modelMapper.map(account, AccountResposneDTO.class))
                    .toList();

        } catch (RuntimeException e) {
            log.warn("Client not found with ID {}: {}", clientId, e.getMessage());
            return List.of();
        } catch (Exception e) {
            log.error("Unexpected error retrieving accounts for client {}: {}", clientId, e.getMessage(), e);
            return List.of();
        }
    }

    public Account getAccountByAccountNumber(String accountNumber){
        return accountRepository.findByAccountNumber(accountNumber);
    }


    public Account deposit(Long id, BigDecimal amount) {
        try {

            Account account = accountRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Account not found"));

            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("The amount must be greater than 100L.");
            }

            account.setBalance(account.getBalance().add(amount));
            Account updated = accountRepository.save(account);

            Transaction transaction = new Transaction();
            transaction.setAmount(amount);
            transaction.setType("DEPOSIT");
            transaction.setTimeStamp(new Date());
            transaction.setAccount(updated);
            transactionRepository.save(transaction);

            log.info("Deposit of ${} successful. New balance: {}", amount, updated.getBalance());
            return updated;

        } catch (Exception e) {
            log.error("Error during deposit: {}", e.getMessage());
            throw e;
        }
    }

    public Account withdraw(Long id, BigDecimal amount) {
        try {
            Account account = accountRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Account not found"));

            if (account.getBalance().compareTo(amount) < 0) {
                throw new RuntimeException("Insufficient funds.");
            }

            account.setBalance(account.getBalance().add(amount));
            Account updated = accountRepository.save(account);

            Transaction transaction = new Transaction();
            transaction.setAmount(amount);
            transaction.setType("WITHDRAW");
            transaction.setTimeStamp(new Date());
            transaction.setAccount(updated);
            transactionRepository.save(transaction);

            log.info("Withdrawal of ${} successful. New balance: {}", amount, updated.getBalance());
            return updated;

        } catch (Exception e) {
            log.error("Error during withdrawal: {}", e.getMessage());
            throw e;
        }
    }

    public BigDecimal getBalance(Long id) {
        Account account = accountRepository.findById(id).orElse(null);
        return account != null ? account.getBalance() : BigDecimal.ZERO;
    }
}
