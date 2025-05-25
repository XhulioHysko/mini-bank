package com.sb.mini_bank.service;

import com.sb.mini_bank.model.dto.client.ClientRequestDTO;
import com.sb.mini_bank.model.dto.client.ClientResponseDTO;
import com.sb.mini_bank.model.entity.Client;
import com.sb.mini_bank.repository.ClientRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ClientService {

    private final ClientRepository clientRepository;
    private final ModelMapper modelMapper;

    public ClientService(ClientRepository clientRepository, ModelMapper modelMapper) {
        this.clientRepository = clientRepository;
        this.modelMapper = modelMapper;
    }

    public ResponseEntity<String> createClient(ClientRequestDTO dto) {
        try {
            Client client = modelMapper.map(dto, Client.class);
            Client saved = clientRepository.save(client);

            return new ResponseEntity<>("Client created successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Client creation failed: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> getClientById(Long id) {
        try {
            Client client = clientRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Client not found"));

            ClientResponseDTO responseDTO = modelMapper.map(client, ClientResponseDTO.class);
            return ResponseEntity.ok(responseDTO);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Client with ID " + id + " was not found.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred.");
        }
    }


    public String updateClientInfo(Long id, ClientRequestDTO dto) {
        try {
            Client client = clientRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Client not found"));
            modelMapper.map(dto, client);
            clientRepository.save(client);

            log.info("Client with ID {} was successfully updated.", id);
            return "Client information updated successfully.";

        } catch (RuntimeException e) {
            log.error("Error updating client with ID {}: {}", id, e.getMessage());
            return "Client with ID " + id + " not found.";
        } catch (Exception e) {
            log.error("Unexpected error while updating client with ID {}: {}", id, e.getMessage());
            return "An unexpected error occurred while updating client info.";
        }
    }


    public boolean deleteClient(Long id) {
        if (clientRepository.existsById(id)) {
            clientRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
