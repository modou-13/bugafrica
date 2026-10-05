package com.bugafrica.security;

import com.bugafrica.user.Role;

/** Utilisateur authentifié disponible dans les controllers via @AuthenticationPrincipal. */
public record AuthUser(Long id, String username, Role role) {
    public boolean isAdmin() { return role == Role.ADMIN; }
}
