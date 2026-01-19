package com.example.app.api.okhttp.executor;

import com.example.app.api.ApiException;
import com.example.app.api.RequestExecutor;
import com.example.app.api.RequestHttp;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

public class SimpleGetRequestExecutor<H, P> implements RequestExecutor<String, Map<String,Object>> {
    protected RequestHttp<H, P> requestHttp;

    public SimpleGetRequestExecutor(RequestHttp<H, P> requestHttp) {
        this.requestHttp = requestHttp;
    }

    public static <H, P> SimpleGetRequestExecutor<H, P> create(RequestHttp<H, P> requestHttp) {
        return new SimpleGetRequestExecutor<>(requestHttp);
    }

    @Override
    public String execute(String uri, Map<String,Object> data) throws ApiException, IOException {
        if (data != null) {
            String queryString = data.entrySet().stream()
                    .map(entry -> entry.getKey() + "=" + entry.getValue())
                    .collect(Collectors.joining("&"));

            if (uri.indexOf('?') == -1) {
                uri += '?';
            }
            uri += uri.endsWith("?") ? queryString : '&' + queryString;
        }

        // 根据HTTP客户端类型执行请求
        OkHttpClient client = (OkHttpClient) requestHttp.getRequestHttpClient();
        Request request = new Request.Builder().url(uri).build();
        try (Response response = client.newCall(request).execute();) {
            return response.body().string();
        }
    }
}