package com.example.app.service.impl;

import com.example.app.model.OrgBound;
import com.example.app.model.User;
import com.example.app.service.UserManageService;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class MockUserManageService implements UserManageService {

    private final Map<String, User> userStore = new ConcurrentHashMap<>();

    public MockUserManageService() {
        initMockData();
    }

    private void initMockData() {
        String[][] mockUsers = {
            {"1", "张三", "zhangsan@example.com", "13800138001", "active", "管理员", "技术部", "2024-01-15"},
            {"2", "李四", "lisi@example.com", "13800138002", "active", "普通用户", "市场部", "2024-02-20"},
            {"3", "王五", "wangwu@example.com", "13800138003", "inactive", "普通用户", "财务部", "2024-03-10"},
            {"4", "赵六", "zhaoliu@example.com", "13800138004", "active", "管理员", "人事部", "2024-04-05"},
            {"5", "钱七", "qianqi@example.com", "13800138005", "active", "普通用户", "技术部", "2024-05-12"},
            {"6", "孙八", "sunba@example.com", "13800138006", "pending", "普通用户", "市场部", "2024-06-01"},
            {"7", "周九", "zhoujiu@example.com", "13800138007", "active", "普通用户", "技术部", "2024-07-18"},
            {"8", "吴十", "wushi@example.com", "13800138008", "inactive", "普通用户", "财务部", "2024-08-25"},
        };

        for (String[] data : mockUsers) {
            User user = new User();
            user.setId(data[0]);
            user.setName(data[1]);
            user.setEmail(data[2]);
            user.setPhone(data[3]);
            user.setStatus(data[4]);
            user.setRole(data[5]);
            user.setOrgName(data[6]);
            user.setCreateTime(data[7]);
            user.setOrgBound(OrgBound.DEPARTMENT);
            userStore.put(user.getId(), user);
        }
        log.info("Mock user data initialized with {} users", userStore.size());
    }

    @Override
    public CompletableFuture<List<User>> getUserList() {
        return CompletableFuture.supplyAsync(() -> {
            simulateDelay();
            return new ArrayList<>(userStore.values());
        });
    }

    @Override
    public CompletableFuture<User> getUserById(String id) {
        return CompletableFuture.supplyAsync(() -> {
            simulateDelay();
            User user = userStore.get(id);
            if (user == null) {
                throw new RuntimeException("用户不存在: " + id);
            }
            return user;
        });
    }

    @Override
    public CompletableFuture<User> createUser(User user) {
        return CompletableFuture.supplyAsync(() -> {
            simulateDelay();
            String newId = String.valueOf(userStore.size() + 1);
            user.setId(newId);
            user.setCreateTime("2024-12-01");
            userStore.put(newId, user);
            log.info("Created user: {}", user.getName());
            return user;
        });
    }

    @Override
    public CompletableFuture<User> updateUser(User user) {
        return CompletableFuture.supplyAsync(() -> {
            simulateDelay();
            if (!userStore.containsKey(user.getId())) {
                throw new RuntimeException("用户不存在: " + user.getId());
            }
            userStore.put(user.getId(), user);
            log.info("Updated user: {}", user.getName());
            return user;
        });
    }

    @Override
    public CompletableFuture<Void> deleteUser(String id) {
        return CompletableFuture.runAsync(() -> {
            simulateDelay();
            if (userStore.remove(id) == null) {
                throw new RuntimeException("用户不存在: " + id);
            }
            log.info("Deleted user: {}", id);
        });
    }

    private void simulateDelay() {
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
