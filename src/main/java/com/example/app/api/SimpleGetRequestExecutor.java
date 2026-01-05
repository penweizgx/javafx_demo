package com.example.app.api;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

public class SimpleGetRequestExecutor<H, P> implements RequestExecutor<String, String> {
    protected RequestHttp<H, P> requestHttp;

    public SimpleGetRequestExecutor(RequestHttp<H, P> requestHttp) {
        this.requestHttp = requestHttp;
    }

    public static <H, P> SimpleGetRequestExecutor<H, P> create(RequestHttp<H, P> requestHttp) {
        return new SimpleGetRequestExecutor<>(requestHttp);
    }

    @Override
    public String execute(String uri, String data) throws ApiException, IOException {
        if (data != null) {
            if (uri.indexOf('?') == -1) {
                uri += '?';
            }
            uri += uri.endsWith("?") ? data : '&' + data;
        }

        // 根据HTTP客户端类型执行请求
        OkHttpClient client = (OkHttpClient) requestHttp.getRequestHttpClient();
        Request request = new Request.Builder().url(uri).build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }
}