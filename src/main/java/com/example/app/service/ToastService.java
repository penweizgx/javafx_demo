package com.example.app.service;

import atlantafx.base.controls.Notification;
import atlantafx.base.theme.Styles;
import atlantafx.base.util.Animations;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class ToastService {
    private final StackPane layer;

    public ToastService(StackPane root) {
        layer = new StackPane();
        layer.setPickOnBounds(false);
        Platform.runLater(() -> root.getChildren().add(layer));
    }

    public void show(String text) {
        show(text, null);
    }

    public void show(String text, String styleClass) {
        Platform.runLater(() -> {
            Notification ntf = new Notification(text);
            ntf.getStyleClass().add(Styles.ELEVATED_1);
            if (styleClass != null) {
                ntf.getStyleClass().add(styleClass);
            }
            ntf.setPrefHeight(Region.USE_PREF_SIZE);
            ntf.setMaxHeight(Region.USE_PREF_SIZE);
            StackPane.setAlignment(ntf, Pos.TOP_RIGHT);
            StackPane.setMargin(ntf, new Insets(10, 10, 0, 0));
            ntf.setOnClose(e -> removeNotification(ntf));

            layer.getChildren().add(ntf);
            Animations.slideInDown(ntf, Duration.millis(250)).playFromStart();

            PauseTransition autoClose = new PauseTransition(Duration.seconds(3));
            autoClose.setOnFinished(e -> removeNotification(ntf));
            autoClose.play();
        });
    }

    private void removeNotification(Notification ntf) {
        var out = Animations.slideOutUp(ntf, Duration.millis(250));
        out.setOnFinished(f -> layer.getChildren().remove(ntf));
        out.playFromStart();
    }

    public void showSuccess(String text) {
        show(text, Styles.SUCCESS);
    }

    public void showWarning(String text) {
        show(text, Styles.WARNING);
    }

    public void showDanger(String text) {
        show(text, Styles.DANGER);
    }
}