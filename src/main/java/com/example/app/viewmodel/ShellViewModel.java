package com.example.app.viewmodel;

import com.example.app.executor.AsyncExecutor;
import com.example.app.exception.ExceptionHandler;
import com.example.app.model.User;
import com.example.app.service.UserService;
import javafx.beans.property.*;
import lombok.Getter;

public class ShellViewModel extends ViewModelBase {

    private final UserService userService;

    @Getter
    private final ObjectProperty<User> currentUser = new SimpleObjectProperty<>();

    @Getter
    private final StringProperty userName = new SimpleStringProperty("");

    @Getter
    private final BooleanProperty userLoaded = new SimpleBooleanProperty(false);

    public ShellViewModel(UserService userService) {
        this.userService = userService;

        currentUser.addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                userName.set(newVal.getName());
                userLoaded.set(true);
            } else {
                userName.set("");
                userLoaded.set(false);
            }
        });
    }

    public void loadUserProfile() {
        executeAsync(
                () -> userService.getCurrentUser().join(),
                user -> currentUser.set(user),
                error -> ExceptionHandler.handle(error, "Failed to load user profile"));
    }

    public String getUserDetailsText() {
        User user = currentUser.get();
        if (user == null) {
            return "用户信息未加载";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("姓名: ").append(user.getName()).append("\n");
        sb.append("机构: ").append(user.getOrgName() != null ? user.getOrgName() : "N/A").append("\n");
        sb.append("机构层级: ").append(user.getOrgBound() != null ? user.getOrgBound().name() : "N/A");

        return sb.toString();
    }

    public User getCurrentUserValue() {
        return currentUser.get();
    }
}