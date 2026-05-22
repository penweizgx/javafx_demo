package com.example.app.api;

import com.example.app.api.okhttp.executor.FormPostRequestExecutor;
import com.example.app.api.okhttp.executor.SimpleGetRequestExecutor;
import com.example.app.api.okhttp.executor.JsonPostRequestExecutor;
import com.example.app.api.storage.ConfigStorage;
import com.example.app.api.storage.InMemoryConfigStorage;
import com.example.app.model.User;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

@Slf4j
public abstract class BaseApiServiceImpl<H, P> implements ApiService, RequestHttp<H, P> {
    protected ConfigStorage configStorage;
    private static final int retrySleepMillis = 1000;
    private static final int maxRetryTimes = 5;

    @Override
    public Object get(String url) {
        return this.get(url, null);
    }

    @Override
    public Object get(String url, Map<String, Object> queryParam) {
        return execute(SimpleGetRequestExecutor.create(this), url, queryParam);
    }

    @Override
    public Object postJSON(String url, Map<String, Object> param) {
        return execute(JsonPostRequestExecutor.create(this), url, param);
    }

    @Override
    public Object post(String url, Map<String, Object> param) {
        return execute(FormPostRequestExecutor.create(this), url, param);
    }

    @Override
    public <T, E> T execute(RequestExecutor<T, E> executor, String uri, E data) throws ApiException {
        int retryTimes = 0;
        do {
            try {
                return executor.execute(uri, data);
            } catch (ApiException e) {
                if (retryTimes + 1 > maxRetryTimes) {
                    throw new ApiException("重试达到最大次数【" + maxRetryTimes + "】");
                }

                if (e.getErrorCode() == -1) { // 系统繁忙
                    int sleepMillis = retrySleepMillis * (1 << retryTimes);
                    try {
                        Thread.sleep(sleepMillis);
                    } catch (InterruptedException e1) {
                        Thread.currentThread().interrupt();
                    }
                } else {
                    throw e;
                }
            } catch (IOException e) {
                log.error("url:{}",uri);
                throw new RuntimeException(e);
            }
        } while (retryTimes++ < maxRetryTimes);

        throw new ApiException("重试达到最大次数【" + maxRetryTimes + "】");
    }

    public void initRSAKey() {
        if (!this.configStorage.isInitRSAKey()) {
            synchronized (this) {
                if (!this.configStorage.isInitRSAKey()) {
                    try {
                        String rsaKeyRequest = this.getRSAKeyRequest().toString();
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
        JsonObject jsonObject = parseEnvelope(responseContent);
        JsonElement resbody = jsonObject.get("resbody");
        if (resbody == null || resbody.isJsonNull()) {
            return null;
        }
        return resbody.isJsonObject() ? resbody.getAsJsonObject() : null;
    }

    protected <T> T extractResBodyAs(String responseContent, Class<T> beanClass) throws ApiException {
        JsonObject jsonObject = parseEnvelope(responseContent);
        JsonElement resbody = jsonObject.get("resbody");
        if (resbody == null || resbody.isJsonNull()) {
            throw new ApiException("响应数据为空");
        }
        return new Gson().fromJson(resbody, beanClass);
    }

    protected <T> T extractResBodyAs(String responseContent, Type targetType) throws ApiException {
        JsonObject jsonObject = parseEnvelope(responseContent);
        JsonElement resbody = jsonObject.get("resbody");
        if (resbody == null || resbody.isJsonNull()) {
            throw new ApiException("响应数据为空");
        }
        return new Gson().fromJson(resbody, targetType);
    }

    private JsonObject parseEnvelope(String responseContent) throws ApiException {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(responseContent, JsonObject.class);
        if (jsonObject.has("code") && jsonObject.get("code").getAsInt() != 200) {
            String message = jsonObject.has("message") ? jsonObject.get("message").getAsString() : "未知错误";
            throw new ApiException(message, jsonObject.get("code").getAsInt());
        }
        return jsonObject;
    }

    @Override
    public String getAccessToken() throws ApiException {
        return this.configStorage.getAccessToken();
    }

    protected ConfigStorage getWxMpConfigStorage() {
        return new InMemoryConfigStorage();
    }

    @Override
    public Object getRSAKeyRequest() throws ApiException {
        return this.get(ApiUrl.Authenticate.PUBLIC_KEY.getUrl(configStorage));
    }

    @Override
    public User getCurrentUser() throws ApiException {
        String response = (String) this.get(ApiUrl.Authenticate.CURRENT_USER.getUrl(configStorage));
        JsonObject jsonObject = extractResBody(response);
        return new Gson().fromJson(jsonObject, User.class);
    }
}