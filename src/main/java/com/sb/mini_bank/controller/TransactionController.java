package com.sb.mini_bank.controller;

import com.sb.mini_bank.model.dto.transaction.TransactionResponseDTO;
import com.sb.mini_bank.model.dto.transaction.TransferRequestDTO;
import com.sb.mini_bank.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(name = "transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }


    @GetMapping("/client/{clientId}/account/{accountId}/history")
    public List<TransactionResponseDTO> getFullClientTransactionHistory(
            @PathVariable Long clientId,
            @PathVariable Long accountId) {
        return transactionService.getTransactionsByClientAndAccount(clientId, accountId);
    }

    // smart filter:
    @GetMapping("/client/{clientId}/account/{accountId}/search")
    public List<TransactionResponseDTO> TransactionFilter(
            @PathVariable Long clientId,
            @PathVariable Long accountId,
            @RequestParam String query
    ) {
        return transactionService.smartFilter(clientId, accountId, query);
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transferFunds(@RequestBody TransferRequestDTO dto) {
        String response = transactionService.transfer(dto);
        return ResponseEntity.ok(response);
    }
}
