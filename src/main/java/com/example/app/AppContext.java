package com.example.app;

import com.google.inject.Injector;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AppContext {
    private static AppContext instance;
    private final Injector injector;
    private final Map<Class<?>, Object> manualServices = new ConcurrentHashMap<>();

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

    @SuppressWarnings("unchecked")
    public <T> T getService(Class<T> clazz) {
        Object manual = manualServices.get(clazz);
        if (manual != null) return (T) manual;
        return injector.getInstance(clazz);
    }

    public <T> void registerService(Class<T> clazz, T service) {
        manualServices.put(clazz, service);
    }

    public Injector getInjector() {
        return injector;
    }
}
