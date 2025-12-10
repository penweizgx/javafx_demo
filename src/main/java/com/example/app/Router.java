package com.example.app;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

/**
 * Router manages the center content of shell.
 * - load pages via ViewManager (cached)
 * ddc
 * - supports goTo(pageId, params) and back()
 */
public class Router {

    private static Pane contentHolder;
    private static final Deque<String> backStack = new ArrayDeque<>();
    private static final Map<String, Object> lastParams = new HashMap<>();
    private static String current;

    public static void init(Pane holder) {
        contentHolder = holder;
    }

    public static void goTo(String pageId) {
        goTo(pageId, null);
    }

    public static void goTo(String pageId, Object params) {
        try {
            if (current != null) backStack.push(current);
            Parent p = ViewManager.load(pageId);
            // if controller implements ParamReceiver, call method
            Object controller = p.getProperties().get("fx:controller");
            // expose controller from FXMLLoader: but easier get via lookup - FXML loader sets controller automatically
            // we will attempt to get controller by reflection using FXMLLoader if needed. Simpler: controllers set themselves in properties on initialize
            if (params != null) p.getProperties().put("router.params", params);
            contentHolder.getChildren().clear();
            contentHolder.getChildren().add(p);
            current = pageId;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void back() {
        if (backStack.isEmpty()) return;
        String prev = backStack.pop();
        goTo(prev, null);
    }

    public static String current() { return current; }
}
