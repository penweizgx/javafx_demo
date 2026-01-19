package com.example.app.api.okhttp.executor;

import com.example.app.api.ApiException;
import com.example.app.api.RequestExecutor;
import com.example.app.api.RequestHttp;
import com.example.app.utils.GsonUtils;
import okhttp3.*;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

/**
 * 简单的POST请求执行器，请求的参数是String, 返回的结果也是String
 *
 * @author Daniel Qian
 */
public class JsonPostRequestExecutor<H, P> implements RequestExecutor<String, Map<String,Object>> {
    protected RequestHttp<H, P> requestHttp;

    public JsonPostRequestExecutor(RequestHttp<H, P> requestHttp) {
        this.requestHttp = requestHttp;
    }

    public static <H, P> JsonPostRequestExecutor<H, P> create(RequestHttp<H, P> requestHttp) {
        return new JsonPostRequestExecutor<>(requestHttp);
    }

    @Override
    public String execute(String uri, Map<String,Object> data) throws ApiException, IOException {
        OkHttpClient client = (OkHttpClient) requestHttp.getRequestHttpClient();
        RequestBody body = RequestBody.Companion.create(
                GsonUtils.toJSONString( data),
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
