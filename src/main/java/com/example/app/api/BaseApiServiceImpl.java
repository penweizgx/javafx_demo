package com.example.app.api;

import com.example.app.api.executor.RequestExecutor;
import com.example.app.api.executor.SimpleGetRequestExecutor;
import com.example.app.api.executor.SimplePostRequestExecutor;
import com.example.app.api.storage.ConfigStorage;
import com.example.app.api.storage.InMemoryConfigStorage;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
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

    protected <T, E> T executeInternal(RequestExecutor<T, E> executor, String uri, E data)
            throws ApiException, IOException {
        // Token is now handled by header interceptor in implementation classes
        return executor.execute(uri, data);
    }

    public void initRSAKey() {
        if (!this.configStorage.isInitRSAKey()) {
            synchronized (this) {
                if (!this.configStorage.isInitRSAKey()) {
                    try {
                        String rsaKeyRequest = this.getRSAKeyRequest();
                        log.debug("RSAKeyRequest: {}", rsaKeyRequest);
                        JsonObject data = extractResBody(rsaKeyRequest);
                        if (data != null) {
                            String modulus = data.get("modulus").getAsString();
                            String exponent = data.get("exponent").getAsString();
                            this.configStorage.updateRSAKey(modulus, exponent);
                        }
                    } catch (ApiException e) {
                        log.error("Init RSA Key failed", e);
                    }
                }
            }
        }
    }

    protected JsonObject extractResBody(String responseContent) throws ApiException {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(responseContent, JsonObject.class);
        if (jsonObject.has("code") && jsonObject.get("code").getAsInt() != 200) {
            throw new ApiException(jsonObject.get("message").getAsString(), jsonObject.get("code").getAsInt());
        }
        if (jsonObject.has("resbody")) {
            return jsonObject.getAsJsonObject("resbody");
        }
        return null;
    }

    @Override
    public String getAccessToken() throws ApiException {
        return this.configStorage.getAccessToken();
    }

    protected ConfigStorage getWxMpConfigStorage() {
        return new InMemoryConfigStorage();
    }

    protected abstract String getRSAKeyRequest() throws ApiException;

    protected abstract String getCurrentUser() throws ApiException;
}