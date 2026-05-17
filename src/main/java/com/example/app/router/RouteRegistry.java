package com.example.app.router;

import java.util.ArrayList;
import java.util.List;

public class RouteRegistry {

    private final List<RouteDefinition> routes = new ArrayList<>();

    public void register(String pattern, String fxmlPath) {
        routes.add(new RouteDefinition(pattern, fxmlPath, null));
    }

    public void register(String pattern, String fxmlPath, String label) {
        routes.add(new RouteDefinition(pattern, fxmlPath, label));
    }

    public RouteMatch match(String path) {
        for (RouteDefinition def : routes) {
            RouteMatch match = def.match(path);
            if (match != null) {
                return match;
            }
        }
        return null;
    }

    public boolean exists(String path) {
        return match(path) != null;
    }

    public List<RouteDefinition> getRoutes() {
        return routes;
    }
}