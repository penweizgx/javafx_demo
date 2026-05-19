package com.example.app.navigation;

import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2MZ;

public final class NavigationIcons {

    private NavigationIcons() {}

    public static Ikon getIcon(String iconName) {
        if (iconName == null) return Material2MZ.SEARCH;
        return switch (iconName) {
            case "home" -> Material2MZ.SEARCH;
            case "settings" -> Material2MZ.SEARCH;
            case "business" -> Material2MZ.WORK_OUTLINE;
            case "user", "user-list" -> Material2MZ.PEOPLE_OUTLINE;
            case "user-detail" -> Material2MZ.PHOTO_CAMERA;
            case "form" -> Material2MZ.THUMB_UP;
            case "list" -> Material2MZ.VIEW_LIST;
            default -> Material2MZ.SEARCH;
        };
    }

    public static FontIcon createIcon(String iconName, int size) {
        FontIcon icon = new FontIcon(getIcon(iconName));
        icon.setIconSize(size);
        return icon;
    }
}