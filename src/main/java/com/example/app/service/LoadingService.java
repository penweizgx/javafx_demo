package com.example.app.service;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

import java.util.concurrent.atomic.AtomicInteger;

public class LoadingService {
    private final StackPane root;
    private final StackPane overlay;
    private final AtomicInteger counter = new AtomicInteger(0);

    public LoadingService(StackPane root) {
        this.root = root;
        this.overlay = create();
    }

    private StackPane create() {
        StackPane sp = new StackPane();
        sp.getStyleClass().add("loading-overlay");
        Rectangle mask = new Rectangle();
        mask.widthProperty().bind(root.widthProperty());
        mask.heightProperty().bind(root.heightProperty());
        mask.getStyleClass().add("modal-mask");
        ProgressIndicator pi = new ProgressIndicator();
        sp.getChildren().addAll(mask, pi);
        StackPane.setAlignment(pi, Pos.CENTER);
        return sp;
    }

    public void show() {
        if (counter.getAndIncrement() == 0) {
            Platform.runLater(() -> { if (!root.getChildren().contains(overlay)) root.getChildren().add(overlay); });
        }
    }

    public void hide() {
        if (counter.decrementAndGet() <= 0) {
            counter.set(0);
            Platform.runLater(() -> root.getChildren().remove(overlay));
        }
    }
}
