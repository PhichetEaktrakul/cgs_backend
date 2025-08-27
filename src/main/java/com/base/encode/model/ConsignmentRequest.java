package com.base.encode.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsignmentRequest {
    private Integer pledgeId;

    private Integer customerId;

    private BigDecimal weight;

    private Integer goldType;

    private BigDecimal refPrice;

    private BigDecimal loanPercent;

    private BigDecimal loanAmount;

    private BigDecimal interestRate;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private String transactionType;
}
