package org.pl.securityservice;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Collections;
import java.util.List;

@Service
public class KeycloakAdminService {

    @Value("${keycloak.server-url}")
    private String keycloakServerUrl;

    @Value("${keycloak.admin-realm}")
    private String adminRealm;

    @Value("${keycloak.realm}")
    private String targetRealm;

    @Value("${keycloak.admin-client-id}")
    private String adminClientId;

    @Value("${keycloak.admin-username}")
    private String adminUsername;

    @Value("${keycloak.admin-password}")
    private String adminPassword;

    @Value("${keycloak.default-user-role:user}")
    private String defaultUserRole;

    private Keycloak keycloakClient;

    @PostConstruct
    public void init() {
        this.keycloakClient = KeycloakBuilder.builder()
                .serverUrl(keycloakServerUrl)
                .realm(adminRealm)
                .username(adminUsername)
                .password(adminPassword)
                .clientId(adminClientId)
                .build();
    }

    @PreDestroy
    public void cleanup() {
        if (this.keycloakClient != null) {
            this.keycloakClient.close();
        }
    }

    /**
     * Reactive wrapper for creating a Keycloak user
     */
    public Mono<Void> createUser(String username, String password, String email) {
        return Mono.fromCallable(() -> {
            UsersResource usersResource = keycloakClient.realm(targetRealm).users();

            // Check if username already exists
            List<UserRepresentation> existing = usersResource.search(username, true);
            if (existing != null && !existing.isEmpty()) {
                throw new RuntimeException("User with username '" + username + "' already exists");
            }

            // Check if email already exists
            if (email != null && !email.isBlank()) {
                List<UserRepresentation> byEmail = usersResource.searchByEmail(email, true);
                for (UserRepresentation u : byEmail) {
                    if (email.equalsIgnoreCase(u.getEmail())) {
                        throw new RuntimeException("User with email '" + email + "' already exists");
                    }
                }
            }

            // Create the user
            UserRepresentation user = new UserRepresentation();
            user.setUsername(username);
            user.setEmail(email);
            user.setEnabled(true);
            user.setRequiredActions(Collections.emptyList());

            Response resp = usersResource.create(user);
            int status = resp.getStatus();
            if (status != 201) {
                resp.close();
                throw new RuntimeException("Failed to create user: HTTP " + status);
            }
            String userId = CreatedResponseUtil.getCreatedId(resp);
            resp.close();

            // Set password
            CredentialRepresentation cred = new CredentialRepresentation();
            cred.setType(CredentialRepresentation.PASSWORD);
            cred.setTemporary(false);
            cred.setValue(password);
            keycloakClient.realm(targetRealm).users().get(userId).resetPassword(cred);

            // Assign default role if set
            if (defaultUserRole != null && !defaultUserRole.isBlank()) {
                RoleRepresentation roleRep = keycloakClient
                        .realm(targetRealm)
                        .roles()
                        .get(defaultUserRole)
                        .toRepresentation();
                keycloakClient.realm(targetRealm)
                        .users()
                        .get(userId)
                        .roles()
                        .realmLevel()
                        .add(Collections.singletonList(roleRep));
            }

            return null;
        }).subscribeOn(Schedulers.boundedElastic()).then(); // runs on a background thread pool
    }
}