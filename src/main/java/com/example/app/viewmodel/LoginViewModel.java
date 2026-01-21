package com.example.app.viewmodel;

import com.example.app.service.AuthService;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import lombok.Getter;

/**
 * 登录界面ViewModel
 */
public class LoginViewModel extends ViewModelBase {

    private final AuthService authService;

    // Properties for two-way binding
    @Getter
    private final StringProperty username = new SimpleStringProperty("");

    @Getter
    private final StringProperty password = new SimpleStringProperty("");

    @Getter
    private final StringProperty errorMessage = new SimpleStringProperty("");

    @Getter
    private final BooleanProperty loginInProgress = new SimpleBooleanProperty(false);

    // Callback
    private Runnable onLoginSuccess;

    public LoginViewModel(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 设置登录成功回调
     */
    public void setOnLoginSuccess(Runnable callback) {
        this.onLoginSuccess = callback;
    }

    /**
     * 执行登录
     */
    public void login() {
        // 验证输入
        if (username.get() == null || username.get().trim().isEmpty()) {
            errorMessage.set("用户名不能为空");
            return;
        }

        if (password.get() == null || password.get().trim().isEmpty()) {
            errorMessage.set("密码不能为空");
            return;
        }

        // 清除之前的错误
        errorMessage.set("");
        loginInProgress.set(true);

        // 异步执行登录
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
                    errorMessage.set(error.getMessage());
                });
    }

    /**
     * 检查是否可以登录（用于按钮绑定）
     */
    public BooleanBinding canLoginProperty() {
        return loginInProgress.not();
    }
}
