package com.scoutli.service;

import com.scoutli.api.dto.AuthDTO;
import com.scoutli.event.UserRegisteredEvent;
import io.quarkus.oidc.client.OidcClient;
import io.quarkus.oidc.client.Tokens;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Form;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

@ApplicationScoped
@Slf4j
public class AuthService {

    @Inject
    OidcClient oidcClient;

    @Inject
    @Channel("users-out")
    Emitter<UserRegisteredEvent> userEventEmitter;

    public boolean register(AuthDTO.RegisterRequest request) {
        log.info("Keycloak registration for {} is typically handled externally or via admin API.", request.email);
        // In a real scenario, you'd use Keycloak Admin REST API to create the user
        // or redirect the user to Keycloak's registration page.
        // For now, we simulate a successful registration in Keycloak and emit an event.

        // Simulate successful registration in Keycloak and get some ID
        String simulatedKeycloakUserId = "kc-user-" + request.email.hashCode();
        String simulatedRole = "MEMBER"; // Default role

        UserRegisteredEvent event = new UserRegisteredEvent(request.email, simulatedKeycloakUserId, simulatedRole);
        userEventEmitter.send(event);
        log.info("UserRegisteredEvent sent to Kafka for user: {}", request.email);
        return true;
    }

    public String login(AuthDTO.LoginRequest request) {
        log.info("Attempting Keycloak login for user: {}", request.email);
        try {
            // Use password grant type to get tokens
            Form form = new Form()
                    .param("username", request.email)
                    .param("password", request.password)
                    .param("grant_type", "password");

            CompletionStage<Tokens> tokensStage = oidcClient.tokens(form);
            Tokens tokens = tokensStage.toCompletableFuture().get();

            if (tokens != null && tokens.getAccessToken() != null) {
                log.info("Keycloak login successful for user: {}", request.email);
                return tokens.getAccessToken();
            }
        } catch (InterruptedException | ExecutionException e) {
            log.warn("Keycloak login failed for user: {}. Error: {}", request.email, e.getMessage());
        }
        return null;
    }
}
