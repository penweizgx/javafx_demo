package com.example.app;

import javafx.scene.Scene;

public class ThemeService {
    private final Scene scene;

    public ThemeService(Scene s) { this.scene = s; }

    public void setLight() {
        scene.getStylesheets().removeIf(u -> u.endsWith("dark.css"));
        if (scene.getStylesheets().stream().noneMatch(u -> u.endsWith("light.css")))
            scene.getStylesheets().add(getClass().getResource("/css/light.css").toExternalForm());
    }

    public void setDark() {
        scene.getStylesheets().removeIf(u -> u.endsWith("light.css"));
        if (scene.getStylesheets().stream().noneMatch(u -> u.endsWith("dark.css")))
            scene.getStylesheets().add(getClass().getResource("/css/dark.css").toExternalForm());
    }
}
