package com.example.app.controller;

import com.example.app.Router;
import com.example.app.service.DialogService;
import com.example.app.service.LoadingService;
import com.example.app.service.ToastService;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Button;

public class ShellController {

    @FXML private StackPane root;
    @FXML private Pane contentHolder;
    @FXML private Button btnHome, btnForm, btnList, btnBack, btnTheme;

    private DialogService dialog;
    private LoadingService loading;
    private ToastService toast;

    private boolean dark = false;

    public void initServices(DialogService d, LoadingService l, ToastService t) {
        this.dialog = d; this.loading = l; this.toast = t;
    }

    public Pane getContentHolder() { return contentHolder; }

    @FXML
    public void initialize() {
        btnHome.setOnAction(e -> Router.goTo("home"));
        btnForm.setOnAction(e -> Router.goTo("form"));
        btnList.setOnAction(e -> Router.goTo("list"));
        btnBack.setOnAction(e -> Router.back());
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
}
