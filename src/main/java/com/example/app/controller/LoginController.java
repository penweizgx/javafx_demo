package com.example.app.controller;

import com.example.app.viewmodel.LoginViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

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

    public LoginController() {
    }

    public void setViewModel(LoginViewModel viewModel) {
        this.viewModel = viewModel;
        setupBindings();
    }

    private void setupBindings() {
        usernameField.textProperty().bindBidirectional(viewModel.getUsername());
        passwordField.textProperty().bindBidirectional(viewModel.getPassword());

        errorLabel.textProperty().bind(viewModel.getErrorMessage());
        errorLabel.visibleProperty().bind(viewModel.getErrorMessage().isNotEmpty());

        loginButton.disableProperty().bind(viewModel.canLoginProperty().not());
    }

    @FXML
    public void onLogin() {
        viewModel.login();
    }
}