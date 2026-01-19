package com.example.app.api.okhttp.executor;

import com.example.app.api.ApiException;
import com.example.app.api.RequestExecutor;
import com.example.app.api.RequestHttp;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

/**
 * 简单的POST请求执行器，请求的参数是String, 返回的结果也是String
 *
 * @author Daniel Qian
 */
@Slf4j
public class FormPostRequestExecutor<H, P> implements RequestExecutor<String, Map<String,Object>> {
    protected RequestHttp<H, P> requestHttp;

    public FormPostRequestExecutor(RequestHttp<H, P> requestHttp) {
        this.requestHttp = requestHttp;
    }

    public static <H, P> FormPostRequestExecutor<H, P> create(RequestHttp<H, P> requestHttp) {
        return new FormPostRequestExecutor<>(requestHttp);
    }

    @Override
    public String execute(String url, Map<String,Object> param) throws ApiException {
        // 创建一个OkHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient();
        // 创建一个RequestBody(参数1：数据类型 参数2传递的json串)
        FormBody.Builder builder = new FormBody.Builder();

        for (String key : param.keySet()) {
            Object obj = param.get(key);
            if (obj != null) {
                builder.addEncoded(key, param.get(key).toString());
            } else {
                builder.addEncoded(key, "");
            }
        }
        FormBody requestBody = builder.build();

        // 创建一个请求对象
        Request request = new Request.Builder().url(url).post(requestBody).build();
        // 发送请求获取响应
        try ( Response response = okHttpClient.newCall(request).execute();){
            return Objects.requireNonNull(response.body()).string();
        } catch (IOException e) {
            log.error("FormPostRequest请求失败",e);
            throw new ApiException("FormPostRequest请求失败",e);
        }
    }
}
