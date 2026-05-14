package com.example.app;

import com.google.inject.Injector;

public class AppContext {
    private static AppContext instance;
    private final Injector injector;

    private AppContext(Injector injector) {
        this.injector = injector;
    }

    public static void init(Injector injector) {
        instance = new AppContext(injector);
    }

    public static AppContext get() {
        if (instance == null) {
            throw new IllegalStateException("AppContext not initialized");
        }
        return instance;
    }

    public <T> T getService(Class<T> clazz) {
        return injector.getInstance(clazz);
    }

    public Injector getInjector() {
        return injector;
    }
}
