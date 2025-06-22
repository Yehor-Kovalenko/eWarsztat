package org.pl.securityservice;

import lombok.Data;

import java.util.List;

@Data
public class ValidateResponse {
    private boolean valid;
    private String username;
    private List<String> roles;
    private String message;
}
