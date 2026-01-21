package com.example.app.viewmodel;

import javafx.application.Platform;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * ViewModel基类，提供通用功能
 */
public abstract class ViewModelBase {

    /**
     * 在后台线程执行任务，结果在JavaFX线程中处理
     * 
     * @param task      后台任务
     * @param onSuccess 成功回调（在JavaFX线程）
     * @param onError   错误回调（在JavaFX线程）
     */
    protected <T> void executeAsync(
            Supplier<T> task,
            Consumer<T> onSuccess,
            Consumer<Throwable> onError) {

        CompletableFuture.supplyAsync(task)
                .thenAccept(result -> Platform.runLater(() -> onSuccess.accept(result)))
                .exceptionally(throwable -> {
                    Platform.runLater(() -> onError.accept(throwable));
                    return null;
                });
    }

    /**
     * 在后台线程执行无返回值任务
     * 
     * @param task      后台任务
     * @param onSuccess 成功回调（在JavaFX线程）
     * @param onError   错误回调（在JavaFX线程）
     */
    protected void executeAsync(
            Runnable task,
            Runnable onSuccess,
            Consumer<Throwable> onError) {

        CompletableFuture.runAsync(task)
                .thenRun(() -> Platform.runLater(onSuccess))
                .exceptionally(throwable -> {
                    Platform.runLater(() -> onError.accept(throwable));
                    return null;
                });
    }
}
