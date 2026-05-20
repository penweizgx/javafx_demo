package com.example.app;

import javafx.scene.Scene;

import java.util.prefs.Preferences;

import com.example.app.navigation.EventBus;

public class ThemeService {
    private static final String PREF_KEY = "app.theme";
    private static final Preferences prefs = Preferences.userNodeForPackage(ThemeService.class);
    private final Scene scene;
    private boolean dark = false;

    public ThemeService(Scene s) { this.scene = s; }

    public void setLight() {
        dark = false;
        scene.getStylesheets().removeIf(u -> u.endsWith("dark.css"));
        if (scene.getStylesheets().stream().noneMatch(u -> u.endsWith("light.css")))
            scene.getStylesheets().add(getClass().getResource("/css/light.css").toExternalForm());
        saveThemePreference();
        EventBus.getInstance().publish(new ThemeChangeEvent(false));
    }

    public void setDark() {
        dark = true;
        scene.getStylesheets().removeIf(u -> u.endsWith("light.css"));
        if (scene.getStylesheets().stream().noneMatch(u -> u.endsWith("dark.css")))
            scene.getStylesheets().add(getClass().getResource("/css/dark.css").toExternalForm());
        saveThemePreference();
        EventBus.getInstance().publish(new ThemeChangeEvent(true));
    }

    public boolean isDark() {
        return dark;
    }

    public void restoreTheme() {
        String saved = prefs.get(PREF_KEY, "light");
        if ("dark".equals(saved)) {
            setDark();
        } else {
            setLight();
        }
    }

    public static boolean isDarkPersisted() {
        return "dark".equals(prefs.get(PREF_KEY, "light"));
    }

    private void saveThemePreference() {
        try {
            prefs.put(PREF_KEY, dark ? "dark" : "light");
            prefs.flush();
        } catch (Exception ignored) {
        }
    }
}
