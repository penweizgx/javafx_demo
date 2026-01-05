package com.example.app.api.executor;

import com.example.app.api.ApiException;
import com.example.app.api.RequestHttp;
import okhttp3.*;
import java.io.IOException;
import java.util.Objects;

/**
 * 简单的POST请求执行器，请求的参数是String, 返回的结果也是String
 *
 * @author Daniel Qian
 */
public class SimplePostRequestExecutor<H, P> implements RequestExecutor<String, String> {
    protected RequestHttp<H, P> requestHttp;

    public SimplePostRequestExecutor(RequestHttp<H, P> requestHttp) {
        this.requestHttp = requestHttp;
    }

    public static <H, P> SimplePostRequestExecutor<H, P> create(RequestHttp<H, P> requestHttp) {
        return new SimplePostRequestExecutor<>(requestHttp);
    }

    @Override
    public String execute(String uri, String data) throws ApiException, IOException {
        OkHttpClient client = (OkHttpClient) requestHttp.getRequestHttpClient();
        RequestBody body = RequestBody.Companion.create(
                data,
                MediaType.parse("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(uri)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return Objects.requireNonNull(response.body()).string();
        }
    }
}
