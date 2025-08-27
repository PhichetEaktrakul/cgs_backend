package com.base.encode.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterestRequest {
    private Integer interestId;

    private Integer pledgeId;

    private Integer prevInterestId;

    private LocalDateTime dueDate;

    private BigDecimal oldLoanAmount;

    private BigDecimal oldInterestRate;

    private BigDecimal payInterest;

    private BigDecimal payLoan;

    private String transactionType;
}
