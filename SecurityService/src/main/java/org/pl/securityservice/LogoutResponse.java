package org.pl.securityservice;

import lombok.Data;
import java.util.List;

@Data
public class LogoutResponse {
    private boolean success;
    private String message;
}

