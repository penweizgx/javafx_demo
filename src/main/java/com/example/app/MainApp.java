package com.example.app;

import com.example.app.controller.ShellController;
import com.example.app.service.DialogService;
import com.example.app.service.LoadingService;
import com.example.app.service.ToastService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // load shell (root is StackPane)
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/shell.fxml"));
        Parent root = loader.load();
        StackPane stackRoot = (StackPane) root;

        // initialize StageManager and DI container
        StageManager.setPrimaryStage(primaryStage);
        AppContext.init(); // register services

        // create services bound to root
        DialogService dialogService = new DialogService(stackRoot);
        LoadingService loadingService = new LoadingService(stackRoot);
        ToastService toastService = new ToastService(stackRoot);

        // register services into AppContext for injection
        AppContext.get().register(DialogService.class, dialogService);
        AppContext.get().register(LoadingService.class, loadingService);
        AppContext.get().register(ToastService.class, toastService);

        // inject Shell controller services
        ShellController shell = loader.getController();
        shell.initServices(dialogService, loadingService, toastService);

        // theme: load base + default theme
        Scene scene = new Scene(root, 1100, 720);
        scene.getStylesheets().add(getClass().getResource("/css/base.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/css/light.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.setTitle("JavaFX Advanced Sample");
        primaryStage.show();

        // init ViewManager
        ViewManager.init(stackRoot);
        
        // 初始化时打开首页Tab
        shell.showInitialTab("home");
    }

    public static void main(String[] args) { launch(args); }
}
