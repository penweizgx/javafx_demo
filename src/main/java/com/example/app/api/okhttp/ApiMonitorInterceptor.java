package com.example.app.api.okhttp;

import com.example.app.AppContext;
import com.example.app.model.ApiRequestLog;
import com.example.app.service.ApiMonitorService;
import okhttp3.*;
import okio.Buffer;
import okio.BufferedSource;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class ApiMonitorInterceptor implements Interceptor {

    private static final int MAX_RESPONSE_LENGTH = 2000;

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Request request = chain.request();
        ApiRequestLog log = new ApiRequestLog();
        log.setTimestamp(Instant.now().toEpochMilli());
        log.setMethod(request.method());
        log.setUrl(request.url().toString());

        log.setRequestParams(request.url().query());
        log.setRequestBody(readRequestBody(request));

        long startNanos = System.nanoTime();
        Response response;
        try {
            response = chain.proceed(request);
        } catch (IOException e) {
            long durationMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNanos);
            log.setDurationMs(durationMs);
            log.setError(e.getMessage());
            addLog(log);
            throw e;
        }

        long durationMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNanos);
        log.setDurationMs(durationMs);
        log.setStatusCode(response.code());

        ResponseBody responseBody = response.body();
        if (responseBody != null) {
            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE);
            Buffer buffer = source.getBuffer();
            Charset charset = StandardCharsets.UTF_8;
            MediaType contentType = responseBody.contentType();
            if (contentType != null) {
                charset = contentType.charset(StandardCharsets.UTF_8);
            }
            String bodyString = buffer.clone().readString(charset);
            if (bodyString.length() > MAX_RESPONSE_LENGTH) {
                bodyString = bodyString.substring(0, MAX_RESPONSE_LENGTH) + "...(truncated)";
            }
            log.setResponseSummary(bodyString);
        }

        addLog(log);
        return response;
    }

    private void addLog(ApiRequestLog log) {
        try {
            ApiMonitorService monitorService = AppContext.get().getService(ApiMonitorService.class);
            monitorService.addLog(log);
        } catch (Exception ignored) {
        }
    }

    private String readRequestBody(Request request) {
        if (request.body() == null) return null;
        try {
            Buffer buffer = new Buffer();
            request.body().writeTo(buffer);
            String body = buffer.readString(StandardCharsets.UTF_8);
            if (body.length() > MAX_RESPONSE_LENGTH) {
                body = body.substring(0, MAX_RESPONSE_LENGTH) + "...(truncated)";
            }
            return body;
        } catch (IOException e) {
            return "[unreadable: " + e.getMessage() + "]";
        }
    }
}
