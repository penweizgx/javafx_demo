package com.example.app.guard;

import com.example.app.navigation.RouteParams;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuditLogGuard implements NavigationGuard {

    @Override
    public GuardResult beforeEach(String path, RouteParams params) {
        return GuardResult.allow();
    }

    @Override
    public void afterEach(String path, RouteParams params) {
        log.info("Page view: {} at {}", path, System.currentTimeMillis());
    }
}