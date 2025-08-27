package com.base.encode.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayableInterestDTO {
    private int interestId;
    private int pledgeId;
    private Integer prevInterestId;
    private LocalDateTime dueDate;
    private BigDecimal oldLoanAmount;
    private BigDecimal oldInterestRate;
    private BigDecimal weight;
    private String goldType;
    private LocalDateTime endDate;
}
