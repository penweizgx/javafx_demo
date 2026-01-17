package com.example.app.api.storage;

public interface ConfigStorage {
    HostConfig getApiHost();

    String getAccessToken();

    void updateAccessToken(String accessToken, int expiresInSeconds);

    void updateRSAKey(String modulus, String exponent);

    String getRSAModulus();

    String getRSAExponent();

    boolean isInitRSAKey();

    boolean isAccessTokenExpired();

    void expireAccessToken();

    /**
     * Gets http proxy host.
     *
     * @return the http proxy host
     */
    String getHttpProxyHost();

    /**
     * Gets http proxy port.
     *
     * @return the http proxy port
     */
    int getHttpProxyPort();

    /**
     * Gets http proxy username.
     *
     * @return the http proxy username
     */
    String getHttpProxyUsername();

    /**
     * Gets http proxy password.
     *
     * @return the http proxy password
     */
    String getHttpProxyPassword();
}