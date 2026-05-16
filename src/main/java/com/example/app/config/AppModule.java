package com.example.app.config;

import com.example.app.api.ApiService;
import com.example.app.api.okhttp.OkHttpApiServiceImpl;
import com.example.app.guard.AuditLogGuard;
import com.example.app.guard.AuthGuard;
import com.example.app.guard.NavigationGuard;
import com.example.app.i18n.I18nService;
import com.example.app.navigation.NavigationConfig;
import com.example.app.navigation.NavigationConfigLoader;
import com.example.app.router.RouteRegistry;
import com.example.app.router.Router;
import com.example.app.service.AuthService;
import com.example.app.service.UserService;
import com.example.app.service.impl.AuthServiceImpl;
import com.example.app.service.impl.UserServiceImpl;
import com.example.app.storage.TokenStorage;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import java.util.Set;

public class AppModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ApiService.class).to(OkHttpApiServiceImpl.class).in(Singleton.class);
        bind(AuthService.class).to(AuthServiceImpl.class).in(Singleton.class);
        bind(UserService.class).to(UserServiceImpl.class).in(Singleton.class);
        bind(TokenStorage.class).in(Singleton.class);
        bind(I18nService.class).in(Singleton.class);
        bind(RouteRegistry.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    public NavigationConfig provideNavigationConfig() {
        return NavigationConfigLoader.load("/navigation.yaml");
    }

    @Provides
    @Singleton
    public AuthGuard provideAuthGuard(AuthService authService) {
        return new AuthGuard(authService, "/login", Set.of("/login", "/home"));
    }

    @Provides
    @Singleton
    public AuditLogGuard provideAuditLogGuard() {
        return new AuditLogGuard();
    }
}