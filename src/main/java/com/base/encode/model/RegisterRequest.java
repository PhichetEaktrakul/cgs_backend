package com.base.encode.model;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

    private String username;
    private String password;
    private String role; // e.g., "USER" or "ADMIN"
}
