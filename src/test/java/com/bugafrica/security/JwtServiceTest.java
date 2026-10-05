package com.bugafrica.security;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class JwtServiceTest {

    private final JwtService service = new JwtService("0123456789012345678901234567890123456789", 60_000);

    @Test
    void generatedTokenIsValidAndContainsUsername() {
        String token = service.generateToken("alice");
        assertTrue(service.isValid(token));
        assertEquals("alice", service.extractUsername(token));
    }

    @Test
    void tamperedTokenIsRejected() {
        assertFalse(service.isValid(service.generateToken("alice") + "x"));
    }

    @Test
    void expiredTokenIsRejected() {
        JwtService expired = new JwtService("0123456789012345678901234567890123456789", -1000);
        assertFalse(expired.isValid(expired.generateToken("alice")));
    }

    @Test
    void shortSecretIsRefused() {
        assertThrows(IllegalStateException.class, () -> new JwtService("trop-court", 1000));
    }
}
