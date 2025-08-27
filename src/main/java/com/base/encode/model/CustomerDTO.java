package com.base.encode.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerDTO {
    private Integer custId;

    private String firstname;

    private String lastname;

    private String phonenumber;

    private String idcard;

    private String address;

    private BigDecimal goldBalance96;

    private BigDecimal goldBalance99;

    private LocalDateTime registerAt;

    private String source;
}
