package com.example.app.api;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class FileUploadRequestExecutor<H, P> implements RequestExecutor<String, File> {
    protected RequestHttp<H, P> requestHttp;

    public FileUploadRequestExecutor(RequestHttp<H, P> requestHttp) {
        this.requestHttp = requestHttp;
    }

    @Override
    public String execute(String uri, File file) throws ApiException, IOException {
            return executeApache(uri, file);
    }

    private String executeApache(String uri, File file) throws IOException {
        CloseableHttpClient client = (CloseableHttpClient) requestHttp.getRequestHttpClient();
        HttpPost httpPost = new HttpPost(uri);

        MultipartEntityBuilder builder = MultipartEntityBuilder.create()
                .addBinaryBody("media", file)
                .setMode(HttpMultipartMode.RFC6532);
        httpPost.setEntity(builder.build());

        try (CloseableHttpResponse response = client.execute(httpPost)) {
            return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        }
    }
}
