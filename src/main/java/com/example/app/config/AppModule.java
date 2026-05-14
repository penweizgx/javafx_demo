package com.example.app.config;

import com.example.app.api.ApiService;
import com.example.app.api.okhttp.OkHttpApiServiceImpl;
import com.example.app.i18n.I18nService;
import com.example.app.service.AuthService;
import com.example.app.service.UserService;
import com.example.app.service.impl.AuthServiceImpl;
import com.example.app.service.impl.UserServiceImpl;
import com.example.app.storage.TokenStorage;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class AppModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ApiService.class).to(OkHttpApiServiceImpl.class).in(Singleton.class);
        bind(AuthService.class).to(AuthServiceImpl.class).in(Singleton.class);
        bind(UserService.class).to(UserServiceImpl.class).in(Singleton.class);
        bind(TokenStorage.class).in(Singleton.class);
        bind(I18nService.class).in(Singleton.class);
    }
}
