package com.example.app.api.okhttp;

import com.example.app.api.*;
import com.example.app.api.storage.ConfigStorage;
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

        ConfigStorage configStorage = getWxMpConfigStorage();
        //设置代理
        if (configStorage.getHttpProxyHost() != null && configStorage.getHttpProxyPort() > 0) {
            httpProxy = OkHttpProxyInfo.httpProxy(configStorage.getHttpProxyHost(),
                    configStorage.getHttpProxyPort(),
                    configStorage.getHttpProxyUsername(),
                    configStorage.getHttpProxyPassword());
            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
            clientBuilder.proxy(getRequestHttpProxy().getProxy());

            //设置授权
            clientBuilder.proxyAuthenticator((route, response) -> {
                String credential = Credentials.basic(httpProxy.getProxyUsername(), httpProxy.getProxyPassword());
                return response.request().newBuilder()
                        .header("Proxy-Authorization", credential)
                        .build();
            });
            httpClient = clientBuilder.build();
        } else {
            httpClient = DefaultOkHttpClientBuilder.get().build();
        }
    }

    @Override
    protected String getRSAKeyRequest() throws ApiException {
        Request request = new Request.Builder().url(ApiUrl.Authenticate.PUBLIC_KEY.getUrl(configStorage)).get().build();
        try (Response response = httpClient.newCall(request).execute()) {
            return Objects.requireNonNull(response.body()).string();
        } catch (IOException e) {
            throw new ApiException("获取access_token失败", e);
        }
    }

    @Override
    protected String getAccessTokenRequest() throws ApiException {

        Request request = new Request.Builder().url(ApiUrl.Authenticate.PUBLIC_KEY.getUrl(configStorage)).get().build();
        try (Response response = httpClient.newCall(request).execute()) {
            return Objects.requireNonNull(response.body()).string();
        } catch (IOException e) {
            throw new ApiException("获取access_token失败", e);
        }
    }
}