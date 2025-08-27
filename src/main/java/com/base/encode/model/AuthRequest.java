package com.base.encode.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthRequest {
    private String username;
    private String password;
    // getters & setters
}
