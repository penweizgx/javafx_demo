package com.example.app.navigation;

import java.util.Map;

public class RouteParams {

    private final Map<String, Object> data;
    private String tabId;
    private String subPagePath;

    public RouteParams() {
        this.data = new java.util.HashMap<>();
    }

    public RouteParams(Map<String, Object> data) {
        this.data = new java.util.HashMap<>(data);
    }

    public static RouteParams empty() {
        return new RouteParams();
    }

    public static RouteParams of(String key, Object value) {
        return new RouteParams().put(key, value);
    }

    public static RouteParams of(Map<String, Object> data) {
        return new RouteParams(data);
    }

    public RouteParams put(String key, Object value) {
        data.put(key, value);
        return this;
    }

    public RouteParams tabId(String tabId) {
        this.tabId = tabId;
        return this;
    }

    public RouteParams subPage(String path) {
        this.subPagePath = path;
        return this;
    }

    public Object get(String key) {
        return data.get(key);
    }

    public String getString(String key) {
        Object v = data.get(key);
        return v != null ? v.toString() : null;
    }

    public Integer getInt(String key) {
        Object v = data.get(key);
        if (v instanceof Integer) return (Integer) v;
        if (v instanceof Number) return ((Number) v).intValue();
        if (v instanceof String) {
            try {
                return Integer.parseInt((String) v);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    public Long getLong(String key) {
        Object v = data.get(key);
        if (v instanceof Long) return (Long) v;
        if (v instanceof Number) return ((Number) v).longValue();
        if (v instanceof String) {
            try {
                return Long.parseLong((String) v);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    public String getTabId() { return tabId; }
    public String getSubPagePath() { return subPagePath; }
    public Map<String, Object> getData() { return data; }

    public void addAll(Map<String, String> params) {
        data.putAll(params);
    }

    @Override
    public String toString() {
        return "RouteParams{" +
                "data=" + data +
                ", tabId='" + tabId + '\'' +
                ", subPagePath='" + subPagePath + '\'' +
                '}';
    }
}
