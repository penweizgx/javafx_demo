package com.example.app.service;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class ToastService {
    private final Pane layer;

    public ToastService(StackPane root) {
        layer = new Pane();
        layer.setPickOnBounds(false);
        layer.getStyleClass().add("toast-layer");
        Platform.runLater(() -> root.getChildren().add(layer));
    }

    public void show(String text) {
        Platform.runLater(() -> {
            Label lbl = new Label(text);
            lbl.getStyleClass().addAll("toast");
            lbl.setLayoutX(20);
            lbl.setLayoutY(20 + layer.getChildren().size() * 54);
            layer.getChildren().add(lbl);

            FadeTransition in = new FadeTransition(Duration.millis(200), lbl);
            in.setFromValue(0); in.setToValue(1);
            PauseTransition wait = new PauseTransition(Duration.seconds(2));
            FadeTransition out = new FadeTransition(Duration.millis(200), lbl);
            out.setFromValue(1); out.setToValue(0);
            SequentialTransition seq = new SequentialTransition(in, wait, out);
            seq.setOnFinished(e -> layer.getChildren().remove(lbl));
            seq.play();
        });
    }
}
