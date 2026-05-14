package com.example.app.viewmodel;

import com.example.app.i18n.I18nService;
import com.example.app.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class LoginViewModelTest {

    @Mock
    private AuthService authService;

    @Mock
    private I18nService i18n;

    private LoginViewModel viewModel;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(i18n.getString(anyString())).thenReturn("Error message");
        viewModel = new LoginViewModel(authService, i18n);
    }

    @Test
    void login_EmptyUsername_ShouldSetErrorMessage() {
        viewModel.getUsername().set("");
        viewModel.getPassword().set("password");

        viewModel.login();

        assertEquals("Error message", viewModel.getErrorMessage().get());
        verify(authService, never()).login(anyString(), anyString());
    }

    @Test
    void login_EmptyPassword_ShouldSetErrorMessage() {
        viewModel.getUsername().set("user");
        viewModel.getPassword().set("");

        viewModel.login();

        assertEquals("Error message", viewModel.getErrorMessage().get());
        verify(authService, never()).login(anyString(), anyString());
    }

    @Test
    void login_ValidCredentials_ShouldCallAuthService() throws InterruptedException {
        viewModel.getUsername().set("user");
        viewModel.getPassword().set("password");
        when(authService.login(anyString(), anyString())).thenReturn(CompletableFuture.completedFuture(null));

        viewModel.login();

        Thread.sleep(100);
        verify(authService, times(1)).login("user", "password");
    }

    @Test
    void canLogin_WhenNotInProgress_ShouldReturnTrue() {
        assertTrue(viewModel.canLoginProperty().get());
    }
}
