package com.example.app.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class AsyncExecutor {
    
    private static final int POOL_SIZE = Runtime.getRuntime().availableProcessors();
    private static final ExecutorService executor = Executors.newFixedThreadPool(POOL_SIZE);
    
    public static <T> CompletableFuture<T> supplyAsync(Supplier<T> task) {
        return CompletableFuture.supplyAsync(task, executor);
    }
    
    public static CompletableFuture<Void> runAsync(Runnable task) {
        return CompletableFuture.runAsync(task, executor);
    }
    
    public static void shutdown() {
        executor.shutdown();
    }
}
