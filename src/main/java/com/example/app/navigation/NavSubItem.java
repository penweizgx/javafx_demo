package com.example.app.navigation;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class NavSubItem extends HBox {

    private final NavigationNode node;
    private final Label label;
    private boolean selected;

    public NavSubItem(NavigationNode node) {
        this.node = node;
        getStyleClass().add("nav-sub-item");

        label = new Label();
        label.setText(node.getLabel());
        getChildren().add(label);

        setOnMouseClicked(e -> onClick());
    }

    private void onClick() {
        EventBus.getInstance().publish(
                new NavigationClickEvent(node, node.getParent().getPath(),
                        new RouteParams().tabId(node.getTabId())));
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        if (selected) {
            getStyleClass().add("selected");
        } else {
            getStyleClass().remove("selected");
        }
    }

    public boolean isSelected() {
        return selected;
    }

    public NavigationNode getNode() {
        return node;
    }

    public String getTabId() {
        return node.getTabId();
    }
}