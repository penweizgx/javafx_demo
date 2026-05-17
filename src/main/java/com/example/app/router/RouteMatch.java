package com.example.app.router;

import lombok.Data;

import java.util.Map;

@Data
public class RouteMatch {

    private final RouteDefinition definition;
    private final String matchedPath;
    private final Map<String, String> pathParams;

    public String getFxmlPath() {
        return definition.getFxmlPath();
    }

    public String getPattern() {
        return definition.getPattern();
    }

    public String getLabel() {
        return definition.getLabel();
    }
}
