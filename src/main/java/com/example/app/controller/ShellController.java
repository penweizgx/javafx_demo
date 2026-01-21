package com.example.app.controller;

import com.example.app.ViewManager;
import com.example.app.model.User;
import com.example.app.service.DialogService;
import com.example.app.service.LoadingService;
import com.example.app.service.ToastService;
import com.example.app.viewmodel.ShellViewModel;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 主界面Controller - 纯View层
 * 职责：UI初始化、数据绑定和事件转发
 */
public class ShellController {

    @FXML
    private StackPane root;
    @FXML
    private TabPane tabPane;
    @FXML
    private Button btnHome, btnForm, btnList, btnBack, btnTheme;
    @FXML
    private Label userNameLabel;

    private ShellViewModel viewModel;
    private DialogService dialog;
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

    /**
     * 设置ViewModel并建立数据绑定
     */
    public void setViewModel(ShellViewModel viewModel) {
        this.viewModel = viewModel;
        setupBindings();

        // 加载用户信息
        viewModel.loadUserProfile();
    }

    /**
     * 初始化服务
     */
    public void initServices(DialogService d, LoadingService l, ToastService t) {
        this.dialog = d;
        this.toast = t;
    }

    /**
     * 建立UI和ViewModel之间的数据绑定
     */
    private void setupBindings() {
        // 绑定用户名显示
        userNameLabel.textProperty().bind(viewModel.getUserName());
        userNameLabel.visibleProperty().bind(viewModel.getUserLoaded());

        // 点击用户名显示详情
        userNameLabel.setOnMouseClicked(e -> showUserDetails());
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
                clearMenuSelection();
            }
        });

        btnHome.setOnAction(e -> showTab("home"));
        btnForm.setOnAction(e -> showTab("form"));
        btnList.setOnAction(e -> showTab("list"));
        btnBack.setOnAction(e -> {
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
     * 显示用户详情弹窗
     */
    private void showUserDetails() {
        User currentUser = viewModel.getCurrentUserValue();
        if (currentUser == null)
            return;

        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.getStyleClass().add("popup-content");
        content.setAlignment(Pos.TOP_LEFT);
        content.setMaxSize(400, 300);
        content.setStyle(
                "-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");

        Label title = new Label("用户详情");
        title.setFont(Font.font("System", FontWeight.BOLD, 18));

        VBox infoBox = new VBox(10);
        infoBox.getChildren().addAll(
                createDetailRow("姓名:", currentUser.getName()),
                createDetailRow("机构:", currentUser.getOrgName()));

        Button closeBtn = new Button("关闭");
        closeBtn.getStyleClass().add("primary-button");
        closeBtn.setOnAction(e -> {
            Runnable closer = (Runnable) content.getProperties().get("dialog-complete");
            if (closer != null)
                closer.run();
        });

        content.getChildren().addAll(title, infoBox, closeBtn);
        dialog.showModal(content, true);
    }

    private HBox createDetailRow(String label, String value) {
        Label l = new Label(label);
        l.setPrefWidth(100);
        l.setStyle("-fx-text-fill: #666;");
        Label v = new Label(value);
        v.setStyle("-fx-font-weight: bold;");
        return new HBox(10, l, v);
    }

    /**
     * 显示或切换到指定的Tab
     */
    public void showTab(String pageId) {
        try {
            if (tabMap.containsKey(pageId)) {
                Tab existingTab = tabMap.get(pageId);
                tabPane.getSelectionModel().select(existingTab);
                updateMenuSelection(pageId);
                return;
            }

            Parent pageContent = ViewManager.load(pageId);
            Tab tab = new Tab();
            tab.setText(pageNames.getOrDefault(pageId, pageId));
            tab.setContent(pageContent);
            tab.setClosable(true);
            tab.setUserData(pageId);

            tab.setOnClosed(e -> {
                tabMap.remove(pageId);
                if (pageId.equals(currentSelectedPageId)) {
                    clearMenuSelection();
                }
            });

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

    private void updateMenuSelection(String pageId) {
        clearMenuSelection();
        Button menuButton = menuButtonMap.get(pageId);
        if (menuButton != null) {
            menuButton.getStyleClass().add("menu-selected");
            currentSelectedPageId = pageId;
        }
    }

    private void clearMenuSelection() {
        for (Button btn : menuButtonMap.values()) {
            btn.getStyleClass().remove("menu-selected");
        }
        currentSelectedPageId = null;
    }

    public void showInitialTab(String pageId) {
        showTab(pageId);
    }
}
