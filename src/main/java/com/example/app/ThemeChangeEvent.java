package com.example.app;

public class ThemeChangeEvent {
    private final boolean dark;

    public ThemeChangeEvent(boolean dark) {
        this.dark = dark;
    }

    public boolean isDark() {
        return dark;
    }
}
