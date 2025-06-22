package org.pl.securityservice;

import lombok.Data;

@Data
public class LogoutRequest {
    private String refreshToken;
}
