package com.example.app.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

public class I18nService {
    
    private ResourceBundle bundle;
    private Locale currentLocale;
    
    public I18nService() {
        this.currentLocale = Locale.getDefault();
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
}