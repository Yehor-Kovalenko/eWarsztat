package org.pl.securityservice;

import org.pl.securityservice.TokenEntity;
import org.pl.securityservice.UserEntity;
import org.pl.securityservice.TokenRepository;
import org.pl.securityservice.UserRepository;
import org.pl.securityservice.KeycloakService;
import org.pl.securityservice.JwtValidationService;
import org.pl.securityservice.TokenResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Instant;
import java.util.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final KeycloakService keycloakService;
    private final JwtValidationService jwtValidationService;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final KeycloakAdminService keycloakAdminService;

    public AuthController(KeycloakService keycloakService,
                          JwtValidationService jwtValidationService,
                          UserRepository userRepository,
                          TokenRepository tokenRepository,
                          KeycloakAdminService keycloakAdminService) {
        this.keycloakService = keycloakService;
        this.jwtValidationService = jwtValidationService;
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.keycloakAdminService = keycloakAdminService;
    }

    /**
     * Login: calls Keycloak token endpoint.
     */
    @PostMapping(value = "/token", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<TokenResponse> login(@RequestBody LoginRequest loginRequest) {
        return keycloakService.login(loginRequest.getUsername(), loginRequest.getPassword())
                .flatMap(tokenResp -> {
                    // After obtaining TokenResponse, store user & token in DB
                    String username = loginRequest.getUsername();
                    return Mono.fromCallable(() -> {
                        UserEntity user = userRepository.findByUsername(username)
                                .orElseGet(() -> {
                                    UserEntity u = UserEntity.builder()
                                            .username(username)
                                            .createdAt(Instant.now())
                                            .build();
                                    return userRepository.save(u);
                                });
                        // compute issuedAt, expiresAt from current time + expiresIn
                        Instant issuedAt = Instant.now();
                        Instant expiresAt = issuedAt.plusSeconds(tokenResp.getRefreshExpiresIn() != null
                                ? tokenResp.getRefreshExpiresIn()
                                : tokenResp.getExpiresIn());
                        TokenEntity tokenEntity = TokenEntity.builder()
                                .refreshToken(tokenResp.getRefreshToken())
                                .issuedAt(issuedAt)
                                .expiresAt(expiresAt)
                                .user(user)
                                .build();
                        tokenRepository.save(tokenEntity);
                        return tokenResp;
                    }).subscribeOn(Schedulers.boundedElastic());
                });
    }

    /**
     * Validate: check token validity and roles.
     * Token is expected in Authorization header as Bearer.
     * Body may contain requiredRole or requiredRoles.
     */
    @PostMapping(value = "/validate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ValidateResponse> validate(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestBody ValidateRequest validateRequest) {
        String prefix = "Bearer ";
        if (authorization == null || !authorization.startsWith(prefix)) {
            ValidateResponse resp = new ValidateResponse();
            resp.setValid(false);
            resp.setMessage("Missing or invalid Authorization header");
            return Mono.just(resp);
        }
        String token = authorization.substring(prefix.length());
        return Mono.fromCallable(() -> {
            try {
                Jwt jwt = jwtValidationService.validateToken(token).block();
                Set<String> userRoles = jwtValidationService.extractRoles(jwt);
                // Determine required roles
                List<String> required = new ArrayList<>();
                if (validateRequest.getRequiredRole() != null) {
                    required.add(validateRequest.getRequiredRole());
                }
                if (validateRequest.getRequiredRoles() != null) {
                    required.addAll(validateRequest.getRequiredRoles());
                }
                boolean hasRole = true;
                if (!required.isEmpty()) {
                    // require at least one? or all? Adjust logic as needed. Here: require any one of them:
                    hasRole = required.stream().anyMatch(userRoles::contains);
                }
                ValidateResponse resp = new ValidateResponse();
                resp.setUsername(jwt.getSubject());
                resp.setRoles(new ArrayList<>(userRoles));
                if (hasRole) {
                    resp.setValid(true);
                    resp.setMessage("Token valid and has required role");
                } else {
                    resp.setValid(false);
                    resp.setMessage("Token valid but missing required role");
                }
                return resp;
            } catch (Exception ex) {
                ValidateResponse resp = new ValidateResponse();
                resp.setValid(false);
                resp.setMessage("Token invalid: " + ex.getMessage());
                return resp;
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Logout: invalidate refresh token in Keycloak, remove from DB.
     */
    @PostMapping(value = "/logout", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<LogoutResponse> logout(@RequestBody LogoutRequest logoutRequest) {
        String refreshToken = logoutRequest.getRefreshToken();
        if (refreshToken == null || refreshToken.isBlank()) {
            LogoutResponse resp = new LogoutResponse();
            resp.setSuccess(false);
            resp.setMessage("Missing refreshToken");
            return Mono.just(resp);
        }
        return keycloakService.logout(refreshToken)
                .then(Mono.fromCallable(() -> {
                    // delete from DB
                    tokenRepository.findByRefreshToken(refreshToken)
                            .ifPresent(tok -> tokenRepository.delete(tok));
                    LogoutResponse resp = new LogoutResponse();
                    resp.setSuccess(true);
                    resp.setMessage("Logged out successfully");
                    return resp;
                }).subscribeOn(Schedulers.boundedElastic()))
                .onErrorResume(ex -> {
                    LogoutResponse resp = new LogoutResponse();
                    resp.setSuccess(false);
                    resp.setMessage("Error during logout: " + ex.getMessage());
                    return Mono.just(resp);
                });
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<RegisterResponse> register(@RequestBody RegisterRequest registerRequest) {
        String username = registerRequest.getUsername();
        String password = registerRequest.getPassword();
        String email = registerRequest.getEmail();
        // Wrap blocking Keycloak admin call into reactive Mono
        return Mono.fromCallable(() -> {
                    // Validate inputs
                    if (username == null || username.isBlank() ||
                            password == null || password.isBlank()) {
                        throw new IllegalArgumentException("Username and password must be provided");
                    }
                    // Call KeycloakAdminService to create user
                    keycloakAdminService.createUser(username, password, email);
                    return new RegisterResponse(true, "User registered successfully");
                })
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorResume(ex -> {
                    String msg = ex.getMessage();
                    // If IllegalArgumentException or RuntimeException from createUser
                    RegisterResponse resp = new RegisterResponse(false, "Registration failed: " + msg);
                    return Mono.just(resp);
                });
    }
}
