package com.scoutli.api.controller;

import com.scoutli.api.dto.AuthRequest;
import com.scoutli.api.dto.AuthResponse;
import com.scoutli.domain.entity.User;
import com.scoutli.auth.service.TokenService;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthController {

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
        user.setEmail(request.email);
        // Password hashing should be added here using BCrypt
        user.setPassword(request.password);
        user.setFullName(request.fullName);
        user.setRoles("user");
        user.persist();

        String token = tokenService.generateToken(user);
        return Response.ok(new AuthResponse(token, user.getId())).build();
    }

    @POST
    @Path("/login")
    public Response login(AuthRequest request) {
        User user = User.findByEmail(request.email);
        // Verify password (plain text for now, hash later)
        if (user == null || !user.getPassword().equals(request.password)) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid credentials").build();
        }

        String token = tokenService.generateToken(user);
        return Response.ok(new AuthResponse(token, user.getId())).build();
    }
}
