package com.sb.mini_bank.model.dto.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClientRequestDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;

}
