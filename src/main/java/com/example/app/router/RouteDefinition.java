package com.example.app.router;

import lombok.Data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
public class RouteDefinition {

    private final String pattern;
    private final String fxmlPath;
    private final Pattern regexPattern;
    private final java.util.List<String> paramNames;

    public RouteDefinition(String pattern, String fxmlPath) {
        this.pattern = pattern;
        this.fxmlPath = fxmlPath;
        this.paramNames = new java.util.ArrayList<>();
        this.regexPattern = compilePattern(pattern);
    }

    private Pattern compilePattern(String pattern) {
        StringBuilder regex = new StringBuilder("^");
        String[] parts = pattern.split("/");

        for (String part : parts) {
            if (part.isEmpty()) continue;
            regex.append("/");
            if (part.startsWith(":")) {
                paramNames.add(part.substring(1));
                regex.append("([^/]+)");
            } else {
                regex.append(java.util.regex.Pattern.quote(part));
            }
        }
        regex.append("$");
        return Pattern.compile(regex.toString());
    }

    public RouteMatch match(String path) {
        Matcher m = regexPattern.matcher(path);
        if (!m.matches()) return null;

        java.util.Map<String, String> params = new java.util.HashMap<>();
        for (int i = 0; i < paramNames.size(); i++) {
            params.put(paramNames.get(i), m.group(i + 1));
        }

        return new RouteMatch(this, path, params);
    }
}
