package com.scoutli.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisteredEvent {
    public String email;
    public String userId; // Keycloak's user ID (sub claim)
    public String role;
}
