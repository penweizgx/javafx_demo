package com.example.app.storage;

import lombok.extern.slf4j.Slf4j;

import java.util.prefs.Preferences;

@Slf4j
public class TokenStorage {
    
    private static final String TOKEN_KEY = "auth.token";
    private static final String EXPIRES_KEY = "auth.expires";
    
    private final Preferences prefs = Preferences.userNodeForPackage(TokenStorage.class);
    
    public void saveToken(String token, long expiresAt) {
        prefs.put(TOKEN_KEY, token);
        prefs.putLong(EXPIRES_KEY, expiresAt);
        log.debug("Token saved, expires at {}", expiresAt);
    }
    
    public String loadToken() {
        return prefs.get(TOKEN_KEY, null);
    }
    
    public long loadExpiresAt() {
        return prefs.getLong(EXPIRES_KEY, 0);
    }
    
    public void clearToken() {
        prefs.remove(TOKEN_KEY);
        prefs.remove(EXPIRES_KEY);
        log.debug("Token cleared");
    }
    
    public boolean hasValidToken() {
        String token = loadToken();
        long expiresAt = loadExpiresAt();
        return token != null && !token.isEmpty() && System.currentTimeMillis() < expiresAt;
    }
}
