package com.example.app.guard;

import com.example.app.navigation.RouteParams;

public interface NavigationGuard {

    GuardResult beforeEach(String path, RouteParams params);

    void afterEach(String path, RouteParams params);
}