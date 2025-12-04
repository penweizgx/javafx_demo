package com.example.app;

import java.util.HashMap;
import java.util.Map;

public class AppContext {
    private static AppContext instance;
    private final Map<Class<?>, Object> registry = new HashMap<>();

    private AppContext() {}

    public static void init() { instance = new AppContext(); }
    public static AppContext get() { return instance; }

    public <T> void register(Class<T> k, T instance) { registry.put(k, instance); }
    public <T> T getService(Class<T> k) { return k.cast(registry.get(k)); }
}
