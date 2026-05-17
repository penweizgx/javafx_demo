package com.example.app.service;

import com.example.app.model.User;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface UserManageService {
    CompletableFuture<List<User>> getUserList();
    CompletableFuture<User> getUserById(String id);
    CompletableFuture<User> createUser(User user);
    CompletableFuture<User> updateUser(User user);
    CompletableFuture<Void> deleteUser(String id);
}
