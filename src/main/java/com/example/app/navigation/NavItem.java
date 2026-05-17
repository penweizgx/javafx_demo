package com.example.app.navigation;

import javafx.animation.RotateTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2MZ;

import java.util.List;

public class NavItem extends HBox {

    private final NavigationNode node;
    private final FontIcon icon;
    private final Label label;
    private final FontIcon expandIcon;
    private final int level;

    private final BooleanProperty selected = new SimpleBooleanProperty(false);
    private NavSubItems subItems;
    private boolean subItemsExpanded = false;

    public NavItem(NavigationNode node) {
        this(node, 1);
    }

    public NavItem(NavigationNode node, int level) {
        this.node = node;
        this.level = level;
        getStyleClass().add("nav-item");
        setStyle("-fx-spacing: 8; -fx-alignment: center-left; -fx-cursor: hand;");

        expandIcon = new FontIcon(Material2MZ.PLUS);
        expandIcon.getStyleClass().add("nav-expand-icon");
        expandIcon.setIconSize(12);
        expandIcon.setVisible(node.hasChildren() && node.getChildren().stream().anyMatch(NavigationNode::hasTabId));

        icon = new FontIcon(getIcon(node.getIcon()));
        icon.getStyleClass().add("nav-icon");
        icon.setIconSize(16);
        HBox.setMargin(icon, new javafx.geometry.Insets(0, 8, 0, 0));

        label = new Label(node.getLabel());
        label.getStyleClass().add("nav-item-label");
        HBox.setHgrow(label, javafx.scene.layout.Priority.ALWAYS);

        getChildren().addAll(icon, label, expandIcon);

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
            expandIcon.setIconCode(Material2MZ.MINUS);
        } else {
            expandIcon.setIconCode(Material2MZ.PLUS);
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
        expandIcon.setIconCode(expanded ? Material2MZ.MINUS : Material2MZ.PLUS);
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

    private org.kordamp.ikonli.Ikon getIcon(String iconName) {
        if (iconName == null) return Material2MZ.SEARCH;
        return switch (iconName) {
            case "home" -> Material2MZ.SEARCH;
            case "settings" -> Material2MZ.SEARCH;
            case "business" -> Material2MZ.WORK_OUTLINE;
            case "user", "user-list" -> Material2MZ.PEOPLE_OUTLINE;
            case "user-detail" -> Material2MZ.PHOTO_CAMERA;
            case "form" -> Material2MZ.THUMB_UP;
            case "list" -> Material2MZ.VIEW_LIST;
            default -> Material2MZ.SEARCH;
        };
    }
}