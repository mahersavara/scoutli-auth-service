package com.scoutli.auth.service;

import com.scoutli.auth.entity.User;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@ApplicationScoped
public class TokenService {

    public String generateToken(User user) {
        String roles = user.roles != null ? user.roles : "user";
        Set<String> groups = new HashSet<>(Arrays.asList(roles.split(",")));
        
        return Jwt.issuer("https://scoutli.com/auth")
                .upn(user.email)
                .groups(groups)
                .claim("id", user.id)
                .expiresIn(3600) 
                .sign();
    }
}
