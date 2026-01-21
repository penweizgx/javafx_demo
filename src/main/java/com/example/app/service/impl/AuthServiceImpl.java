package com.example.app.service.impl;

import com.example.app.api.ApiService;
import com.example.app.service.AuthService;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

/**
 * 认证服务实现
 */
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final ApiService apiService;
    private boolean authenticated = false;

    public AuthServiceImpl(ApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public CompletableFuture<Void> login(String username, String password) {
        return CompletableFuture.runAsync(() -> {
            try {
                apiService.login(username, password);
                authenticated = true;
                log.info("User {} logged in successfully", username);
            } catch (Exception e) {
                log.error("Login failed for user {}", username, e);
                throw new RuntimeException("登录失败: " + e.getMessage(), e);
            }
        });
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void logout() {
        authenticated = false;
        log.info("User logged out");
    }
}
