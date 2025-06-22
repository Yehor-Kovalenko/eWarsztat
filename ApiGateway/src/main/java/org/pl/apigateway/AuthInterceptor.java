package org.pl.apigateway;

import org.pl.securityservice.ValidateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component("AuthInterceptor")
public class AuthInterceptor implements GlobalFilter, Ordered {

    private final WebClient webClient;
    // Base URL for SecurityService
    private static final String SECURITY_SERVICE_BASE = "http://localhost:8085";

    @Autowired
    public AuthInterceptor(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(SECURITY_SERVICE_BASE).build();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        // Skip login/register paths on the gateway itself
        if (path.equals("/api/login") || path.equals("/api/register") || path.contains("webjars/swagger-ui/index.html") || path.contains("swagger-ui") || path.contains("v3/api-docs")) {
            return chain.filter(exchange);
        }
        // Also skip actuator, swagger, etc., if needed:
        // if (path.startsWith("/actuator")) return chain.filter(exchange);

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
            // Optionally write a response body
            byte[] bytes = "Missing or invalid Authorization header".getBytes();
            exchange.getResponse().getHeaders().setContentType(MediaType.TEXT_PLAIN);
            return exchange.getResponse().writeWith(Mono.just(exchange.getResponse()
                    .bufferFactory().wrap(bytes)));
        }

        // Call SecurityService /auth/validate
        return webClient.post()
                .uri("/auth/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .bodyValue("{}")  // empty JSON body; adjust if you need roles
                .retrieve()
                .bodyToMono(ValidateResponse.class)
                .flatMap(vr -> {
                    if (Boolean.TRUE.equals(vr.isValid())) {
                        // Optionally: attach username/roles into headers or exchange attributes
                        exchange.getAttributes().put("username", vr.getUsername());
                        exchange.getAttributes().put("roles", vr.getRoles());
                        // Proceed to downstream route
                        return chain.filter(exchange);
                    } else {
                        exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
                        byte[] bytes = ("Invalid token: " + vr.getMessage()).getBytes();
                        exchange.getResponse().getHeaders().setContentType(MediaType.TEXT_PLAIN);
                        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse()
                                .bufferFactory().wrap(bytes)));
                    }
                })
                .onErrorResume(ex -> {
                    // If SecurityService returns 4xx or other error
                    exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
                    String msg = "Token validation error";
                    if (ex instanceof org.springframework.web.reactive.function.client.WebClientResponseException wcEx) {
                        msg += ": " + wcEx.getRawStatusCode();
                    } else {
                        msg += ": " + ex.getMessage();
                    }
                    byte[] bytes = msg.getBytes();
                    exchange.getResponse().getHeaders().setContentType(MediaType.TEXT_PLAIN);
                    return exchange.getResponse().writeWith(Mono.just(exchange.getResponse()
                            .bufferFactory().wrap(bytes)));
                });
    }

    @Override
    public int getOrder() {
        // Order of filter: choose appropriate order (before routing filters).
        // HIGHEST_PRECEDENCE ensures this runs early.
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
