package com.example.app.controller;

import atlantafx.base.controls.Popover;
import atlantafx.base.theme.Styles;
import com.example.app.AppContext;
import com.example.app.MainApp;
import com.example.app.ThemeChangeEvent;
import com.example.app.ThemeService;
import com.example.app.ViewManager;
import com.example.app.executor.AsyncExecutor;
import com.example.app.exception.ExceptionHandler;
import com.example.app.i18n.I18nService;
import com.example.app.i18n.LocaleChangeEvent;
import com.example.app.model.User;
import com.example.app.navigation.*;
import com.example.app.router.Router;
import com.example.app.router.RouteRegistry;
import com.example.app.service.DialogService;
import com.example.app.service.LoadingService;
import com.example.app.service.ToastService;
import com.example.app.storage.TokenStorage;
import com.example.app.viewmodel.ShellViewModel;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import javafx.application.Platform;

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
    private VBox navContainer;
    @FXML
    private Button btnSystem;
    @FXML
    private MenuButton themeMenuButton;
    @FXML
    private RadioMenuItem lightThemeItem;
    @FXML
    private RadioMenuItem darkThemeItem;
    @FXML
    private Label userNameLabel;
    @FXML
    private MenuButton userMenuButton;
    @FXML
    private MenuItem viewInfoItem;
    @FXML
    private MenuItem logoutItem;
    @FXML
    private Label copyrightLabel;

    private ShellViewModel viewModel;
    private DialogService dialog;
    private ToastService toast;
    private ThemeService themeService;
    private I18nService i18n;

    private NavigationPane navigationPane;
    private Router router;
    private NavigationConfig navConfig;

    private boolean dark = false;

    private final Consumer<LocaleChangeEvent> localeHandler = e -> refreshI18n();
    private final Consumer<ThemeChangeEvent> themeHandler = e -> syncThemeState(e.isDark());

    private final Map<String, Tab> tabMap = new HashMap<>();

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

    public void setupCloseHandler(Stage stage) {
        stage.setOnCloseRequest(event -> {
            event.consume();
            showExitConfirm(stage);
        });
    }

    private void showExitConfirm(Stage stage) {
        var alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(i18n != null ? i18n.getString("shell.exit.title") : "退出确认");
        alert.setHeaderText(null);
        alert.setContentText(i18n != null ? i18n.getString("shell.exit.confirm") : "确定要退出程序吗？");

        ButtonType exitBtn = new ButtonType(
                i18n != null ? i18n.getString("shell.exit.ok") : "退出",
                ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelBtn = new ButtonType(
                i18n != null ? i18n.getString("shell.exit.cancel") : "取消",
                ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(exitBtn, cancelBtn);
        alert.initOwner(stage);

        // copy stylesheets from the scene
        alert.getDialogPane().getStylesheets().addAll(stage.getScene().getStylesheets());

        var result = alert.showAndWait();
        if (result.isPresent() && result.get() == exitBtn) {
            cleanup();
            AsyncExecutor.shutdown();
            Platform.exit();
            System.exit(0);
        }
    }

    public void setThemeService(ThemeService themeService) {
        this.themeService = themeService;
    }

    public void setI18n(I18nService i18n) {
        this.i18n = i18n;
        refreshMenuI18n();
    }

    public void setNavigationConfig(NavigationConfig config) {
        this.navConfig = config;
    }

    private void setupBindings() {
        userNameLabel.textProperty().bind(viewModel.getUserName());
    }

    @FXML
    public void initialize() {
        initializeBrand();
        initializeButtons();
        initializeUserMenu();
        if (viewModel != null) {
            userMenuButton.visibleProperty().bind(viewModel.getUserLoaded());
        }
    }

    private void initializeUserMenu() {
        refreshMenuI18n();
    }

    public void initNavigation() {
        if (navConfig != null && navContainer != null) {
            navigationPane = new NavigationPane(navConfig);
            navContainer.getChildren().clear();
            navContainer.getChildren().add(navigationPane);

            RouteRegistry registry = new RouteRegistry();
            registerRoutes(registry, navConfig);
            router = new Router(registry);
            router.setTabPane(tabPane);

            subscribeEvents();
        }
    }

    private void registerRoutes(RouteRegistry registry, NavigationConfig config) {
        for (NavigationNode root : config.getRoots()) {
            registerNodeRoutes(registry, root);
        }
    }

    private void registerNodeRoutes(RouteRegistry registry, NavigationNode node) {
        if (node.hasPath() && node.getFxml() != null) {
            registry.register(node.getPath(), node.getFxml(), node.getLabel(), node.getIcon());
        }
        for (NavigationNode child : node.getChildren()) {
            registerNodeRoutes(registry, child);
        }
    }

    private void subscribeEvents() {
        EventBus.getInstance().subscribe(NavigationClickEvent.class, event -> {
            String path = event.getPath();
            if (path != null && router != null) {
                router.navigate(path, event.getParams());
            }
        });
        EventBus.getInstance().subscribe(RouteChangeEvent.class, this::onRouteChange);
        EventBus.getInstance().subscribe(LocaleChangeEvent.class, localeHandler);
        EventBus.getInstance().subscribe(ThemeChangeEvent.class, themeHandler);
    }

    private void onRouteChange(RouteChangeEvent event) {
        if (event.getType() == RouteChangeEvent.Type.TAB_CLOSE) return;
        if (navigationPane != null && event.getPath() != null) {
            navigationPane.selectByPath(event.getPath());
            navigationPane.expandToPath(event.getPath());
        }
    }

    private void refreshI18n() {
        if (i18n == null) return;
        brandLabel.setText(i18n.getString("shell.header.brand"));
        copyrightLabel.setText(i18n.getString("shell.footer.copyright"));
        btnSystem.setText(i18n.getString("shell.header.system"));
        themeMenuButton.setText(i18n.getString("shell.header.theme"));
        lightThemeItem.setText(i18n.getString("shell.theme.light"));
        darkThemeItem.setText(i18n.getString("shell.theme.dark"));
        refreshMenuI18n();
    }

    private void refreshMenuI18n() {
        if (i18n == null) return;
        if (viewInfoItem != null) viewInfoItem.setText(i18n.getString("shell.user.viewInfo"));
        if (logoutItem != null) logoutItem.setText(i18n.getString("shell.user.logout"));
    }

    private void syncThemeState(boolean isDark) {
        dark = isDark;
        if (isDark) {
            darkThemeItem.setSelected(true);
        } else {
            lightThemeItem.setSelected(true);
        }
    }

    private void initializeBrand() {
        if (i18n != null) {
            brandLabel.setText(i18n.getString("shell.header.brand"));
            copyrightLabel.setText(i18n.getString("shell.footer.copyright"));
            btnSystem.setText(i18n.getString("shell.header.system"));
            lightThemeItem.setText(i18n.getString("shell.theme.light"));
            darkThemeItem.setText(i18n.getString("shell.theme.dark"));
        } else {
            brandLabel.setText("JavaFX MVVM Demo");
            copyrightLabel.setText("© 2024 JavaFX MVVM Demo");
            btnSystem.setText("系统");
            lightThemeItem.setText("浅色");
            darkThemeItem.setText("深色");
        }

        try {
            Image logo = new Image(getClass().getResourceAsStream("/images/logo.png"));
            brandIcon.setImage(logo);
        } catch (Exception e) {
            brandIcon.setVisible(false);
        }
    }

    private void initializeButtons() {
        btnSystem.setOnAction(e -> {
            if (router != null) {
                router.navigate("/system/config");
            }
        });

        lightThemeItem.setSelected(true);

        if (i18n != null) {
            themeMenuButton.setText(i18n.getString("shell.header.theme"));
        } else {
            themeMenuButton.setText("主题");
        }
    }

    @FXML
    public void setLightTheme() {
        if (themeService != null) {
            dark = false;
            themeService.setLight();
        }
    }

    @FXML
    public void setDarkTheme() {
        if (themeService != null) {
            dark = true;
            themeService.setDark();
        }
    }

    private Popover userPopover;

    @FXML
    public void showUserDetails() {
        User currentUser = viewModel.getCurrentUserValue();
        if (currentUser == null)
            return;

        VBox content = new VBox(10);
        content.setPadding(new Insets(12));

        Label title = new Label(i18n != null ? i18n.getString("user.details") : "用户详情");
        title.getStyleClass().add(Styles.TITLE_4);

        VBox infoBox = new VBox(6);
        String nameLabel = i18n != null ? i18n.getString("user.name") : "姓名:";
        String orgLabel = i18n != null ? i18n.getString("user.org") : "机构:";
        infoBox.getChildren().addAll(
                createDetailRow(nameLabel, currentUser.getName()),
                createDetailRow(orgLabel, currentUser.getOrgName()));

        Button closeBtn = new Button(i18n != null ? i18n.getString("shell.user.close") : "关闭");
        closeBtn.getStyleClass().addAll(Styles.BUTTON_OUTLINED);
        closeBtn.setOnAction(e -> userPopover.hide());

        content.getChildren().addAll(title, infoBox, closeBtn);

        userPopover = new Popover(content);
        userPopover.setHeaderAlwaysVisible(false);
        userPopover.show(userNameLabel);
    }

    private HBox createDetailRow(String label, String value) {
        Label l = new Label(label);
        l.setPrefWidth(100);
        l.getStyleClass().add(Styles.TEXT_MUTED);
        Label v = new Label(value);
        v.getStyleClass().add(Styles.TEXT_BOLD);
        return new HBox(10, l, v);
    }

    public void showTab(String pageId) {
        if (router != null) {
            router.navigate("/" + pageId);
        } else {
            legacyShowTab(pageId);
        }
    }

    private void legacyShowTab(String pageId) {
        try {
            if (tabMap.containsKey(pageId)) {
                Tab existingTab = tabMap.get(pageId);
                tabPane.getSelectionModel().select(existingTab);
                return;
            }

            Parent pageContent = ViewManager.load(pageId);
            Tab tab = new Tab();
            tab.setText(pageId);
            tab.setContent(pageContent);
            tab.setClosable(true);
            tab.setUserData(pageId);

            tab.setOnClosed(e -> tabMap.remove(pageId));

            tabMap.put(pageId, tab);
            tabPane.getTabs().add(tab);
            tabPane.getSelectionModel().select(tab);
        } catch (IOException e) {
            ExceptionHandler.handle(e, "Failed to load page: " + pageId);
            if (toast != null) {
                toast.show("加载页面失败: " + pageId);
            }
        }
    }

    public void showInitialTab(String pageId) {
        initNavigation();
        if (router != null) {
            router.navigate("/home");
        } else {
            legacyShowTab(pageId);
        }
    }

    public NavigationPane getNavigationPane() {
        return navigationPane;
    }

    public Router getRouter() {
        return router;
    }

    @FXML
    public void logout() {
        try {
            TokenStorage tokenStorage = AppContext.get().getService(TokenStorage.class);
            if (tokenStorage != null) {
                tokenStorage.clearToken();
            }
        } catch (Exception ignored) {
        }

        cleanup();

        Stage stage = (Stage) userMenuButton.getScene().getWindow();
        stage.close();

        MainApp.showLoginScreen();
    }

    public void cleanup() {
        EventBus.getInstance().unsubscribe(LocaleChangeEvent.class, localeHandler);
        EventBus.getInstance().unsubscribe(ThemeChangeEvent.class, themeHandler);
        if (navigationPane != null) {
            navigationPane.cleanup();
        }
    }
}