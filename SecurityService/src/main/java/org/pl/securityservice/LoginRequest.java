package org.pl.securityservice;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
