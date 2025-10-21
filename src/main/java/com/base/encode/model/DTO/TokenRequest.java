package com.base.encode.model.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenRequest {
    private String customerId;

    private String firstname;

    private String lastname;

    private String phonenumber;

    private String idcard;

    private String address;

    private String source;
}
