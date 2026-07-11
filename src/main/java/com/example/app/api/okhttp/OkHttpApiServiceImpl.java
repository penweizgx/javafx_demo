package com.example.app.api.okhttp;

import com.example.app.api.*;
import com.example.app.api.storage.InMemoryConfigStorage;
import com.example.app.service.ApiMonitorService;
import com.google.inject.Inject;
import okhttp3.logging.HttpLoggingInterceptor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;

@Slf4j
public class OkHttpApiServiceImpl extends BaseApiServiceImpl<OkHttpClient, OkHttpProxyInfo> {
    protected OkHttpClient httpClient;
    private OkHttpProxyInfo httpProxy;

    @Inject
    private ApiMonitorService apiMonitorService;

    @Inject
    public OkHttpApiServiceImpl() {
    }

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
        log.debug("OkHttpApiServiceImpl initHttp");

        // configStorage 已通过 Guice 注入，无需重新创建
        if (configStorage == null) {
            configStorage = new InMemoryConfigStorage();
        }
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

            clientBuilder.addInterceptor(new ApiMonitorInterceptor(apiMonitorService));

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

            clientBuilder.addInterceptor(new ApiMonitorInterceptor(apiMonitorService));

            httpClient = clientBuilder.build();
        }
    }

}
