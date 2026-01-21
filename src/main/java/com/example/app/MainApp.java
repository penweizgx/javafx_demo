package com.example.app;

import com.example.app.api.ApiService;
import com.example.app.api.okhttp.OkHttpApiServiceImpl;
import com.example.app.controller.LoginController;
import com.example.app.controller.ShellController;
import com.example.app.service.*;
import com.example.app.service.impl.AuthServiceImpl;
import com.example.app.service.impl.UserServiceImpl;
import com.example.app.viewmodel.LoginViewModel;
import com.example.app.viewmodel.ShellViewModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * 主应用入口
 * 职责：初始化服务层、创建ViewModel、管理界面切换
 */
public class MainApp extends Application {

    private Stage primaryStage;

    // Service层
    private ApiService apiService;
    private AuthService authService;
    private UserService userService;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;

        // 初始化AppContext和StageManager
        AppContext.init();
        StageManager.setPrimaryStage(primaryStage);

        // 初始化Service层
        initializeServices();

        // 从登录界面开始
        showLogin();
    }

    /**
     * 初始化所有服务
     */
    private void initializeServices() {
        // API服务
        apiService = new OkHttpApiServiceImpl();
        apiService.initHttp();
        AppContext.get().register(ApiService.class, apiService);

        // 业务服务
        authService = new AuthServiceImpl(apiService);
        userService = new UserServiceImpl(apiService);

        AppContext.get().register(AuthService.class, authService);
        AppContext.get().register(UserService.class, userService);
    }

    /**
     * 显示登录界面
     */
    private void showLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();
            LoginController controller = loader.getController();

            // 创建LoginViewModel并注入
            LoginViewModel loginViewModel = new LoginViewModel(authService);
            loginViewModel.setOnLoginSuccess(this::showMainShell);
            controller.setViewModel(loginViewModel);

            Scene scene = new Scene(root, 400, 500);
            scene.getStylesheets().add(getClass().getResource("/css/base.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("/css/light.css").toExternalForm());

            primaryStage.setScene(scene);
            primaryStage.setTitle("登录");
            primaryStage.centerOnScreen();
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示主界面
     */
    private void showMainShell() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/shell.fxml"));
            Parent root = loader.load();
            StackPane stackRoot = (StackPane) root;

            // 创建UI服务
            DialogService dialogService = new DialogService(stackRoot);
            LoadingService loadingService = new LoadingService(stackRoot);
            ToastService toastService = new ToastService(stackRoot);

            AppContext.get().register(DialogService.class, dialogService);
            AppContext.get().register(LoadingService.class, loadingService);
            AppContext.get().register(ToastService.class, toastService);

            // 获取Controller并注入服务和ViewModel
            ShellController shell = loader.getController();
            shell.initServices(dialogService, loadingService, toastService);

            // 创建ShellViewModel并注入
            ShellViewModel shellViewModel = new ShellViewModel(userService);
            shell.setViewModel(shellViewModel);

            // 设置场景
            Scene scene = new Scene(root, 1100, 720);
            scene.getStylesheets().add(getClass().getResource("/css/base.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("/css/light.css").toExternalForm());

            primaryStage.setScene(scene);
            primaryStage.setTitle("JavaFX MVVM Demo");
            primaryStage.centerOnScreen();
            primaryStage.show();

            // 初始化ViewManager
            ViewManager.init(stackRoot);

            // 显示首页Tab
            shell.showInitialTab("home");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
