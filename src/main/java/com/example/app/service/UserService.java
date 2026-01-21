package com.example.app.service;

import com.example.app.model.User;
import java.util.concurrent.CompletableFuture;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 异步获取当前用户信息
     * 
     * @return 用户信息的CompletableFuture
     */
    CompletableFuture<User> getCurrentUser();

    /**
     * 获取缓存的用户信息（如果有）
     * 
     * @return 缓存的用户信息，如果没有则返回null
     */
    User getCachedUser();

    /**
     * 清除缓存的用户信息
     */
    void clearCache();
}
