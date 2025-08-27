package com.base.encode.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDTO {
    private String refcode;

    private String idcard;

    private String firstname;

    private String lastname;

    private Double goldBalance;






/*     private String name;

    private String phonenumber;

    private String email; */
}
