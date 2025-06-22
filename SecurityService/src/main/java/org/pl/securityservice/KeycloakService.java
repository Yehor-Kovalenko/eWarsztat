package org.pl.securityservice;

import org.pl.securityservice.TokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Service
public class KeycloakService {

    private final WebClient keycloakWebClient;
    private final String clientId;
    private final String clientSecret;
    private final String tokenUri;  // full URL or relative path
    private final String logoutUri;

    public KeycloakService(WebClient keycloakWebClient,
                           @Value("eWarsztat") String clientId,
                           @Value("FuUw3gWriPkghCzMFe410BoYXh6dwqKC") String clientSecret,
                           @Value("http://localhost:9000/realms/myrealm/protocol/openid-connect/token") String tokenUri,
                           @Value("http://localhost:9000/realms/myrealm/protocol/openid-connect/logout") String logoutUri) {
        this.keycloakWebClient = keycloakWebClient;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.tokenUri = tokenUri;
        this.logoutUri = logoutUri;
    }

    /**
     * Call Keycloak token endpoint with password grant.
     */
    public Mono<TokenResponse> login(String username, String password) {
        return keycloakWebClient
                .post()
                .uri(tokenUri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue("grant_type=password"
                        + "&client_id=" + clientId
                        + "&client_secret=" + clientSecret
                        + "&username=" + username
                        + "&password=" + password)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(TokenResponse.class);
    }

    /**
     * Call Keycloak logout endpoint with refresh_token.
     */
    public Mono<Void> logout(String refreshToken) {
        return keycloakWebClient
                .post()
                .uri(logoutUri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue("client_id=" + clientId
                        + "&client_secret=" + clientSecret
                        + "&refresh_token=" + refreshToken)
                .retrieve()
                .bodyToMono(Void.class);
    }

}

