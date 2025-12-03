package com.scoutli.service;

import com.scoutli.api.dto.AuthDTO;
import io.quarkus.oidc.client.OidcClient;
import io.quarkus.oidc.client.Tokens;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
@Slf4j
public class AuthService {

    @Inject
    OidcClient oidcClient;

    public boolean register(AuthDTO.RegisterRequest request) {
        log.info("Keycloak registration for {} is typically handled externally or via admin API.", request.email);
        // In a real scenario, you'd use Keycloak Admin REST API to create the user
        // or redirect the user to Keycloak's registration page.
        // For now, we simulate a successful registration in Keycloak and emit an event.

        // Simulate successful registration in Keycloak
        log.info("User registration simulated for: {}", request.email);
        return true;
    }

    public String login(AuthDTO.LoginRequest request) {
        log.info("Attempting Keycloak login for user: {}", request.email);
        try {
            // Use password grant type to get tokens
            Map<String, String> params = new HashMap<>();
            params.put("username", request.email);
            params.put("password", request.password);
            params.put("grant_type", "password");

            Tokens tokens = oidcClient.getTokens(params)
                    .onFailure()
                    .invoke(failure -> log.warn("Keycloak login failed for user: {}. Error: {}", request.email,
                            failure.getMessage()))
                    .await().indefinitely();

            if (tokens != null && tokens.getAccessToken() != null) {
                log.info("Keycloak login successful for user: {}", request.email);
                return tokens.getAccessToken();
            }
        } catch (Exception e) {
            log.warn("Keycloak login failed for user: {}. Error: {}", request.email, e.getMessage());
        }
        return null;
    }
}
