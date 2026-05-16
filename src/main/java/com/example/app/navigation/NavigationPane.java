package com.example.app.navigation;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class NavigationPane extends VBox {

    private final NavigationConfig config;
    private final List<NavGroup> groups = new ArrayList<>();
    private final StringProperty selectedPath = new SimpleStringProperty();

    public NavigationPane(NavigationConfig config) {
        this.config = config;
        getStyleClass().add("navigation-pane");

        buildNavigation();
        subscribeEvents();
    }

    private void buildNavigation() {
        for (NavigationNode root : config.getRoots()) {
            if (root.hasPath()) {
                NavItem item = new NavItem(root);
                getChildren().add(item);
            } else if (root.hasChildren()) {
                NavGroup group = new NavGroup(root, 1);
                groups.add(group);
                getChildren().add(group);
            }
        }
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
    }

    public void selectByPath(String path) {
        String basePath = path.contains("#") ? path.substring(0, path.indexOf("#")) : path;
        for (NavGroup group : groups) {
            group.selectItem(basePath);
        }
    }

    public void expandToPath(String path) {
        String basePath = path.contains("#") ? path.substring(0, path.indexOf("#")) : path;
        for (NavGroup group : groups) {
            group.expandToPath(basePath);
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
}