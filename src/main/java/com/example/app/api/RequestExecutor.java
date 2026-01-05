package com.example.app.api;

import java.io.IOException;

// 3. 请求执行器接口
public interface RequestExecutor<T, E> {
    T execute(String uri, E data) throws ApiException, IOException;
}