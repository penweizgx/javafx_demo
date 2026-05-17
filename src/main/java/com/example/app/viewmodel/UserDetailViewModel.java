package com.example.app.viewmodel;

import com.example.app.executor.AsyncExecutor;
import com.example.app.exception.ExceptionHandler;
import com.example.app.model.User;
import com.example.app.service.UserManageService;
import javafx.beans.property.*;
import lombok.Getter;

public class UserDetailViewModel extends ViewModelBase {

    private final UserManageService userService;

    @Getter
    private final ObjectProperty<User> user = new SimpleObjectProperty<>();

    @Getter
    private final StringProperty userId = new SimpleStringProperty();

    @Getter
    private final BooleanProperty loading = new SimpleBooleanProperty(false);

    @Getter
    private final BooleanProperty editMode = new SimpleBooleanProperty(false);

    @Getter
    private final StringProperty errorMessage = new SimpleStringProperty("");

    @Getter
    private final StringProperty name = new SimpleStringProperty();

    @Getter
    private final StringProperty email = new SimpleStringProperty();

    @Getter
    private final StringProperty phone = new SimpleStringProperty();

    @Getter
    private final StringProperty orgName = new SimpleStringProperty();

    @Getter
    private final StringProperty role = new SimpleStringProperty();

    @Getter
    private final StringProperty status = new SimpleStringProperty();

    public UserDetailViewModel(UserManageService userService) {
        this.userService = userService;
    }

    public ObjectProperty<User> userProperty() {
        return user;
    }

    public BooleanProperty loadingProperty() {
        return loading;
    }

    public BooleanProperty editModeProperty() {
        return editMode;
    }

    public StringProperty errorMessageProperty() {
        return errorMessage;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty emailProperty() {
        return email;
    }

    public StringProperty phoneProperty() {
        return phone;
    }

    public StringProperty orgNameProperty() {
        return orgName;
    }

    public StringProperty roleProperty() {
        return role;
    }

    public StringProperty statusProperty() {
        return status;
    }

    public void loadUser(String id) {
        if (id == null || id.isEmpty()) {
            errorMessage.set("用户ID不能为空");
            return;
        }

        userId.set(id);
        loading.set(true);
        errorMessage.set("");

        executeAsync(
            () -> userService.getUserById(id).join(),
            this::onUserLoaded,
            this::onLoadError
        );
    }

    private void onUserLoaded(User loadedUser) {
        user.set(loadedUser);
        updateFields(loadedUser);
        loading.set(false);
    }

    private void updateFields(User u) {
        name.set(u.getName());
        email.set(u.getEmail());
        phone.set(u.getPhone());
        orgName.set(u.getOrgName());
        role.set(u.getRole());
        status.set(u.getStatus());
    }

    private void onLoadError(Throwable error) {
        loading.set(false);
        errorMessage.set("加载用户详情失败: " + error.getMessage());
        ExceptionHandler.handle(error, "Failed to load user detail");
    }

    public void enterEditMode() {
        editMode.set(true);
    }

    public void cancelEdit() {
        editMode.set(false);
        if (user.get() != null) {
            updateFields(user.get());
        }
    }

    public void saveUser() {
        User current = user.get();
        if (current == null) return;

        current.setName(name.get());
        current.setEmail(email.get());
        current.setPhone(phone.get());
        current.setOrgName(orgName.get());
        current.setRole(role.get());
        current.setStatus(status.get());

        loading.set(true);
        executeAsync(
            () -> userService.updateUser(current).join(),
            updated -> {
                user.set(updated);
                editMode.set(false);
                loading.set(false);
            },
            error -> {
                loading.set(false);
                errorMessage.set("保存失败: " + error.getMessage());
                ExceptionHandler.handle(error, "Failed to save user");
            }
        );
    }

    public String getStatusText() {
        String s = status.get();
        if (s == null) return "";
        return switch (s) {
            case "active" -> "正常";
            case "inactive" -> "停用";
            case "pending" -> "待审核";
            default -> s;
        };
    }
}
