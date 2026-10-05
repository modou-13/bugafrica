package com.bugafrica.user;

import java.time.Instant;

public record UserResponse(Long id, String username, String email, String country,
                           int reputation, Role role, Instant createdAt) {

    public static UserResponse from(User u) {
        return new UserResponse(u.getId(), u.getUsername(), u.getEmail(), u.getCountry(),
                u.getReputation(), u.getRole(), u.getCreatedAt());
    }
}
