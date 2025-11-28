package com.scoutli.auth.resource;

import com.scoutli.auth.dto.AuthRequest;
import com.scoutli.auth.dto.AuthResponse;
import com.scoutli.auth.entity.User;
import com.scoutli.auth.service.TokenService;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    TokenService tokenService;

    @POST
    @Path("/register")
    @Transactional
    public Response register(AuthRequest request) {
        if (User.findByEmail(request.email) != null) {
            return Response.status(Response.Status.CONFLICT).entity("Email already exists").build();
        }

        User user = new User();
        user.email = request.email;
        // Password hashing should be added here using BCrypt
        user.password = request.password; 
        user.fullName = request.fullName;
        user.roles = "user";
        user.persist();

        String token = tokenService.generateToken(user);
        return Response.ok(new AuthResponse(token, user.id)).build();
    }

    @POST
    @Path("/login")
    public Response login(AuthRequest request) {
        User user = User.findByEmail(request.email);
        // Verify password (plain text for now, hash later)
        if (user == null || !user.password.equals(request.password)) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid credentials").build();
        }

        String token = tokenService.generateToken(user);
        return Response.ok(new AuthResponse(token, user.id)).build();
    }
}
