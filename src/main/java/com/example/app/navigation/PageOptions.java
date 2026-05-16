package com.example.app.navigation;

import lombok.Data;

@Data
public class PageOptions {

    private boolean cacheable = true;
    private boolean singleton = false;
    private boolean closable = true;
    private boolean supportsSubPage = false;
    private String titleKey;
    private String icon;

    private String permission;
    private java.util.List<String> roles;
    private boolean validateParams;
    private boolean confirmLeave;

    public static PageOptions defaults() {
        return new PageOptions();
    }
}
