package com.sb.mini_bank.model.dto.account;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountResposneDTO {

    private Long id;
    private String accountNumber;
    private String balance;
    private String type;
    private String currency;
    private BigDecimal dailyTransactionLimit;
}
