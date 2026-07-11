package com.example.app.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiRequestLog {
    private long id;
    private long timestamp;
    private String method;
    private String url;
    private String requestParams;
    private String requestBody;
    private int statusCode;
    private String responseSummary;
    private long durationMs;
    private String error;
}
