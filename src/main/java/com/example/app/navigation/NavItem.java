package com.example.app.navigation;

import javafx.animation.RotateTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

import java.util.List;

public class NavItem extends HBox {

    private final NavigationNode node;
    private final ImageView icon;
    private final Label label;
    private final Label expandIcon;

    private final BooleanProperty selected = new SimpleBooleanProperty(false);
    private NavSubItems subItems;
    private boolean subItemsExpanded = false;

    public NavItem(NavigationNode node) {
        this.node = node;
        getStyleClass().add("nav-item");

        expandIcon = new Label("▶");
        expandIcon.getStyleClass().add("nav-expand-icon");
        expandIcon.setVisible(node.hasChildren() && node.getChildren().stream().anyMatch(NavigationNode::hasTabId));

        icon = new ImageView();
        icon.getStyleClass().add("nav-icon");
        icon.setFitWidth(16);
        icon.setFitHeight(16);
        icon.setPreserveRatio(true);
        if (node.getIcon() != null) {
            try {
                icon.setImage(new javafx.scene.image.Image(
                        getClass().getResourceAsStream("/images/" + node.getIcon() + ".png")));
            } catch (Exception e) {
                icon.setVisible(false);
            }
        } else {
            icon.setVisible(false);
        }

        label = new Label(node.getLabel());
        label.getStyleClass().add("nav-item-label");
        HBox.setHgrow(label, javafx.scene.layout.Priority.ALWAYS);

        getChildren().addAll(expandIcon, icon, label);

        setOnMouseClicked(e -> onClick());

        selected.addListener((obs, old, val) -> {
            if (val) {
                getStyleClass().add("selected");
            } else {
                getStyleClass().remove("selected");
            }
        });
    }

    private void onClick() {
        if (node.hasPath()) {
            RouteParams params = new RouteParams();
            if (subItemsExpanded && subItems != null && subItems.getSelected() != null) {
                params.tabId(subItems.getSelected().getTabId());
            }
            EventBus.getInstance().publish(
                    new NavigationClickEvent(node, node.getPath(), params));
        }

        if (expandIcon.isVisible()) {
            toggleSubItems();
        }
    }

    private void toggleSubItems() {
        subItemsExpanded = !subItemsExpanded;
        if (subItemsExpanded) {
            expandIcon.setText("▼");
        } else {
            expandIcon.setText("▶");
        }
        EventBus.getInstance().publish(new SubItemsToggleEvent(this, subItemsExpanded));
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }

    public boolean isSelected() {
        return selected.get();
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }

    public NavigationNode getNode() {
        return node;
    }

    public void setSubItems(NavSubItems subItems) {
        this.subItems = subItems;
    }

    public NavSubItems getSubItems() {
        return subItems;
    }

    public boolean hasSubItems() {
        return subItems != null;
    }

    public boolean isSubItemsExpanded() {
        return subItemsExpanded;
    }

    public void setSubItemsExpanded(boolean expanded) {
        this.subItemsExpanded = expanded;
        expandIcon.setText(expanded ? "▼" : "▶");
    }

    public static class SubItemsToggleEvent {
        private final NavItem item;
        private final boolean expanded;

        public SubItemsToggleEvent(NavItem item, boolean expanded) {
            this.item = item;
            this.expanded = expanded;
        }

        public NavItem getItem() { return item; }
        public boolean isExpanded() { return expanded; }
    }
}