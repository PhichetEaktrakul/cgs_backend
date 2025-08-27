package com.base.encode.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EncodeDTO {
    private Integer custId;

    private String firstname;

    private String lastname;

    private String phonenumber;

    private String idcard;

    private String address;

    private String source;
}
