package com.example.app.navigation;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class NavGroup extends VBox {

    private final NavigationNode node;
    private final int level;
    private final HBox header;
    private final VBox content;
    private final Label expandIcon;
    private final ImageView groupIcon;
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
        header.getStyleClass().add("level-" + level);

        expandIcon = new Label("▶");
        expandIcon.getStyleClass().add("nav-expand-icon");

        groupIcon = new ImageView();
        groupIcon.getStyleClass().add("nav-icon");
        groupIcon.setFitWidth(16);
        groupIcon.setFitHeight(16);
        groupIcon.setPreserveRatio(true);
        if (node.getIcon() != null) {
            try {
                groupIcon.setImage(new javafx.scene.image.Image(
                        getClass().getResourceAsStream("/images/" + node.getIcon() + ".png")));
            } catch (Exception e) {
                groupIcon.setVisible(false);
            }
        } else {
            groupIcon.setVisible(false);
        }

        groupLabel = new Label(node.getLabel());
        groupLabel.getStyleClass().add("nav-group-label");
        HBox.setHgrow(groupLabel, javafx.scene.layout.Priority.ALWAYS);

        header.getChildren().addAll(expandIcon, groupIcon, groupLabel);

        content = new VBox();
        content.getStyleClass().add("nav-group-content");

        getChildren().addAll(header, content);

        header.setOnMouseClicked(e -> toggle());

        expanded.addListener((obs, old, val) -> {
            if (val) {
                expandIcon.setText("▼");
            } else {
                expandIcon.setText("▶");
            }
        });

        buildChildren();
    }

    private void buildChildren() {
        for (NavigationNode child : node.getChildren()) {
            if (child.hasPath() || child.hasTabId()) {
                if (child.hasTabId()) {
                    continue;
                }
                NavItem item = new NavItem(child);
                items.add(item);
                content.getChildren().add(item);

                List<NavigationNode> subTabs = child.getChildren().stream()
                        .filter(NavigationNode::hasTabId)
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
}