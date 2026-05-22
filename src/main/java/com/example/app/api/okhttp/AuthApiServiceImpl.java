package com.example.app.api.okhttp;

import com.example.app.api.ApiException;
import com.example.app.api.ApiUrl;
import com.example.app.utils.RSAUtils;
import com.google.inject.Inject;
import okhttp3.*;

import java.io.IOException;
import java.util.Objects;

/**
 * 认证模块ApiServiceImpl — login是特例，需要FormPost + 从response header提取token，
 * 无法通过父类通用方法完成，因此直接使用httpClient构建OkHttp Request。
 */
public class AuthApiServiceImpl extends OkHttpApiServiceImpl {

    @Inject
    public AuthApiServiceImpl() {
        // Guice创建子类实例时会先调用父类无参构造器
        // 父类OkHttpApiServiceImpl的无参构造器不会调用initHttp()
        // 需要手动调用initHttp()来初始化configStorage和httpClient
        initHttp();
    }

    public void login(String username, String password) throws ApiException {
        initRSAKey();
        String modulus = configStorage.getRSAModulus();
        String exponent = configStorage.getRSAExponent();

        if (modulus == null || exponent == null) {
            throw new ApiException("RSA Key not initialized");
        }

        String encryptedPassword = RSAUtils.encrypt(password, modulus, exponent);

        RequestBody body = new FormBody.Builder()
                .add("username", username)
                .add("password", encryptedPassword)
                .build();
        Request request = new Request.Builder()
                .url(ApiUrl.Authenticate.LOGIN_WITH_PASSWORD.getUrl(configStorage))
                .post(body)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            String responseBody = Objects.requireNonNull(response.body()).string();
            extractResBody(responseBody);

            String token = response.header("x-auth-token");
            if (token != null && !token.isEmpty()) {
                configStorage.updateAccessToken(token, 7200);
            } else {
                throw new ApiException("Login successful but x-auth-token missing");
            }
        } catch (IOException e) {
            throw new ApiException("Login failed", e);
        }
    }
}
