package com.example.app;

import com.example.app.api.ApiService;
import com.example.app.config.AppModule;
import com.example.app.controller.LoginController;
import com.example.app.controller.ShellController;
import com.example.app.exception.ExceptionHandler;
import com.example.app.i18n.I18nService;
import com.example.app.service.*;
import com.example.app.service.impl.AuthServiceImpl;
import com.example.app.service.impl.UserServiceImpl;
import com.example.app.storage.TokenStorage;
import com.example.app.viewmodel.LoginViewModel;
import com.example.app.viewmodel.ShellViewModel;
import com.google.inject.Guice;
import com.google.inject.Injector;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import atlantafx.base.theme.PrimerLight;

public class MainApp extends Application {

    private Stage primaryStage;
    private Injector injector;
    private I18nService i18n;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        injector = Guice.createInjector(new AppModule());
        AppContext.init(injector);
        StageManager.setPrimaryStage(primaryStage);

        i18n = new I18nService();

        initializeServices();

        showLogin();
    }

    private void initializeServices() {
        ApiService apiService = injector.getInstance(ApiService.class);
        apiService.initHttp();
    }

    private void showLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            loader.setControllerFactory(injector::getInstance);
            Parent root = loader.load();
            LoginController controller = loader.getController();

            AuthService authService = injector.getInstance(AuthService.class);
            LoginViewModel loginViewModel = new LoginViewModel(authService, i18n);
            loginViewModel.setOnLoginSuccess(this::showMainShell);
            controller.setViewModel(loginViewModel);

            Scene scene = new Scene(root, 400, 500);
            Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());

            primaryStage.setScene(scene);
            primaryStage.setTitle(i18n.getString("login.title"));
            primaryStage.centerOnScreen();
            primaryStage.show();

        } catch (Exception e) {
            ExceptionHandler.handle(e, "Failed to show login screen");
        }
    }

    private void showMainShell() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/shell.fxml"));
            loader.setControllerFactory(injector::getInstance);
            Parent root = loader.load();
            StackPane stackRoot = (StackPane) root;

            DialogService dialogService = new DialogService(stackRoot);
            LoadingService loadingService = new LoadingService(stackRoot);
            ToastService toastService = new ToastService(stackRoot);

            ShellController shell = loader.getController();
            shell.initServices(dialogService, loadingService, toastService);
            shell.setI18n(i18n);

            UserService userService = injector.getInstance(UserService.class);
            ShellViewModel shellViewModel = new ShellViewModel(userService);
            shell.setViewModel(shellViewModel);

            Scene scene = new Scene(root, 1100, 720);
            ThemeService themeService = new ThemeService(scene);
            shell.setThemeService(themeService);

            primaryStage.setScene(scene);
            primaryStage.setTitle(i18n.getString("app.title"));
            primaryStage.centerOnScreen();
            primaryStage.show();

            ViewManager.init(stackRoot);
            shell.showInitialTab("home");

        } catch (Exception e) {
            ExceptionHandler.handle(e, "Failed to show main shell");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}