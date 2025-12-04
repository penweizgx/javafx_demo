package com.example.app;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Loads FXML pages and caches Parent + Controller.
 * Uses FXMLLoader.setControllerFactory to allow constructor injection from AppContext.
 */
public class ViewManager {
    private static StackPane root;
    private static final Map<String, Parent> cache = new HashMap<>();

    public static void init(StackPane appRoot) {
        root = appRoot;
    }

    public static Parent load(String pageId) throws IOException {
        if (cache.containsKey(pageId)) return cache.get(pageId);

        String path = "/fxml/" + pageId + ".fxml";
        FXMLLoader loader = new FXMLLoader(ViewManager.class.getResource(path));

        // controller factory: try to find constructor that can accept services from AppContext
        loader.setControllerFactory(cls -> {
            // if AppContext contains bean of this type, use it; else try no-arg constructor
            Object bean = AppContext.get().getService(cls);
            if (bean != null) return bean;
            try {
                return cls.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        Parent p = loader.load();
        cache.put(pageId, p);
        return p;
    }

    public static void showAsPopup(Parent node) {
        root.getChildren().add(node);
    }

    public static void removePopup(Parent node) {
        root.getChildren().remove(node);
    }
}
