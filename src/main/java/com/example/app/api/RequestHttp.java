package com.example.app.api;

public interface RequestHttp<H, P> {
    H getRequestHttpClient();
    /**
     * 返回httpProxy.
     *
     * @return 返回httpProxy
     */
    P getRequestHttpProxy();
}