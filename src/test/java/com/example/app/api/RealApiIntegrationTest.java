package com.example.app.api;

import com.example.app.api.okhttp.OkHttpApiServiceImpl;

import com.example.app.api.storage.ConfigStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Real API integration test.
 * Need to replace credentials before running.
 */
public class RealApiIntegrationTest {

    private OkHttpApiServiceImpl apiService;
    protected ConfigStorage configStorage;

    @BeforeEach
    void setUp() {
        apiService = new OkHttpApiServiceImpl();
        apiService.initHttp();
        configStorage = apiService.getWxMpConfigStorage();
    }

    @Test
    @Disabled("Requires real credentials")
    void loginAndGetCurrentUser_Successful() throws ApiException {
        // Replace with real credentials
        String username = "15828245173";
        String password = "351688";

        System.out.println("Attempting login...");
        apiService.login(username, password);
        System.out.println("Login successful. Token: " + apiService.getAccessToken());

        assertNotNull(apiService.getAccessToken(), "Token should not be null");

        System.out.println("Fetching current user...");
        String userJson = apiService.getCurrentUser();
        System.out.println("User info: " + userJson);

        assertNotNull(userJson, "User info should not be null");
        // Simple check
        assertTrue(userJson.contains("\"code\":200") || userJson.contains("\"code\": 200"),
                "Response should be 200 OK");
    }
}
