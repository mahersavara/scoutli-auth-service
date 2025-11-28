package com.scoutli.auth.dto;

import lombok.Data;

@Data
public class AuthRequest {
    public String email;
    public String password;
    public String fullName;
}
