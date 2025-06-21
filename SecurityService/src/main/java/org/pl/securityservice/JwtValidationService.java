package org.pl.securityservice;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class JwtValidationService {

    private final JwtDecoder jwtDecoder;

    public JwtValidationService(JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    /**
     * Validate the token, decode it, and return the Jwt object if valid.
     */
    public Jwt validateToken(String token) {
        // This will throw JwtException (e.g. BadJwtException) if invalid (signature, expired, etc).
        // For synchronous approach. If you want reactive, you could wrap in Mono.fromCallable(...)
        return jwtDecoder.decode(token);
    }

    /**
     * Extract roles from the Jwt.
     * Depending on Keycloak token structure, roles may be under:
     * - "realm_access": {"roles": [...]}
     * - "resource_access": { "<client-id>": {"roles": [...]} }
     *
     * Return a Set<String> of roles.
     */
    public Set<String> extractRoles(Jwt jwt) {
        Set<String> roles = new HashSet<>();
        // Example: realm roles
        Object realmAccess = jwt.getClaims().get("realm_access");
        if (realmAccess instanceof Map<?, ?> raMap) {
            Object rolesObj = raMap.get("roles");
            if (rolesObj instanceof Collection<?>) {
                for (Object r : (Collection<?>) rolesObj) {
                    if (r instanceof String) {
                        roles.add((String) r);
                    }
                }
            }
        }
        // Example: client roles
        Object resourceAccess = jwt.getClaims().get("resource_access");
        if (resourceAccess instanceof Map<?, ?> resMap) {
            for (Object entryObj : resMap.entrySet()) {
                if (entryObj instanceof Map.Entry<?, ?> e) {
                    Object v = e.getValue();
                    if (v instanceof Map<?, ?> clientMap) {
                        Object rObj = clientMap.get("roles");
                        if (rObj instanceof Collection<?> roleList) {
                            for (Object r : roleList) {
                                if (r instanceof String) {
                                    roles.add((String) r);
                                }
                            }
                        }
                    }
                }
            }
        }
        return roles;
    }
}
