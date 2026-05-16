package com.example.app.router;

import lombok.Data;

import javafx.scene.control.Tab;

@Data
public class RouteHistory {

    private final String path;
    private final com.example.app.navigation.RouteParams params;
    private final Tab tab;
    private final long timestamp;
    private int tabIndex;

    public RouteHistory(String path, com.example.app.navigation.RouteParams params, Tab tab) {
        this.path = path;
        this.params = params;
        this.tab = tab;
        this.timestamp = System.currentTimeMillis();
    }
}
