package com.base.encode.model.DTO;

import java.math.BigDecimal;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerMetaEdit {
    private Integer customerId;

    private BigDecimal loanPercent;

    private BigDecimal interestRate;
}
