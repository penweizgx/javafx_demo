package com.example.app.navigation;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2MZ;

import java.util.ArrayList;
import java.util.List;

public class NavGroup extends VBox {

    private final NavigationNode node;
    private final int level;
    private final HBox header;
    private final VBox content;
    private final FontIcon expandIcon;
    private final FontIcon groupIcon;
    private final Label groupLabel;

    private final BooleanProperty expanded = new SimpleBooleanProperty(false);
    private final List<NavGroup> childGroups = new ArrayList<>();
    private final List<NavItem> items = new ArrayList<>();

    private Timeline expandAnimation;
    private Timeline collapseAnimation;

    public NavGroup(NavigationNode node, int level) {
        this.node = node;
        this.level = level;
        getStyleClass().add("nav-group");

        header = new HBox();
        header.getStyleClass().add("nav-group-header");
        header.setStyle("-fx-spacing: 8; -fx-alignment: center-left; -fx-cursor: hand;");

        expandIcon = new FontIcon(Material2MZ.PLUS);
        expandIcon.getStyleClass().add("nav-expand-icon");
        expandIcon.setIconSize(14);

        groupIcon = new FontIcon(getIcon(node.getIcon()));
        groupIcon.getStyleClass().add("nav-icon");
        groupIcon.setIconSize(16);
        HBox.setMargin(groupIcon, new javafx.geometry.Insets(0, 8, 0, 0));

        groupLabel = new Label(node.getLabel());
        groupLabel.getStyleClass().add("nav-group-label");
        HBox.setHgrow(groupLabel, javafx.scene.layout.Priority.ALWAYS);

        header.getChildren().addAll(groupIcon, groupLabel, expandIcon);

        content = new VBox();
        content.getStyleClass().add("nav-group-content");
        content.setStyle("-fx-padding: 0 0 0 16px; -fx-spacing: 2;");
        content.setVisible(false);
        content.setManaged(false);

        getChildren().addAll(header, content);

        header.setOnMouseClicked(e -> toggle());

        expanded.addListener((obs, old, val) -> {
            if (val) {
                expandIcon.setIconCode(Material2MZ.MINUS);
            } else {
                expandIcon.setIconCode(Material2MZ.PLUS);
            }
        });

        buildChildren();
        
        if (content.getChildren().isEmpty()) {
            expandIcon.setVisible(false);
            expandIcon.setManaged(false);
        }
    }

    private void buildChildren() {
        for (NavigationNode child : node.getChildren()) {
            if (!child.isShowInNav()) {
                continue;
            }

            if (child.hasPath() || child.hasTabId()) {
                if (child.hasTabId()) {
                    continue;
                }
                NavItem item = new NavItem(child, level + 1);
                items.add(item);
                content.getChildren().add(item);

                List<NavigationNode> subTabs = child.getChildren().stream()
                        .filter(n -> n.isShowInNav() && n.hasTabId())
                        .collect(java.util.stream.Collectors.toList());
                if (!subTabs.isEmpty()) {
                    NavSubItems subItems = new NavSubItems(subTabs);
                    item.setSubItems(subItems);
                    content.getChildren().add(subItems);
                    subItems.setVisible(false);
                    subItems.setManaged(false);

                    EventBus.getInstance().subscribe(NavItem.SubItemsToggleEvent.class, event -> {
                        if (event.getItem() == item) {
                            subItems.setVisible(event.isExpanded());
                            subItems.setManaged(event.isExpanded());
                        }
                    });
                }
            } else if (child.hasChildren()) {
                NavGroup childGroup = new NavGroup(child, level + 1);
                childGroups.add(childGroup);
                content.getChildren().add(childGroup);
            }
        }
    }

    public void toggle() {
        setExpanded(!expanded.get());
    }

    public void setExpanded(boolean expanded) {
        this.expanded.set(expanded);
        animate(expanded);
    }

    private void animate(boolean expand) {
        if (expand) {
            if (collapseAnimation != null && collapseAnimation.getStatus() == Animation.Status.RUNNING) {
                collapseAnimation.stop();
            }
            content.setVisible(true);
            content.setManaged(true);
            content.setMaxHeight(0);

            expandAnimation = new Timeline();
            KeyValue kv = new KeyValue(content.maxHeightProperty(),
                    content.getPrefHeight() > 0 ? content.getPrefHeight() : 1000,
                    javafx.animation.Interpolator.EASE_BOTH);
            KeyFrame kf = new KeyFrame(Duration.millis(200), kv);
            expandAnimation.getKeyFrames().add(kf);
            expandAnimation.setOnFinished(e -> content.setMaxHeight(Double.MAX_VALUE));
            expandAnimation.play();
        } else {
            if (expandAnimation != null && expandAnimation.getStatus() == Animation.Status.RUNNING) {
                expandAnimation.stop();
            }

            collapseAnimation = new Timeline();
            KeyValue kv = new KeyValue(content.maxHeightProperty(), 0,
                    javafx.animation.Interpolator.EASE_BOTH);
            KeyFrame kf = new KeyFrame(Duration.millis(200), kv);
            collapseAnimation.getKeyFrames().add(kf);
            collapseAnimation.setOnFinished(e -> {
                content.setVisible(false);
                content.setManaged(false);
            });
            collapseAnimation.play();
        }
    }

    public boolean isExpanded() {
        return expanded.get();
    }

    public BooleanProperty expandedProperty() {
        return expanded;
    }

    public NavigationNode getNode() {
        return node;
    }

    public List<NavGroup> getChildGroups() {
        return childGroups;
    }

    public List<NavItem> getItems() {
        return items;
    }

    public void selectItem(String path) {
        for (NavItem item : items) {
            if (item.getNode().hasPath() && item.getNode().getPath().equals(path)) {
                item.setSelected(true);
            } else {
                item.setSelected(false);
            }
        }
        for (NavGroup child : childGroups) {
            child.selectItem(path);
        }
    }

    public void expandToPath(String path) {
        for (NavItem item : items) {
            if (item.getNode().hasPath() && item.getNode().getPath().equals(path)) {
                if (!expanded.get()) {
                    setExpanded(true);
                }
                return;
            }
        }
        for (NavGroup child : childGroups) {
            child.expandToPath(path);
            if (child.containsPath(path)) {
                if (!expanded.get()) {
                    setExpanded(true);
                }
            }
        }
    }

    public boolean containsPath(String path) {
        for (NavItem item : items) {
            if (item.getNode().hasPath() && item.getNode().getPath().equals(path)) {
                return true;
            }
        }
        for (NavGroup child : childGroups) {
            if (child.containsPath(path)) {
                return true;
            }
        }
        return false;
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