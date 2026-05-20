package com.example.app.i18n;

import com.example.app.navigation.EventBus;

import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class I18nService {

    private static final String PREF_KEY = "app.locale";
    private final Preferences prefs = Preferences.userNodeForPackage(I18nService.class);

    private ResourceBundle bundle;
    private Locale currentLocale;

    public I18nService() {
        Optional<Locale> saved = loadLocalePreference();
        this.currentLocale = saved.orElse(Locale.getDefault());
        loadBundle();
    }

    public I18nService(Locale locale) {
        this.currentLocale = locale;
        loadBundle();
    }

    private void loadBundle() {
        bundle = ResourceBundle.getBundle("messages", currentLocale);
    }

    public void setLocale(Locale locale) {
        this.currentLocale = locale;
        loadBundle();
        saveLocalePreference(locale);
        EventBus.getInstance().publish(new LocaleChangeEvent(locale));
    }

    public Locale getLocale() {
        return currentLocale;
    }

    public String getString(String key) {
        return bundle.getString(key);
    }

    public String getString(String key, Object... args) {
        String value = bundle.getString(key);
        return String.format(value, args);
    }

    public boolean isChinese() {
        return currentLocale.getLanguage().equals("zh");
    }

    public void toggleLanguage() {
        if (isChinese()) {
            setLocale(Locale.US);
        } else {
            setLocale(Locale.SIMPLIFIED_CHINESE);
        }
    }

    public void saveLocalePreference(Locale locale) {
        try {
            prefs.put(PREF_KEY, locale.toString());
            prefs.flush();
        } catch (Exception ignored) {
        }
    }

    public Optional<Locale> loadLocalePreference() {
        try {
            String value = prefs.get(PREF_KEY, null);
            if (value != null) {
                return parseLocale(value);
            }
        } catch (Exception ignored) {
        }
        return Optional.empty();
    }

    private Optional<Locale> parseLocale(String value) {
        String[] parts = value.split("_");
        if (parts.length == 1) {
            return Optional.of(new Locale(parts[0]));
        } else if (parts.length == 2) {
            return Optional.of(new Locale(parts[0], parts[1]));
        }
        return Optional.empty();
    }
}
