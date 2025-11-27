package com.scoutli.api.controller;

import com.scoutli.api.dto.AuthDTO;
import com.scoutli.api.dto.UserDTO;
import com.scoutli.service.AuthService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.jwt.JsonWebToken;
import java.util.stream.Collectors;

@Path("/api/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthController {

    @Inject
    AuthService authService;

    @Inject
    JsonWebToken jwt; // Inject JWT to access claims

    @POST
    @Path("/register")
    public Response register(AuthDTO.RegisterRequest request) {
        // Registration is typically handled directly by Keycloak's UI or Admin API.
        // This endpoint can be removed or used to trigger a Keycloak Admin API call.
        // For now, we assume user registration happens successfully externally.
        boolean registered = authService.register(request);
        if (registered) {
            return Response.status(201).entity("User registration initiated. Please complete on Keycloak if needed.").build();
        }
        return Response.status(400).entity("Registration failed").build();
    }

    @POST
    @Path("/login")
    public Response login(AuthDTO.LoginRequest request) {
        String token = authService.login(request);
        if (token != null) {
            return Response.ok(new AuthDTO.AuthResponse(token)).build();
        }
        return Response.status(401).entity("Invalid credentials or Keycloak unavailable").build();
    }

    @GET
    @Path("/me")
    @RolesAllowed({"MEMBER", "ADMIN"})
    public UserDTO getMyUserDetails(@Context SecurityContext securityContext) {
        String email = securityContext.getUserPrincipal().getName(); // or jwt.getName() / jwt.getClaim("email")
        
        // The 'groups' claim typically holds roles in Keycloak JWTs
        String role = jwt.getGroups().stream().findFirst().orElse("UNKNOWN"); // Assuming single role for simplicity

        // In a real application, you might want to fetch more details from Keycloak's user info endpoint
        // or a local cache if you're not solely relying on JWT claims.
        
        UserDTO user = new UserDTO();
        user.setEmail(email);
        // User ID might be in 'sub' claim (subject)
        user.setId(Long.valueOf(jwt.getSubject())); // Assuming 'sub' can be parsed to Long
        user.setRole(role);
        return user;
    }
}
