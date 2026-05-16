package com.example.app.navigation;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class NavigationConfig {

    private List<NavigationNode> roots = new ArrayList<>();
    private QuickAccessConfig quickAccess;

    private java.util.Map<String, NavigationNode> nodeById = new java.util.HashMap<>();
    private java.util.Map<String, NavigationNode> nodeByPath = new java.util.HashMap<>();

    public void indexNodes() {
        nodeById.clear();
        nodeByPath.clear();
        for (NavigationNode root : roots) {
            indexNode(root);
        }
    }

    private void indexNode(NavigationNode node) {
        if (node.getId() != null) {
            nodeById.put(node.getId(), node);
        }
        if (node.hasPath()) {
            nodeByPath.put(node.getPath(), node);
        }
        for (NavigationNode child : node.getChildren()) {
            indexNode(child);
        }
    }

    public NavigationNode findById(String id) {
        return nodeById.get(id);
    }

    public NavigationNode findByPath(String path) {
        if (path == null) return null;
        String basePath = path.contains("#") ? path.substring(0, path.indexOf("#")) : path;
        return nodeByPath.get(basePath);
    }

    public NavigationNode findParent(NavigationNode node) {
        return node != null ? node.getParent() : null;
    }

    public List<NavigationNode> getAncestors(NavigationNode node) {
        List<NavigationNode> ancestors = new ArrayList<>();
        NavigationNode current = node;
        while (current != null) {
            ancestors.add(0, current);
            current = current.getParent();
        }
        return ancestors;
    }

    @Data
    public static class QuickAccessConfig {
        private List<QuickAccessItem> header = new ArrayList<>();
        private List<QuickAccessItem> dashboard = new ArrayList<>();
    }

    @Data
    public static class QuickAccessItem {
        private String id;
        private String label;
        private String labelKey;
        private String icon;
        private String path;
        private RouteParams params;
        private boolean showBadge;
        private String badgeSource;
    }
}
