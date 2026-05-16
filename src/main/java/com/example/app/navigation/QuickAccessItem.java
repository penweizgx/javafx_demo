package com.example.app.navigation;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class QuickAccessItem extends HBox {

    private final NavigationConfig.QuickAccessItem item;
    private final Button button;
    private final Label badge;

    public QuickAccessItem(NavigationConfig.QuickAccessItem item) {
        this.item = item;
        getStyleClass().add("quick-access-item");

        button = new Button();
        button.setText(item.getLabel());
        button.getStyleClass().add("quick-access-button");

        badge = new Label();
        badge.getStyleClass().add("quick-access-badge");
        badge.setVisible(item.isShowBadge());

        getChildren().addAll(button, badge);

        button.setOnAction(e -> onClick());
    }

    private void onClick() {
        String path = item.getPath();
        RouteParams params = item.getParams() != null ? item.getParams() : RouteParams.empty();
        EventBus.getInstance().publish(new NavigationClickEvent(null, path, params));
    }

    public void setBadgeValue(int value) {
        if (item.isShowBadge()) {
            badge.setText(String.valueOf(value));
            badge.setVisible(value > 0);
        }
    }

    public NavigationConfig.QuickAccessItem getItem() {
        return item;
    }
}