package com.example.app.controller;

import com.example.app.viewmodel.LoginViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * 登录界面Controller - 纯View层
 * 职责：UI初始化和数据绑定
 */
public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private Button loginButton;

    private LoginViewModel viewModel;

    /**
     * 设置ViewModel并建立数据绑定
     */
    public void setViewModel(LoginViewModel viewModel) {
        this.viewModel = viewModel;
        setupBindings();
    }

    /**
     * 建立UI和ViewModel之间的数据绑定
     */
    private void setupBindings() {
        // 双向绑定输入字段
        usernameField.textProperty().bindBidirectional(viewModel.getUsername());
        passwordField.textProperty().bindBidirectional(viewModel.getPassword());

        // 单向绑定错误信息
        errorLabel.textProperty().bind(viewModel.getErrorMessage());
        errorLabel.visibleProperty().bind(viewModel.getErrorMessage().isNotEmpty());

        // 绑定按钮禁用状态
        loginButton.disableProperty().bind(viewModel.canLoginProperty().not());
    }

    /**
     * 登录按钮点击事件 - 仅转发到ViewModel
     */
    @FXML
    public void onLogin() {
        viewModel.login();
    }
}
