package com.example.app.navigation;

import java.util.function.Consumer;

public class EventBus {

    private static EventBus instance;
    private final java.util.Map<Class<?>, java.util.List<Consumer<?>>> handlers = new java.util.concurrent.ConcurrentHashMap<>();

    private EventBus() {}

    public static synchronized EventBus getInstance() {
        if (instance == null) {
            instance = new EventBus();
        }
        return instance;
    }

    public <T> void subscribe(Class<T> eventType, Consumer<T> handler) {
        handlers.computeIfAbsent(eventType, k -> new java.util.concurrent.CopyOnWriteArrayList<>())
                .add(handler);
    }

    public <T> void unsubscribe(Class<T> eventType, Consumer<T> handler) {
        java.util.List<Consumer<?>> list = handlers.get(eventType);
        if (list != null) {
            list.remove(handler);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> void publish(T event) {
        java.util.List<Consumer<?>> list = handlers.get(event.getClass());
        if (list != null) {
            for (Consumer<?> h : list) {
                try {
                    ((Consumer<T>) h).accept(event);
                } catch (Exception e) {
                    Thread.currentThread().getUncaughtExceptionHandler()
                            .uncaughtException(Thread.currentThread(), e);
                }
            }
        }
    }

    public void clear() {
        handlers.clear();
    }
}
