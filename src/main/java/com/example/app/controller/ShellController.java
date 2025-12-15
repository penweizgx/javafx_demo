package com.example.app.controller;

import com.example.app.ViewManager;
import com.example.app.service.DialogService;
import com.example.app.service.LoadingService;
import com.example.app.service.ToastService;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ShellController {

    @FXML private StackPane root;
    @FXML private TabPane tabPane;
    @FXML private Button btnHome, btnForm, btnList, btnBack, btnTheme;

    private ToastService toast;

    private boolean dark = false;
    
    // 存储页面ID到Tab的映射
    private final Map<String, Tab> tabMap = new HashMap<>();
    // 存储页面ID到页面名称的映射
    private final Map<String, String> pageNames = new HashMap<>();
    // 存储页面ID到菜单按钮的映射
    private final Map<String, Button> menuButtonMap = new HashMap<>();
    // 当前选中的页面ID
    private String currentSelectedPageId = null;

    public void initServices(DialogService d, LoadingService l, ToastService t) {
        this.toast = t;
    }

    @FXML
    public void initialize() {
        // 初始化页面名称映射
        pageNames.put("home", "首页");
        pageNames.put("form", "表单");
        pageNames.put("list", "列表");
        
        // 初始化菜单按钮映射
        menuButtonMap.put("home", btnHome);
        menuButtonMap.put("form", btnForm);
        menuButtonMap.put("list", btnList);
        
        // 监听TabPane的选中变化
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab != null) {
                String pageId = (String) newTab.getUserData();
                if (pageId != null) {
                    updateMenuSelection(pageId);
                }
            } else {
                // 如果没有选中的Tab，清除菜单选中状态
                clearMenuSelection();
            }
        });
        
        btnHome.setOnAction(e -> showTab("home"));
        btnForm.setOnAction(e -> showTab("form"));
        btnList.setOnAction(e -> showTab("list"));
        btnBack.setOnAction(e -> {
            // Tab模式下，返回功能可以关闭当前Tab或切换到上一个Tab
            if (tabPane.getTabs().size() > 1) {
                Tab currentTab = tabPane.getSelectionModel().getSelectedItem();
                if (currentTab != null) {
                    tabPane.getTabs().remove(currentTab);
                    String pageId = (String) currentTab.getUserData();
                    if (pageId != null) {
                        tabMap.remove(pageId);
                    }
                }
            }
        });
        btnTheme.setOnAction(e -> {
            dark = !dark;
            // toggle using direct Scene css swap for demo
            if (dark) {
                root.getScene().getStylesheets().removeIf(s -> s.endsWith("light.css"));
                root.getScene().getStylesheets().add(getClass().getResource("/css/dark.css").toExternalForm());
            } else {
                root.getScene().getStylesheets().removeIf(s -> s.endsWith("dark.css"));
                root.getScene().getStylesheets().add(getClass().getResource("/css/light.css").toExternalForm());
            }
        });
    }

    /**
     * 显示或切换到指定的Tab（公开方法供外部调用）
     */
    public void showTab(String pageId) {
        try {
            // 如果Tab已存在，直接切换到它
            if (tabMap.containsKey(pageId)) {
                Tab existingTab = tabMap.get(pageId);
                tabPane.getSelectionModel().select(existingTab);
                updateMenuSelection(pageId);
                return;
            }

            // 创建新的Tab
            Parent pageContent = ViewManager.load(pageId);
            Tab tab = new Tab();
            tab.setText(pageNames.getOrDefault(pageId, pageId));
            tab.setContent(pageContent);
            tab.setClosable(true);
            tab.setUserData(pageId); // 存储页面ID以便后续使用

            // 当Tab关闭时，从映射中移除
            tab.setOnClosed(e -> {
                tabMap.remove(pageId);
                // 如果关闭的是当前选中的Tab，清除菜单选中状态
                if (pageId.equals(currentSelectedPageId)) {
                    clearMenuSelection();
                }
            });

            // 添加到TabPane并选中
            tabMap.put(pageId, tab);
            tabPane.getTabs().add(tab);
            tabPane.getSelectionModel().select(tab);
            updateMenuSelection(pageId);
        } catch (IOException e) {
            e.printStackTrace();
            if (toast != null) {
                toast.show("加载页面失败: " + pageId);
            }
        }
    }

    /**
     * 更新菜单按钮的选中状态
     */
    private void updateMenuSelection(String pageId) {
        // 清除之前的选中状态
        clearMenuSelection();
        
        // 设置新的选中状态
        Button menuButton = menuButtonMap.get(pageId);
        if (menuButton != null) {
            menuButton.getStyleClass().add("menu-selected");
            currentSelectedPageId = pageId;
        }
    }

    /**
     * 清除所有菜单按钮的选中状态
     */
    private void clearMenuSelection() {
        for (Button btn : menuButtonMap.values()) {
            btn.getStyleClass().remove("menu-selected");
        }
        currentSelectedPageId = null;
    }

    /**
     * 初始化时显示初始Tab
     */
    public void showInitialTab(String pageId) {
        showTab(pageId);
    }
}
