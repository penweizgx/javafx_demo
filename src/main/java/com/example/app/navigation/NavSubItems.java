package com.example.app.navigation;

import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class NavSubItems extends VBox {

    private final List<NavSubItem> items = new ArrayList<>();
    private NavSubItem selected;

    public NavSubItems(List<NavigationNode> nodes) {
        getStyleClass().add("nav-sub-items");

        for (NavigationNode node : nodes) {
            if (node.hasTabId()) {
                NavSubItem item = new NavSubItem(node);
                items.add(item);
                getChildren().add(item);
            }
        }
    }

    public void select(String tabId) {
        for (NavSubItem item : items) {
            if (item.getTabId().equals(tabId)) {
                item.setSelected(true);
                selected = item;
            } else {
                item.setSelected(false);
            }
        }
    }

    public void clearSelection() {
        for (NavSubItem item : items) {
            item.setSelected(false);
        }
        selected = null;
    }

    public NavSubItem getSelected() {
        return selected;
    }

    public List<NavSubItem> getItems() {
        return items;
    }
}