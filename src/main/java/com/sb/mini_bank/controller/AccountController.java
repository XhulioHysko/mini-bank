package com.sb.mini_bank.controller;

import com.sb.mini_bank.model.dto.account.AccountRequestDTO;
import com.sb.mini_bank.model.dto.account.AccountResposneDTO;
import com.sb.mini_bank.model.entity.Account;
import com.sb.mini_bank.service.AccountService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/{clientId}/create")
    public String createAccount(@PathVariable Long clientId,
                                            @RequestBody AccountRequestDTO dto) {
        return accountService.createAccount(clientId, dto);
    }

    @GetMapping("/client/{clientId}")
    public List<AccountResposneDTO> getAccountsByClientId(@PathVariable Long clientId) {
        return accountService.getAccountsByClientId(clientId);
    }

    @GetMapping("/{accountNumber}")
    public Account getAccountByAccountNumber(@PathVariable String accountNumber) {
        return accountService.getAccountByAccountNumber(accountNumber);
    }

    @PutMapping("/deposit/{id}")
    public Account deposit(@PathVariable Long id, @RequestParam BigDecimal amount) {
        return accountService.deposit(id, amount);
    }

    @PutMapping("/withdraw/{id}")
    public Account withdraw(@PathVariable Long id, @RequestParam BigDecimal amount) {
        return accountService.withdraw(id, amount);
    }

    @GetMapping("/balance/{id}")
    public BigDecimal getBalance(@PathVariable Long id) {
        return accountService.getBalance(id);
    }
}
