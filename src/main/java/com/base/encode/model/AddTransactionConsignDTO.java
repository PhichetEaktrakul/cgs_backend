package com.base.encode.model;

import java.math.BigDecimal;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddTransactionConsignDTO {
    private Integer pledge_id;
    
    private String transaction_type;

    private BigDecimal amount;

    private PledgeGoldDTO data;
}
