package com.example.app.controller;

import com.example.app.AppContext;
import com.example.app.i18n.I18nService;
import com.example.app.i18n.LocaleChangeEvent;
import com.example.app.navigation.EventBus;
import com.example.app.viewmodel.LoginViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.util.function.Consumer;

public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private CheckBox rememberMeCheckBox;
    @FXML
    private Label errorLabel;
    @FXML
    private Button loginButton;
    @FXML
    private Label titleLabel;
    @FXML
    private Label usernameLabel;
    @FXML
    private Label passwordLabel;

    private LoginViewModel viewModel;
    private I18nService i18n;
    private final Consumer<LocaleChangeEvent> localeHandler = e -> refreshI18n();

    public LoginController() {
    }

    public void setViewModel(LoginViewModel viewModel) {
        this.viewModel = viewModel;
        setupBindings();
    }

    private void setupBindings() {
        usernameField.textProperty().bindBidirectional(viewModel.getUsername());
        passwordField.textProperty().bindBidirectional(viewModel.getPassword());
        rememberMeCheckBox.selectedProperty().bindBidirectional(viewModel.getRememberMe());

        errorLabel.textProperty().bind(viewModel.getErrorMessage());
        errorLabel.visibleProperty().bind(viewModel.getErrorMessage().isNotEmpty());

        loginButton.disableProperty().bind(viewModel.canLoginProperty().not());
    }

    @FXML
    public void initialize() {
        try {
            i18n = AppContext.get().getService(I18nService.class);
        } catch (Exception e) {
            // fallback
        }
        refreshI18n();
        EventBus.getInstance().subscribe(LocaleChangeEvent.class, localeHandler);
    }

    private void refreshI18n() {
        if (i18n == null) return;
        titleLabel.setText(i18n.getString("login.title"));
        usernameLabel.setText(i18n.getString("login.username"));
        passwordLabel.setText(i18n.getString("login.password"));
        rememberMeCheckBox.setText(i18n.getString("login.rememberMe"));
        loginButton.setText(i18n.getString("login.button"));
        usernameField.setPromptText(i18n.getString("login.username"));
        passwordField.setPromptText(i18n.getString("login.password"));
    }

    @FXML
    public void onLogin() {
        viewModel.login();
    }

    public void cleanup() {
        EventBus.getInstance().unsubscribe(LocaleChangeEvent.class, localeHandler);
    }
}