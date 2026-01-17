package com.example.app.api.okhttp;

import com.example.app.api.*;
import com.example.app.utils.RSAUtils;

import okhttp3.logging.HttpLoggingInterceptor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.util.Objects;

@Slf4j
public class OkHttpApiServiceImpl extends BaseApiServiceImpl<OkHttpClient, OkHttpProxyInfo> {
    private OkHttpClient httpClient;
    private OkHttpProxyInfo httpProxy;

    @Override
    public OkHttpClient getRequestHttpClient() {
        return httpClient;
    }

    @Override
    public OkHttpProxyInfo getRequestHttpProxy() {
        return httpProxy;
    }

    @Override
    public void initHttp() {
        log.debug("WxChannelServiceOkHttpImpl initHttp");

        this.configStorage = getWxMpConfigStorage();
        // 设置代理
        if (configStorage.getHttpProxyHost() != null && configStorage.getHttpProxyPort() > 0) {
            httpProxy = OkHttpProxyInfo.httpProxy(configStorage.getHttpProxyHost(),
                    configStorage.getHttpProxyPort(),
                    configStorage.getHttpProxyUsername(),
                    configStorage.getHttpProxyPassword());
            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
            clientBuilder.proxy(getRequestHttpProxy().getProxy());

            // 设置授权
            clientBuilder.addInterceptor(chain -> {
                Request original = chain.request();
                Request.Builder requestBuilder = original.newBuilder();
                String token = configStorage.getAccessToken();
                if (token != null) {
                    requestBuilder.header("x-auth-token", token);
                }
                return chain.proceed(requestBuilder.build());
            });

            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            clientBuilder.addInterceptor(loggingInterceptor);

            httpClient = clientBuilder.build();
        } else {
            OkHttpClient baseClient = DefaultOkHttpClientBuilder.get().build();
            OkHttpClient.Builder clientBuilder = baseClient.newBuilder();
            // Use COMPATIBLE_TLS to support wider range of handshake patterns
            clientBuilder
                    .connectionSpecs(java.util.Arrays.asList(ConnectionSpec.MODERN_TLS, ConnectionSpec.COMPATIBLE_TLS));

            clientBuilder.addInterceptor(chain -> {
                Request original = chain.request();
                Request.Builder requestBuilder = original.newBuilder();
                requestBuilder.header("User-Agent",
                        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.114 Safari/537.36");
                String token = configStorage.getAccessToken();
                if (token != null) {
                    requestBuilder.header("x-auth-token", token);
                }
                return chain.proceed(requestBuilder.build());
            });

            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            clientBuilder.addInterceptor(loggingInterceptor);

            httpClient = clientBuilder.build();
        }
    }

    @Override
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
            // Check response code from body wrapper
            extractResBody(responseBody);

            // Extract token from header
            String token = response.header("x-auth-token");
            if (token != null && !token.isEmpty()) {
                configStorage.updateAccessToken(token, 7200); // Default 2 hours
            } else {
                throw new ApiException("Login successful but x-auth-token missing");
            }
        } catch (IOException e) {
            throw new ApiException("Login failed", e);
        }
    }

    @Override
    protected String getRSAKeyRequest() throws ApiException {
        Request request = new Request.Builder().url(ApiUrl.Authenticate.PUBLIC_KEY.getUrl(configStorage)).get().build();
        try (Response response = httpClient.newCall(request).execute()) {
            return Objects.requireNonNull(response.body()).string();
        } catch (IOException e) {
            throw new ApiException("获取RSA Public Key失败", e);
        }
    }

    @Override
    public String getCurrentUser() throws ApiException {
        Request request = new Request.Builder().url(ApiUrl.Authenticate.CURRENT_USER.getUrl(configStorage)).get().build();
        try (Response response = httpClient.newCall(request).execute()) {
            return Objects.requireNonNull(response.body()).string();
        } catch (IOException e) {
            throw new ApiException("获取当前用户信息失败", e);
        }
    }
}
