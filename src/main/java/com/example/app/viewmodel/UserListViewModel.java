package com.example.app.viewmodel;

import com.example.app.executor.AsyncExecutor;
import com.example.app.exception.ExceptionHandler;
import com.example.app.model.User;
import com.example.app.service.UserManageService;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;

import java.util.List;
import java.util.function.Consumer;

public class UserListViewModel extends ViewModelBase {

    private final UserManageService userService;

    @Getter
    private final ObservableList<User> userList = FXCollections.observableArrayList();

    @Getter
    private final StringProperty searchText = new SimpleStringProperty("");

    @Getter
    private final BooleanProperty loading = new SimpleBooleanProperty(false);

    @Getter
    private final StringProperty errorMessage = new SimpleStringProperty("");

    private Consumer<User> onUserSelected;

    public UserListViewModel(UserManageService userService) {
        this.userService = userService;
    }

    public void setOnUserSelected(Consumer<User> callback) {
        this.onUserSelected = callback;
    }

    public BooleanProperty loadingProperty() {
        return loading;
    }

    public StringProperty errorMessageProperty() {
        return errorMessage;
    }

    public StringProperty searchTextProperty() {
        return searchText;
    }

    public void loadUsers() {
        loading.set(true);
        errorMessage.set("");
        
        executeAsync(
            () -> userService.getUserList().join(),
            this::onUsersLoaded,
            this::onLoadError
        );
    }

    private void onUsersLoaded(List<User> users) {
        userList.setAll(users);
        loading.set(false);
    }

    private void onLoadError(Throwable error) {
        loading.set(false);
        errorMessage.set("加载用户列表失败: " + error.getMessage());
        ExceptionHandler.handle(error, "Failed to load users");
    }

    public void selectUser(User user) {
        if (onUserSelected != null && user != null) {
            onUserSelected.accept(user);
        }
    }

    public void search() {
        String keyword = searchText.get().trim().toLowerCase();
        if (keyword.isEmpty()) {
            loadUsers();
            return;
        }

        loading.set(true);
        executeAsync(
            () -> userService.getUserList().join(),
            users -> {
                List<User> filtered = users.stream()
                    .filter(u -> u.getName().toLowerCase().contains(keyword) ||
                                 u.getEmail().toLowerCase().contains(keyword) ||
                                 u.getOrgName().toLowerCase().contains(keyword))
                    .toList();
                userList.setAll(filtered);
                loading.set(false);
            },
            this::onLoadError
        );
    }

    public void deleteUser(User user) {
        executeAsync(
            () -> { userService.deleteUser(user.getId()).join(); return null; },
            v -> loadUsers(),
            error -> {
                errorMessage.set("删除用户失败: " + error.getMessage());
                ExceptionHandler.handle(error, "Failed to delete user");
            }
        );
    }
}
