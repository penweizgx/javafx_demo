package com.example.app.service.impl;

import com.example.app.api.ApiService;
import com.example.app.exception.ExceptionHandler;
import com.example.app.service.AuthService;
import com.example.app.storage.TokenStorage;
import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

@Slf4j
public class AuthServiceImpl implements AuthService {

    private final ApiService apiService;
    private final TokenStorage tokenStorage;
    private boolean authenticated = false;

    @Inject
    public AuthServiceImpl(ApiService apiService, TokenStorage tokenStorage) {
        this.apiService = apiService;
        this.tokenStorage = tokenStorage;
    }

    @Override
    public CompletableFuture<Void> login(String username, String password) {
        return CompletableFuture.runAsync(() -> {
            try {
                apiService.login(username, password);
                authenticated = true;
                log.info("User {} logged in successfully", username);
            } catch (Exception e) {
                ExceptionHandler.handle(e, "Login failed for user " + username);
                throw new RuntimeException("登录失败: " + e.getMessage(), e);
            }
        });
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated || tokenStorage.hasValidToken();
    }

    @Override
    public void logout() {
        authenticated = false;
        tokenStorage.clearToken();
        log.info("User logged out");
    }
}