package com.example.app.navigation;

import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.List;

public class QuickAccessBar extends HBox {

    private final List<QuickAccessItem> items = new ArrayList<>();

    public QuickAccessBar(List<NavigationConfig.QuickAccessItem> configItems) {
        getStyleClass().add("quick-access-bar");
        setSpacing(8);

        if (configItems != null) {
            for (NavigationConfig.QuickAccessItem item : configItems) {
                QuickAccessItem quickItem = new QuickAccessItem(item);
                items.add(quickItem);
                getChildren().add(quickItem);
            }
        }
    }

    public void updateBadge(String itemId, int value) {
        for (QuickAccessItem item : items) {
            if (item.getItem().getId().equals(itemId)) {
                item.setBadgeValue(value);
                break;
            }
        }
    }

    public List<QuickAccessItem> getItems() {
        return items;
    }
}