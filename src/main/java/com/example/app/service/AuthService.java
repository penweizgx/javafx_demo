package com.example.app.service;

import java.util.concurrent.CompletableFuture;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 异步执行登录
     * 
     * @param username 用户名
     * @param password 密码
     * @return 登录结果的CompletableFuture
     */
    CompletableFuture<Void> login(String username, String password);

    /**
     * 检查是否已认证
     * 
     * @return 如果已认证返回true
     */
    boolean isAuthenticated();

    /**
     * 登出
     */
    void logout();
}
