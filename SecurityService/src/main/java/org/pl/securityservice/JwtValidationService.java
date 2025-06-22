package org.pl.securityservice;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class JwtValidationService {

    private final ReactiveJwtDecoder jwtDecoder;

    public JwtValidationService(ReactiveJwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    /**
     * Reactive validation of JWT token.
     * @param token the JWT token as a string
     * @return Mono emitting Jwt if valid, error otherwise
     */
    public Mono<Jwt> validateToken(String token) {
        return jwtDecoder.decode(token);
    }

    /**
     * Extract roles from a Jwt object.
     * Parses both realm and client-specific roles.
     */
    public Set<String> extractRoles(Jwt jwt) {
        Set<String> roles = new HashSet<>();

        // Realm roles
        Object realmAccess = jwt.getClaims().get("realm_access");
        if (realmAccess instanceof Map<?, ?> raMap) {
            Object rolesObj = raMap.get("roles");
            if (rolesObj instanceof Collection<?>) {
                for (Object r : (Collection<?>) rolesObj) {
                    if (r instanceof String role) {
                        roles.add(role);
                    }
                }
            }
        }

        // Client roles
        Object resourceAccess = jwt.getClaims().get("resource_access");
        if (resourceAccess instanceof Map<?, ?> resMap) {
            for (Object value : resMap.values()) {
                if (value instanceof Map<?, ?> clientMap) {
                    Object rObj = clientMap.get("roles");
                    if (rObj instanceof Collection<?> roleList) {
                        for (Object r : roleList) {
                            if (r instanceof String role) {
                                roles.add(role);
                            }
                        }
                    }
                }
            }
        }

        return roles;
    }

    /**
     * Optionally provide a reactive version of extractRoles.
     */
    public Mono<Set<String>> extractRolesReactive(Jwt jwt) {
        return Mono.fromCallable(() -> extractRoles(jwt));
    }
}
