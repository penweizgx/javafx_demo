package com.example.app.navigation;

import javafx.scene.Parent;

public interface SubPageContainer {
    void showSubPage(Parent page, RouteParams params);
    void closeSubPage();
    boolean hasSubPage();
    Parent getSubPage();
}