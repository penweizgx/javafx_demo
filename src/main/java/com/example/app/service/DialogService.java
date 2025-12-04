package com.example.app.service;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;

import java.util.concurrent.CompletableFuture;

public class DialogService {
    private final StackPane root;

    public DialogService(StackPane root) { this.root = root; }

    public <T> CompletableFuture<T> showModal(Node content, boolean blockBackground) {
        CompletableFuture<T> future = new CompletableFuture<>();
        Platform.runLater(() -> {
            StackPane container = new StackPane();
            container.getStyleClass().add("modal-container");
            container.setPrefSize(root.getWidth(), root.getHeight());
            if (blockBackground) container.setMouseTransparent(false);

            container.getChildren().add(content);
            root.getChildren().add(container);

            // ESC to cancel
            container.addEventFilter(KeyEvent.KEY_PRESSED, ev -> {
                if (ev.getCode() == KeyCode.ESCAPE) {
                    future.complete(null);
                    root.getChildren().remove(container);
                }
            });

            // Helper API: content can set property "dialog-complete" to a Runnable to close+complete
            content.getProperties().put("dialog-complete", (Runnable) () -> {
                root.getChildren().remove(container);
            });

            // Caller or controller inside content should complete the future appropriately.
        });
        return future;
    }

    public void closeAll() {
        Platform.runLater(() -> root.getChildren().removeIf(n -> n.getStyleClass().contains("modal-container")));
    }
}
