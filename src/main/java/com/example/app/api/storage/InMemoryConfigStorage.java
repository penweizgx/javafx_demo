package com.example.app.api.storage;

import org.apache.commons.lang3.StringUtils;

public class InMemoryConfigStorage implements ConfigStorage {
    private HostConfig apiHost;
    private String accessToken;
    private long expiresTime;
    private String rsaExponent;
    private String rsaModulus;

    private String httpProxyHost;
    private int httpProxyPort;
    private String httpProxyUsername;
    private String httpProxyPassword;

    @Override
    public HostConfig getApiHost() {
        return apiHost;
    }

    @Override
    public String getAccessToken() {
        return this.accessToken;
    }

    @Override
    public void updateAccessToken(String accessToken, int expiresInSeconds) {
        this.accessToken = accessToken;
        this.expiresTime = System.currentTimeMillis() + (expiresInSeconds - 200) * 1000L;
    }

    @Override
    public void updateRSAKey(String modulus, String exponent) {
        this.rsaExponent = modulus;
        this.rsaModulus = exponent;
    }

    @Override
    public boolean isInitRSAKey() {
        return StringUtils.isNoneBlank(this.rsaExponent) && StringUtils.isNoneBlank(this.rsaModulus);
    }

    @Override
    public boolean isAccessTokenExpired() {
        return System.currentTimeMillis() > this.expiresTime;
    }

    @Override
    public void expireAccessToken() {
        this.expiresTime = 0;
    }

    @Override
    public String getHttpProxyHost() {
        return this.httpProxyHost;
    }

    @Override
    public int getHttpProxyPort() {
        return this.httpProxyPort;
    }

    @Override
    public String getHttpProxyUsername() {
        return this.httpProxyUsername;
    }

    @Override
    public String getHttpProxyPassword() {
        return this.httpProxyPassword;
    }
}