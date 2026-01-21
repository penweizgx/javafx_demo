package com.example.app.api;

import com.example.app.model.User;
import java.util.Map;

// 1. 基础服务接口
public interface ApiService {
    Object get(String url);

    Object get(String url, Map<String, Object> queryParam);

    Object postJSON(String url, Map<String, Object> param);

    Object post(String url, Map<String, Object> param);

    <T, E> T execute(RequestExecutor<T, E> executor, String uri, E data);

    String getAccessToken();

    /**
     * 初始化http请求对象.
     */
    void initHttp();

    /**
     * User login.
     * 
     * @param username username
     * @param password password
     */
    void login(String username, String password);

    Object getRSAKeyRequest();

    User getCurrentUser();
}