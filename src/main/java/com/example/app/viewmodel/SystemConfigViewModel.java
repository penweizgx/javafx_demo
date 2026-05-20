package com.example.app.viewmodel;

import com.example.app.ThemeChangeEvent;
import com.example.app.ThemeService;
import com.example.app.i18n.I18nService;
import com.example.app.navigation.EventBus;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.Locale;

public class SystemConfigViewModel extends ViewModelBase {

    private final I18nService i18nService;
    private final ObjectProperty<Locale> selectedLocale = new SimpleObjectProperty<>();
    private boolean darkTheme;

    public SystemConfigViewModel(I18nService i18nService) {
        this.i18nService = i18nService;
        this.selectedLocale.set(i18nService.getLocale());
        this.darkTheme = ThemeService.isDarkPersisted();
    }

    public ObjectProperty<Locale> selectedLocaleProperty() {
        return selectedLocale;
    }

    public void switchLanguage(Locale locale) {
        if (locale != null && !locale.equals(i18nService.getLocale())) {
            i18nService.setLocale(locale);
            selectedLocale.set(locale);
        }
    }

    public boolean isDarkTheme() {
        return darkTheme;
    }

    public void switchTheme(boolean dark) {
        this.darkTheme = dark;
        EventBus.getInstance().publish(new ThemeChangeEvent(dark));
    }

    public void setDarkTheme(boolean dark) {
        this.darkTheme = dark;
    }

    public I18nService getI18nService() {
        return i18nService;
    }
}
