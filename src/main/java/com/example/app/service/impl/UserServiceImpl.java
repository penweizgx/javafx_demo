package com.example.app.service.impl;

import com.example.app.api.ApiService;
import com.example.app.model.User;
import com.example.app.service.UserService;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

/**
 * 用户服务实现
 */
@Slf4j
public class UserServiceImpl implements UserService {

    private final ApiService apiService;
    private User cachedUser;

    public UserServiceImpl(ApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public CompletableFuture<User> getCurrentUser() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                User user = apiService.getCurrentUser();
                cachedUser = user;
                log.info("Fetched current user: {}", user.getName());
                return user;
            } catch (Exception e) {
                log.error("Failed to fetch current user", e);
                throw new RuntimeException("获取用户信息失败: " + e.getMessage(), e);
            }
        });
    }

    @Override
    public User getCachedUser() {
        return cachedUser;
    }

    @Override
    public void clearCache() {
        cachedUser = null;
        log.info("User cache cleared");
    }
}
