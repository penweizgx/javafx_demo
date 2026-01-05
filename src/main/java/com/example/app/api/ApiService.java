package com.example.app.api;

import com.example.app.api.executor.RequestExecutor;

// 1. 基础服务接口
public interface ApiService {
    String get(String url, String queryParam) throws ApiException;
    String post(String url, String postData) throws ApiException;
    <T, E> T execute(RequestExecutor<T, E> executor, String uri, E data) throws ApiException;
    String getAccessToken() throws ApiException;
    /**
     * 初始化http请求对象.
     */
    void initHttp();
}