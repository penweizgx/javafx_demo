package com.example.app.navigation;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class NavigationNode {

    private String id;
    private String path;
    private String label;
    private String labelKey;
    private String icon;
    private int level;
    private boolean expanded;
    private boolean leaf;
    private String tabId;
    private String fxml;

    private List<NavigationNode> children = new ArrayList<>();
    private NavigationNode parent;

    private PageOptions options;

    public void addChild(NavigationNode child) {
        children.add(child);
        child.setParent(this);
        child.setLevel(this.level + 1);
    }

    public boolean hasChildren() {
        return children != null && !children.isEmpty();
    }

    public boolean hasPath() {
        return path != null && !path.isEmpty();
    }

    public boolean hasTabId() {
        return tabId != null && !tabId.isEmpty();
    }

    public String getFullPath() {
        if (path == null) return null;
        StringBuilder sb = new StringBuilder(path);
        if (tabId != null) {
            sb.append("#").append(tabId);
        }
        return sb.toString();
    }
}
