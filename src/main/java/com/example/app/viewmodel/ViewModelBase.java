package com.example.app.viewmodel;

import com.example.app.executor.AsyncExecutor;
import javafx.application.Platform;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class ViewModelBase {

    protected <T> void executeAsync(
            Supplier<T> task,
            Consumer<T> onSuccess,
            Consumer<Throwable> onError) {

        AsyncExecutor.supplyAsync(task)
                .thenAccept(result -> Platform.runLater(() -> onSuccess.accept(result)))
                .exceptionally(throwable -> {
                    Platform.runLater(() -> onError.accept(throwable));
                    return null;
                });
    }

    protected void executeAsync(
            Runnable task,
            Runnable onSuccess,
            Consumer<Throwable> onError) {

        AsyncExecutor.runAsync(task)
                .thenRun(() -> Platform.runLater(onSuccess))
                .exceptionally(throwable -> {
                    Platform.runLater(() -> onError.accept(throwable));
                    return null;
                });
    }
}