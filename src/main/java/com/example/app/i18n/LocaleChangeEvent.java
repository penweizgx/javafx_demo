package com.example.app.i18n;

import java.util.Locale;

public class LocaleChangeEvent {

    private final Locale newLocale;

    public LocaleChangeEvent(Locale newLocale) {
        this.newLocale = newLocale;
    }

    public Locale getNewLocale() {
        return newLocale;
    }
}
