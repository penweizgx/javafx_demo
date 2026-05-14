package com.example.app.viewmodel;

import com.example.app.executor.AsyncExecutor;
import com.example.app.i18n.I18nService;
import com.example.app.service.AuthService;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import lombok.Getter;

import java.util.function.Consumer;

public class LoginViewModel extends ViewModelBase {

    private final AuthService authService;
    private final I18nService i18n;

    @Getter
    private final StringProperty username = new SimpleStringProperty("");

    @Getter
    private final StringProperty password = new SimpleStringProperty("");

    @Getter
    private final StringProperty errorMessage = new SimpleStringProperty("");

    @Getter
    private final BooleanProperty loginInProgress = new SimpleBooleanProperty(false);

    private Runnable onLoginSuccess;

    public LoginViewModel(AuthService authService, I18nService i18n) {
        this.authService = authService;
        this.i18n = i18n;
    }

    public void setOnLoginSuccess(Runnable callback) {
        this.onLoginSuccess = callback;
    }

    public void login() {
        if (username.get() == null || username.get().trim().isEmpty()) {
            errorMessage.set(i18n.getString("login.error.empty.username"));
            return;
        }

        if (password.get() == null || password.get().trim().isEmpty()) {
            errorMessage.set(i18n.getString("login.error.empty.password"));
            return;
        }

        errorMessage.set("");
        loginInProgress.set(true);

        executeAsync(
                () -> {
                    authService.login(username.get(), password.get()).join();
                    return null;
                },
                result -> {
                    loginInProgress.set(false);
                    if (onLoginSuccess != null) {
                        onLoginSuccess.run();
                    }
                },
                error -> {
                    loginInProgress.set(false);
                    errorMessage.set(i18n.getString("login.error.failed") + ": " + error.getMessage());
                });
    }

    public BooleanBinding canLoginProperty() {
        return loginInProgress.not();
    }
}