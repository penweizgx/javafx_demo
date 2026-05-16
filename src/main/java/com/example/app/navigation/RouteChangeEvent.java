package com.example.app.navigation;

import javafx.scene.control.Tab;

public class RouteChangeEvent {

    public enum Type {
        NAVIGATE, BACK, FORWARD, TAB_SWITCH, TAB_CLOSE
    }

    private final String path;
    private final RouteParams params;
    private final Tab tab;
    private final Type type;

    public RouteChangeEvent(String path, RouteParams params, Tab tab, Type type) {
        this.path = path;
        this.params = params;
        this.tab = tab;
        this.type = type;
    }

    public String getPath() { return path; }
    public RouteParams getParams() { return params; }
    public Tab getTab() { return tab; }
    public Type getType() { return type; }

    public static RouteChangeEvent navigate(String path, RouteParams params, Tab tab) {
        return new RouteChangeEvent(path, params, tab, Type.NAVIGATE);
    }

    public static RouteChangeEvent back(String path, Tab tab) {
        return new RouteChangeEvent(path, null, tab, Type.BACK);
    }

    public static RouteChangeEvent forward(String path, Tab tab) {
        return new RouteChangeEvent(path, null, tab, Type.FORWARD);
    }

    public static RouteChangeEvent tabSwitch(String path, Tab tab) {
        return new RouteChangeEvent(path, null, tab, Type.TAB_SWITCH);
    }

    public static RouteChangeEvent tabClose(String path, Tab tab) {
        return new RouteChangeEvent(path, null, tab, Type.TAB_CLOSE);
    }
}
