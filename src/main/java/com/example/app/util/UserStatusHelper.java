package com.example.app.util;

import atlantafx.base.theme.Styles;

public final class UserStatusHelper {

    private UserStatusHelper() {}

    public static String getStatusText(String status) {
        if (status == null) return "-";
        return switch (status) {
            case "active" -> "正常";
            case "inactive" -> "停用";
            case "pending" -> "待审核";
            default -> status;
        };
    }

    public static String getStatusStyle(String status) {
        if (status == null) return "";
        return switch (status) {
            case "active" -> Styles.SUCCESS;
            case "inactive" -> Styles.DANGER;
            case "pending" -> Styles.WARNING;
            default -> "";
        };
    }
}