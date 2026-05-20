package com.example.app.controller;

import com.example.app.AppContext;
import com.example.app.ThemeChangeEvent;
import com.example.app.ThemeService;
import com.example.app.i18n.I18nService;
import com.example.app.i18n.LocaleChangeEvent;
import com.example.app.navigation.EventBus;
import com.example.app.viewmodel.SystemConfigViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.util.StringConverter;

import java.util.Locale;
import java.util.function.Consumer;

public class SystemConfigController {

    @FXML
    private Label titleLabel;
    @FXML
    private Label languageLabel;
    @FXML
    private ComboBox<Locale> languageComboBox;
    @FXML
    private Label themeLabel;
    @FXML
    private ComboBox<String> themeComboBox;

    private SystemConfigViewModel viewModel;
    private I18nService i18n;
    private final Consumer<LocaleChangeEvent> localeHandler = this::onLocaleChange;
    private final Consumer<ThemeChangeEvent> themeHandler = this::onThemeChange;

    public SystemConfigController() {
    }

    public void setViewModel(SystemConfigViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @FXML
    public void initialize() {
        try {
            this.i18n = AppContext.get().getService(I18nService.class);
            this.viewModel = new SystemConfigViewModel(i18n);
        } catch (Exception e) {
            // fallback
        }

        setupLanguageComboBox();
        setupThemeComboBox();
        refreshI18n();
        EventBus.getInstance().subscribe(LocaleChangeEvent.class, localeHandler);
        EventBus.getInstance().subscribe(ThemeChangeEvent.class, themeHandler);
    }

    private void setupLanguageComboBox() {
        languageComboBox.getItems().addAll(Locale.SIMPLIFIED_CHINESE, Locale.US);

        languageComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Locale locale) {
                if (locale == null) return "";
                if (locale.getLanguage().equals("zh")) return "中文";
                if (locale.getLanguage().equals("en")) return "English";
                return locale.getDisplayName();
            }

            @Override
            public Locale fromString(String string) {
                return null;
            }
        });

        if (i18n != null) {
            languageComboBox.setValue(i18n.getLocale());
        }

        languageComboBox.setOnAction(e -> {
            Locale selected = languageComboBox.getValue();
            if (viewModel != null) {
                viewModel.switchLanguage(selected);
            }
        });
    }

    private void setupThemeComboBox() {
        String light = i18n != null ? i18n.getString("shell.theme.light") : "浅色";
        String dark = i18n != null ? i18n.getString("shell.theme.dark") : "深色";
        themeComboBox.getItems().addAll(light, dark);

        if (viewModel != null && viewModel.isDarkTheme()) {
            themeComboBox.setValue(dark);
        } else {
            themeComboBox.setValue(light);
        }

        themeComboBox.setOnAction(e -> {
            if (viewModel == null || i18n == null) return;
            String darkLabel = i18n.getString("shell.theme.dark");
            boolean isDark = darkLabel.equals(themeComboBox.getValue());
            viewModel.switchTheme(isDark);
        });
    }

    private void onLocaleChange(LocaleChangeEvent event) {
        refreshI18n();
        if (languageComboBox.getValue() != event.getNewLocale()) {
            languageComboBox.setValue(event.getNewLocale());
        }
        refreshThemeComboBox();
    }

    private void onThemeChange(ThemeChangeEvent event) {
        if (viewModel != null) {
            viewModel.setDarkTheme(event.isDark());
        }
        if (i18n == null) return;
        String dark = i18n.getString("shell.theme.dark");
        String light = i18n.getString("shell.theme.light");
        themeComboBox.setValue(event.isDark() ? dark : light);
    }

    private void refreshThemeComboBox() {
        if (i18n == null) return;
        boolean isDark = viewModel != null && viewModel.isDarkTheme();
        String light = i18n.getString("shell.theme.light");
        String dark = i18n.getString("shell.theme.dark");
        themeComboBox.getItems().setAll(light, dark);
        themeComboBox.setValue(isDark ? dark : light);
    }

    private void refreshI18n() {
        if (i18n == null) return;
        titleLabel.setText(i18n.getString("system.config.title"));
        languageLabel.setText(i18n.getString("system.config.language"));
        themeLabel.setText(i18n.getString("system.config.theme"));
    }

    public void cleanup() {
        EventBus.getInstance().unsubscribe(LocaleChangeEvent.class, localeHandler);
        EventBus.getInstance().unsubscribe(ThemeChangeEvent.class, themeHandler);
    }
}
