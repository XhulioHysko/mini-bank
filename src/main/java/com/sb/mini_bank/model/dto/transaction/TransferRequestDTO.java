package com.sb.mini_bank.model.dto.transaction;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequestDTO {

    private Long fromAccountId;
    private Long toAccountId;

    @Column(precision = 19, scale = 2)
    private BigDecimal amount;

}
