package com.sb.mini_bank.controller;

import com.sb.mini_bank.model.dto.client.ClientRequestDTO;
import com.sb.mini_bank.service.ClientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }


    @PostMapping("/create")
    public ResponseEntity<String> createClient(@RequestBody ClientRequestDTO dto) {
        return clientService.createClient(dto);
    }

    @GetMapping("/info/{id}")
    public ResponseEntity<?> getClientById(@PathVariable Long id) {
        return clientService.getClientById(id);
    }


    @PutMapping("/update/{id}")
    public String updateClientInfo(@PathVariable Long id,
                                                    @RequestBody ClientRequestDTO dto) {
        return clientService.updateClientInfo(id, dto);
    }

    @DeleteMapping("/{id}")
    public boolean deleteClient(@PathVariable Long id) {
        return clientService.deleteClient(id);
    }
}
