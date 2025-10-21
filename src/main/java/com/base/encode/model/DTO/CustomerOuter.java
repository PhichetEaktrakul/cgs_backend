package com.base.encode.model.DTO;

import java.math.BigDecimal;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerOuter {
    private String customerId;

    private String firstname;

    private String lastname;

    private String phonenumber;

    private String idcard;

    private String address;

    private BigDecimal goldBalance96;

    private BigDecimal goldBalance99;

    private String source;
}
