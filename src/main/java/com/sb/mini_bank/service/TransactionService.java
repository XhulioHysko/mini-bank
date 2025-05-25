package com.sb.mini_bank.service;

import com.sb.mini_bank.model.dto.transaction.TransactionResponseDTO;
import com.sb.mini_bank.model.dto.transaction.TransferRequestDTO;
import com.sb.mini_bank.model.entity.Account;
import com.sb.mini_bank.model.entity.Client;
import com.sb.mini_bank.model.entity.Transaction;
import com.sb.mini_bank.repository.AccountRepository;
import com.sb.mini_bank.repository.ClientRepository;
import com.sb.mini_bank.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final ClientRepository clientRepository;
    private final ModelMapper modelMapper;

    public TransactionService(TransactionRepository transactionRepository,
                              AccountRepository accountRepository, ClientRepository clientRepository, ModelMapper modelMapper) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.clientRepository = clientRepository;
        this.modelMapper = modelMapper;
    }


    public List<TransactionResponseDTO> getTransactionsByClientAndAccount(Long clientId, Long accountId) {
        try {
            Account account = accountRepository.findById(accountId)
                    .orElseThrow(() -> new RuntimeException("Account not found"));

            if (!account.getClient().getId().equals(clientId)) {
                throw new RuntimeException("Unauthorized access.");
            }

            List<Transaction> transactions = transactionRepository.findByAccount(account);

            return transactions.stream()
                    .map(tx -> modelMapper.map(tx, TransactionResponseDTO.class))
                    .toList();

        } catch (Exception e) {
            throw new RuntimeException("Error fetching transactions: " + e.getMessage());
        }
    }

    public List<TransactionResponseDTO> smartFilter(Long clientId, Long accountId, String query) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        String lowered = query.toLowerCase();

        return transactionRepository.findByAccount(account).stream()
                .filter(t -> {
                    return String.valueOf(t.getAmount()).toLowerCase().contains(lowered)
                            || t.getType().toLowerCase().contains(lowered)
                            || t.getTimeStamp().toString().toLowerCase().contains(lowered);
                })
                .map(t -> modelMapper.map(t, TransactionResponseDTO.class))
                .collect(Collectors.toList());
    }


    @Transactional
    public String transfer(TransferRequestDTO dto) {
        try {
            BigDecimal amount = dto.getAmount();

            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Transfer of 0L not allowed!");
            }

            // Get the sender's and receiver's accounts
            Account fromAccount = accountRepository.findById(dto.getFromAccountId())
                    .orElseThrow(() -> new NoSuchElementException("Sender account does not exist!"));

            Account toAccount = accountRepository.findById(dto.getToAccountId())
                    .orElseThrow(() -> new NoSuchElementException("Receiver account does not exist!"));

            // Verify if the sender's account is the same as the receiver's
            if (fromAccount.getId().equals(toAccount.getId())) {
                throw new IllegalArgumentException("You cannot transfer to the same account!");
            }

            // Check if the sender's account has sufficient balance
            if (fromAccount.getBalance().compareTo(amount) < 0) {
                throw new IllegalArgumentException("Your balance is not sufficient to make this transfer!");
            }

            // Check the currency of both accounts (sender and receiver)
            if (!fromAccount.getCurrency().equals(toAccount.getCurrency())) {
                throw new IllegalArgumentException("You cannot transfer between accounts with different currencies!");
            }

            // Create the start and end dates of the day
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            Date startOfDay = calendar.getTime();

            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            Date endOfDay = calendar.getTime();

            // Check the daily transaction limit
            BigDecimal dailyTransactionTotal = transactionRepository.getTotalAmountForToday(fromAccount.getId(), startOfDay, endOfDay);

            // Verify that the new transaction does not exceed the daily limit
            if (dailyTransactionTotal.add(amount).compareTo(fromAccount.getDailyTransactionLimit()) > 0) {
                throw new IllegalArgumentException("You have exceeded your daily transaction limit!");
            }

            // Adjust the balances
            fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
            toAccount.setBalance(toAccount.getBalance().add(amount));

            // Save the accounts to the database
            accountRepository.save(fromAccount);
            accountRepository.save(toAccount);

            // Record the transactions
            transactionRepository.save(new Transaction(fromAccount, amount, "TRANSFER_OUT", new Date()));
            transactionRepository.save(new Transaction(toAccount, amount, "TRANSFER_IN", new Date()));

            // Return the success message
            return "The transfer was successfully completed from account " + fromAccount.getAccountNumber() +
                    " to account " + toAccount.getAccountNumber();

        } catch (IllegalArgumentException | NoSuchElementException e) {
            log.warn("Error during transfer: {}", e.getMessage());
            return e.getMessage();
        } catch (Exception e) {
            log.error("Unexpected error during transfer: {}", e.getMessage(), e);
            return "An unexpected error occurred during the transfer.";
        }
    }


    // Function to get the limit for each currency
    private BigDecimal getDailyLimit(String currency) {
        switch (currency) {
            case "LEK":
                return new BigDecimal("1000000");  // 1,000,000 LEK
            case "EURO":
                return new BigDecimal("1000");  // 1,000 EURO
            case "USD":
                return new BigDecimal("1000");  // 1,000 USD
            default:
                throw new IllegalArgumentException("Invalid currency for transaction limit.");
        }
    }


}
