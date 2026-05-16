package com.example.app.guard;

import com.example.app.navigation.RouteParams;
import com.example.app.service.AuthService;
import com.example.app.AppContext;

import java.util.Set;

public class AuthGuard implements NavigationGuard {

    private final AuthService authService;
    private final String loginPath;
    private final Set<String> publicRoutes;

    public AuthGuard(AuthService authService, String loginPath, Set<String> publicRoutes) {
        this.authService = authService;
        this.loginPath = loginPath;
        this.publicRoutes = publicRoutes;
    }

    @Override
    public GuardResult beforeEach(String path, RouteParams params) {
        if (isPublicRoute(path)) {
            return GuardResult.allow();
        }

        if (!authService.isAuthenticated()) {
            return GuardResult.redirect(loginPath);
        }

        return GuardResult.allow();
    }

    @Override
    public void afterEach(String path, RouteParams params) {
    }

    private boolean isPublicRoute(String path) {
        if (publicRoutes == null || publicRoutes.isEmpty()) {
            return false;
        }
        for (String publicPath : publicRoutes) {
            if (path.startsWith(publicPath)) {
                return true;
            }
        }
        return false;
    }
}