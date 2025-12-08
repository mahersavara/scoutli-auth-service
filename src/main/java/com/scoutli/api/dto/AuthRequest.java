package com.scoutli.api.dto;

import lombok.Data;

@Data
public class AuthRequest {
    public String email;
    public String password;
    public String fullName;
}
