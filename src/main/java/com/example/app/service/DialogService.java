package com.example.app.service;

import atlantafx.base.controls.ModalPane;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

import java.util.concurrent.CompletableFuture;

public class DialogService {
    private final ModalPane modalPane;

    public DialogService(StackPane root) {
        this.modalPane = new ModalPane();
        Platform.runLater(() -> root.getChildren().add(modalPane));
    }

    public <T> CompletableFuture<T> showModal(Node content, boolean blockBackground) {
        CompletableFuture<T> future = new CompletableFuture<>();
        Platform.runLater(() -> {
            modalPane.setPersistent(blockBackground);

            content.getProperties().put("dialog-complete", (Runnable) () -> {
                modalPane.hide(true);
                future.complete(null);
            });

            modalPane.show(content);
        });
        return future;
    }

    public void closeAll() {
        Platform.runLater(() -> modalPane.hide(true));
    }

    public ModalPane getModalPane() {
        return modalPane;
    }
}