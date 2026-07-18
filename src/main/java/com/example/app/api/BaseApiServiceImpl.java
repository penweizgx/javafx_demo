package com.example.app.api;

import com.example.app.api.okhttp.executor.FormPostRequestExecutor;
import com.example.app.api.okhttp.executor.SimpleGetRequestExecutor;
import com.example.app.api.okhttp.executor.JsonPostRequestExecutor;
import com.example.app.api.storage.ConfigStorage;
import com.example.app.model.DatePeriod;
import com.example.app.model.InvalidBill;
import com.example.app.model.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

@Slf4j
public abstract class BaseApiServiceImpl<H, P> implements ApiService, RequestHttp<H, P> {
    protected ConfigStorage configStorage;
    private static final int retrySleepMillis = 1000;
    private static final int maxRetryTimes = 5;

    private static final Gson lenientGson = new GsonBuilder()
            .registerTypeAdapter(Integer.class, new LenientIntegerAdapter())
            .registerTypeAdapter(Long.class, new LenientLongAdapter())
            .registerTypeAdapter(int.class, new LenientIntegerAdapter())
            .registerTypeAdapter(long.class, new LenientLongAdapter())
            .registerTypeAdapter(InvalidBill.class, new LenientObjectDeserializer<>(InvalidBill.class))
            .registerTypeAdapter(DatePeriod.class, new LenientObjectDeserializer<>(DatePeriod.class))
            .create();

    protected Gson getGson() {
        return lenientGson;
    }

    @Inject
    public void setConfigStorage(ConfigStorage configStorage) {
        this.configStorage = configStorage;
    }

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
        return getGson().fromJson(resbody, beanClass);
    }

    protected <T> T extractResBodyAs(String responseContent, Type targetType) throws ApiException {
        JsonObject jsonObject = parseEnvelope(responseContent);
        JsonElement resbody = jsonObject.get("resbody");
        if (resbody == null || resbody.isJsonNull()) {
            throw new ApiException("响应数据为空");
        }
        return getGson().fromJson(resbody, targetType);
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

    @Override
    public Object getRSAKeyRequest() throws ApiException {
        return this.get(ApiUrl.Authenticate.PUBLIC_KEY.getUrl(configStorage));
    }

    @Override
    public User getCurrentUser() throws ApiException {
        String response = (String) this.get(ApiUrl.Authenticate.CURRENT_USER.getUrl(configStorage));
        JsonObject jsonObject = extractResBody(response);
        return getGson().fromJson(jsonObject, User.class);
    }

    private static class LenientIntegerAdapter extends TypeAdapter<Integer> {
        @Override
        public void write(JsonWriter out, Integer value) throws IOException {
            if (value == null) out.nullValue();
            else out.value(value);
        }

        @Override
        public Integer read(JsonReader in) throws IOException {
            try {
                return in.nextInt();
            } catch (NumberFormatException | IllegalStateException e) {
                in.skipValue();
                return null;
            }
        }
    }

    private static class LenientLongAdapter extends TypeAdapter<Long> {
        @Override
        public void write(JsonWriter out, Long value) throws IOException {
            if (value == null) out.nullValue();
            else out.value(value);
        }

        @Override
        public Long read(JsonReader in) throws IOException {
            try {
                return in.nextLong();
            } catch (NumberFormatException | IllegalStateException e) {
                in.skipValue();
                return null;
            }
        }
    }

    private static class LenientObjectDeserializer<T> implements JsonDeserializer<T> {
        private final Class<T> clazz;

        LenientObjectDeserializer(Class<T> clazz) {
            this.clazz = clazz;
        }

        @Override
        public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
            if (json == null || json.isJsonNull() || !json.isJsonObject()) {
                return null;
            }
            return new Gson().fromJson(json, clazz);
        }
    }
}