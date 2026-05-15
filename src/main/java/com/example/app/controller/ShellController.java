package com.example.app.controller;

import com.example.app.ThemeService;
import com.example.app.ViewManager;
import com.example.app.exception.ExceptionHandler;
import com.example.app.i18n.I18nService;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ShellController {

    @FXML
    private StackPane root;
    @FXML
    private TabPane tabPane;
    @FXML
    private ImageView brandIcon;
    @FXML
    private Label brandLabel;
    @FXML
    private Button btnHome, btnForm, btnList, btnSystem, btnTheme;
    @FXML
    private Label userNameLabel;
    @FXML
    private Label copyrightLabel;

    private ShellViewModel viewModel;
    private DialogService dialog;
    private ToastService toast;
    private ThemeService themeService;
    private I18nService i18n;

    private boolean dark = false;

    private final Map<String, Tab> tabMap = new HashMap<>();
    private final Map<String, String> pageNames = new HashMap<>();
    private final Map<String, Button> menuButtonMap = new HashMap<>();
    private String currentSelectedPageId = null;

    public ShellController() {
    }

    public void setViewModel(ShellViewModel viewModel) {
        this.viewModel = viewModel;
        setupBindings();
        viewModel.loadUserProfile();
    }

    public void initServices(DialogService d, LoadingService l, ToastService t) {
        this.dialog = d;
        this.toast = t;
    }

    public void setThemeService(ThemeService themeService) {
        this.themeService = themeService;
    }

    public void setI18n(I18nService i18n) {
        this.i18n = i18n;
    }

    private void setupBindings() {
        userNameLabel.textProperty().bind(viewModel.getUserName());
        userNameLabel.visibleProperty().bind(viewModel.getUserLoaded());
        userNameLabel.setOnMouseClicked(e -> showUserDetails());
    }

    @FXML
    public void initialize() {
        initializeBrand();
        initializeNavigation();
        initializeButtons();
    }

    private void initializeBrand() {
        if (i18n != null) {
            brandLabel.setText(i18n.getString("shell.header.brand"));
            copyrightLabel.setText(i18n.getString("shell.footer.copyright"));
            btnSystem.setText(i18n.getString("shell.header.system"));
            btnTheme.setText(i18n.getString("shell.header.theme"));
        } else {
            brandLabel.setText("JavaFX MVVM Demo");
            copyrightLabel.setText("© 2024 JavaFX MVVM Demo");
            btnSystem.setText("系统");
            btnTheme.setText("主题");
        }

        try {
            Image logo = new Image(getClass().getResourceAsStream("/images/logo.png"));
            brandIcon.setImage(logo);
        } catch (Exception e) {
            brandIcon.setVisible(false);
        }
    }

    private void initializeNavigation() {
        if (i18n != null) {
            pageNames.put("home", i18n.getString("shell.menu.home"));
            pageNames.put("form", i18n.getString("shell.menu.form"));
            pageNames.put("list", i18n.getString("shell.menu.list"));
        } else {
            pageNames.put("home", "首页");
            pageNames.put("form", "表单");
            pageNames.put("list", "列表");
        }

        btnHome.setText(pageNames.get("home"));
        btnForm.setText(pageNames.get("form"));
        btnList.setText(pageNames.get("list"));

        menuButtonMap.put("home", btnHome);
        menuButtonMap.put("form", btnForm);
        menuButtonMap.put("list", btnList);

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
    }

    private void initializeButtons() {
        btnHome.setOnAction(e -> showTab("home"));
        btnForm.setOnAction(e -> showTab("form"));
        btnList.setOnAction(e -> showTab("list"));

        btnSystem.setOnAction(e -> {
            // Placeholder - no action yet
        });

        btnTheme.setOnAction(e -> {
            if (themeService != null) {
                dark = !dark;
                if (dark) {
                    themeService.setDark();
                } else {
                    themeService.setLight();
                }
            }
        });
    }

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

        String titleText = i18n != null ? i18n.getString("user.details") : "用户详情";
        Label title = new Label(titleText);
        title.setFont(Font.font("System", FontWeight.BOLD, 18));

        VBox infoBox = new VBox(10);
        String nameLabel = i18n != null ? i18n.getString("user.name") : "姓名:";
        String orgLabel = i18n != null ? i18n.getString("user.org") : "机构:";
        infoBox.getChildren().addAll(
                createDetailRow(nameLabel, currentUser.getName()),
                createDetailRow(orgLabel, currentUser.getOrgName()));

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
            ExceptionHandler.handle(e, "Failed to load page: " + pageId);
            if (toast != null) {
                String msg = i18n != null ? i18n.getString("toast.page.load.failed", pageId) : "加载页面失败: " + pageId;
                toast.show(msg);
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
