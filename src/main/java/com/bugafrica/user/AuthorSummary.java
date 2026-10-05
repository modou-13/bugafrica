package com.bugafrica.user;

/** Version publique d'un utilisateur (sans email) utilisée dans les bugs et les réponses. */
public record AuthorSummary(Long id, String username, String country, int reputation) {

    public static AuthorSummary from(User u) {
        return new AuthorSummary(u.getId(), u.getUsername(), u.getCountry(), u.getReputation());
    }
}
