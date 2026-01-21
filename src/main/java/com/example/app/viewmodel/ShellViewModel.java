package com.example.app.viewmodel;

import com.example.app.model.User;
import com.example.app.service.UserService;
import javafx.beans.property.*;
import lombok.Getter;

/**
 * 主界面ViewModel
 */
public class ShellViewModel extends ViewModelBase {

    private final UserService userService;

    // Properties
    @Getter
    private final ObjectProperty<User> currentUser = new SimpleObjectProperty<>();

    @Getter
    private final StringProperty userName = new SimpleStringProperty("");

    @Getter
    private final BooleanProperty userLoaded = new SimpleBooleanProperty(false);

    public ShellViewModel(UserService userService) {
        this.userService = userService;

        // 监听currentUser变化，自动更新userName
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

    /**
     * 加载用户信息
     */
    public void loadUserProfile() {
        executeAsync(
                () -> userService.getCurrentUser().join(),
                user -> currentUser.set(user),
                error -> {
                    // 加载失败，可以记录日志或显示错误
                    error.printStackTrace();
                });
    }

    /**
     * 获取用户详情字符串（用于弹窗显示）
     */
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

    /**
     * 获取当前用户对象
     */
    public User getCurrentUserValue() {
        return currentUser.get();
    }
}
