package com.example.app.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TokenStorageTest {

    private TokenStorage tokenStorage;

    @BeforeEach
    void setUp() {
        tokenStorage = new TokenStorage();
        tokenStorage.clearToken();
    }

    @Test
    void saveToken_ShouldStoreToken() {
        String token = "test-token";
        long expiresAt = System.currentTimeMillis() + 3600000;

        tokenStorage.saveToken(token, expiresAt);

        assertEquals(token, tokenStorage.loadToken());
        assertEquals(expiresAt, tokenStorage.loadExpiresAt());
    }

    @Test
    void clearToken_ShouldRemoveToken() {
        tokenStorage.saveToken("token", System.currentTimeMillis() + 3600000);

        tokenStorage.clearToken();

        assertNull(tokenStorage.loadToken());
        assertEquals(0, tokenStorage.loadExpiresAt());
    }

    @Test
    void hasValidToken_WithValidToken_ShouldReturnTrue() {
        tokenStorage.saveToken("token", System.currentTimeMillis() + 3600000);

        assertTrue(tokenStorage.hasValidToken());
    }

    @Test
    void hasValidToken_WithExpiredToken_ShouldReturnFalse() {
        tokenStorage.saveToken("token", System.currentTimeMillis() - 1000);

        assertFalse(tokenStorage.hasValidToken());
    }

    @Test
    void hasValidToken_WithNoToken_ShouldReturnFalse() {
        tokenStorage.clearToken();

        assertFalse(tokenStorage.hasValidToken());
    }
}
