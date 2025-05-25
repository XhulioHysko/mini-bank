package com.sb.mini_bank.model.dto.transaction;

import lombok.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponseDTO {

    private Long id;
    private String amount;
    private String type;
    private Date timeStamp;
}
