package com.example.app.navigation;

public class NavigationClickEvent {

    private final NavigationNode node;
    private final String path;
    private final RouteParams params;

    public NavigationClickEvent(NavigationNode node, String path, RouteParams params) {
        this.node = node;
        this.path = path;
        this.params = params;
    }

    public NavigationNode getNode() { return node; }
    public String getPath() { return path; }
    public RouteParams getParams() { return params; }
}