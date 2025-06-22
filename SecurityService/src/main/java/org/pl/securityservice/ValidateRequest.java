package org.pl.securityservice;

import lombok.Data;

import java.util.List;

@Data
public class ValidateRequest {
    // Optional: if you prefer passing role in body.
    private String requiredRole;
    private List<String> requiredRoles;
}
