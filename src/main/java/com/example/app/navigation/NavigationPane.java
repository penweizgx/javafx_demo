package com.example.app.navigation;

import atlantafx.base.theme.Styles;
import atlantafx.base.theme.Tweaks;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.HashMap;
import java.util.Map;

public class NavigationPane extends VBox {

    private final NavigationConfig config;
    private final TreeView<NavigationNode> treeView;
    private final StringProperty selectedPath = new SimpleStringProperty();
    private final Map<String, TreeItem<NavigationNode>> itemByPath = new HashMap<>();

    public NavigationPane(NavigationConfig config) {
        this.config = config;
        getStyleClass().add("navigation-pane");

        treeView = new TreeView<>();
        treeView.getStyleClass().add(Styles.DENSE);
        treeView.getStyleClass().add(Tweaks.ALT_ICON);
        treeView.getStyleClass().add(Tweaks.EDGE_TO_EDGE);
        treeView.setShowRoot(false);
        treeView.setCellFactory(param -> new NavTreeCell());

        buildNavigation();
        subscribeEvents();

        VBox.setVgrow(treeView, javafx.scene.layout.Priority.ALWAYS);
        getChildren().add(treeView);
    }

    private void buildNavigation() {
        TreeItem<NavigationNode> rootItem = new TreeItem<>();
        rootItem.setExpanded(true);

        for (NavigationNode node : config.getRoots()) {
            if (!node.isShowInNav()) {
                continue;
            }
            TreeItem<NavigationNode> item = createTreeItem(node);
            rootItem.getChildren().add(item);
        }

        treeView.setRoot(rootItem);
    }

    private TreeItem<NavigationNode> createTreeItem(NavigationNode node) {
        TreeItem<NavigationNode> item = new TreeItem<>(node);
        if (node.isExpanded()) {
            item.setExpanded(true);
        }

        if (node.hasPath()) {
            itemByPath.put(node.getPath(), item);
        }

        for (NavigationNode child : node.getChildren()) {
            if (!child.isShowInNav()) {
                continue;
            }
            TreeItem<NavigationNode> childItem = createTreeItem(child);
            item.getChildren().add(childItem);
        }

        return item;
    }

    private void subscribeEvents() {
        EventBus.getInstance().subscribe(RouteChangeEvent.class, event -> {
            String path = event.getPath();
            if (path != null) {
                selectByPath(path);
                expandToPath(path);
            }
        });

        EventBus.getInstance().subscribe(NavigationClickEvent.class, event -> {
            String path = event.getPath();
            if (path != null) {
                selectedPath.set(path);
            }
        });

        treeView.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null && selected.getValue() != null) {
                NavigationNode node = selected.getValue();
                if (node.hasPath()) {
                    RouteParams params = new RouteParams();
                    EventBus.getInstance().publish(
                            new NavigationClickEvent(node, node.getPath(), params));
                }
            }
        });
    }

    private String extractBasePath(String path) {
        return path.contains("#") ? path.substring(0, path.indexOf("#")) : path;
    }

    public void selectByPath(String path) {
        TreeItem<NavigationNode> item = itemByPath.get(extractBasePath(path));
        if (item != null) {
            treeView.getSelectionModel().select(item);
        }
    }

    public void expandToPath(String path) {
        TreeItem<NavigationNode> item = itemByPath.get(extractBasePath(path));
        if (item != null) {
            TreeItem<NavigationNode> parent = item.getParent();
            while (parent != null && parent.getValue() != null) {
                parent.setExpanded(true);
                parent = parent.getParent();
            }
        }
    }

    public StringProperty selectedPathProperty() {
        return selectedPath;
    }

    public String getSelectedPath() {
        return selectedPath.get();
    }

    public NavigationConfig getConfig() {
        return config;
    }

    private static class NavTreeCell extends TreeCell<NavigationNode> {

        @Override
        protected void updateItem(NavigationNode node, boolean empty) {
            super.updateItem(node, empty);

            if (empty || node == null) {
                setText(null);
                setGraphic(null);
            } else {
                setText(node.getLabel());
                FontIcon icon = NavigationIcons.createIcon(node.getIcon(), 16);
                setGraphic(icon);
            }
        }

        }
}