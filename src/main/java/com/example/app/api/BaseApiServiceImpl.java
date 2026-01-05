package com.example.app.api;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public abstract class BaseApiServiceImpl<H, P> implements ApiService, RequestHttp<H, P> {
    protected ConfigStorage configStorage;
    private int retrySleepMillis = 1000;
    private int maxRetryTimes = 5;

    @Override
    public String get(String url, String queryParam) throws ApiException {
        return execute(SimpleGetRequestExecutor.create(this), url, queryParam);
    }

    @Override
    public String post(String url, String postData) throws ApiException {
        return execute(SimplePostRequestExecutor.create(this), url, postData);
    }

    @Override
    public <T, E> T execute(RequestExecutor<T, E> executor, String uri, E data) throws ApiException {
        int retryTimes = 0;
        do {
            try {
                return this.executeInternal(executor, uri, data);
            } catch (ApiException e) {
                if (retryTimes + 1 > this.maxRetryTimes) {
                    throw new ApiException("重试达到最大次数【" + this.maxRetryTimes + "】");
                }

                if (e.getErrorCode() == -1) { // 系统繁忙
                    int sleepMillis = this.retrySleepMillis * (1 << retryTimes);
                    try {
                        Thread.sleep(sleepMillis);
                    } catch (InterruptedException e1) {
                        Thread.currentThread().interrupt();
                    }
                } else {
                    throw e;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } while (retryTimes++ < this.maxRetryTimes);

        throw new ApiException("重试达到最大次数【" + this.maxRetryTimes + "】");
    }

    protected <T, E> T executeInternal(RequestExecutor<T, E> executor, String uri, E data) throws ApiException, IOException {
        // 添加access token
        if (uri.contains("access_token")) {
            throw new IllegalArgumentException("uri参数中不允许有access_token");
        }

        String accessToken = this.getAccessToken();
        if (accessToken != null && !uri.contains("access_token=")) {
            if (uri.indexOf('?') == -1) {
                uri += '?';
            }
            uri += uri.endsWith("?") ? "access_token=" + accessToken : "&access_token=" + accessToken;
        }

        return executor.execute(uri, data);
    }

    public void initRSAKey() {
       if(!this.configStorage.isInitRSAKey()){
           // 获取锁的逻辑
           synchronized (this) {
               if (this.configStorage.isInitRSAKey()) {
                   String rsaKeyRequest = this.getRSAKeyRequest();
                   log.debug("RSAKeyRequest: {}", rsaKeyRequest);
               }
           }
       }
    }
    @Override
    public String getAccessToken() throws ApiException {
        if (this.configStorage.isAccessTokenExpired()) {
            // 获取锁的逻辑
            synchronized (this) {
                if (this.configStorage.isAccessTokenExpired()) {
                    String responseContent = this.getAccessTokenRequest();
                    // 解析响应并更新token
                    this.configStorage.updateAccessToken("new_token", 7200);
                }
            }
        }
        return this.configStorage.getAccessToken();
    }

    protected ConfigStorage getWxMpConfigStorage() {
        return new InMemoryConfigStorage();
    }


    protected abstract String getRSAKeyRequest() throws ApiException;

    protected abstract String getAccessTokenRequest() throws ApiException;
}