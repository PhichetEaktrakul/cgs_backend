package com.base.encode.model.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemConfig {
    private Integer id;

    private Double loanPercent;

    private Double interestRate;
    
    private Integer numPay;
}
