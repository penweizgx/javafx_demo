package com.example.app.controller;

import com.example.app.AppContext;
import com.example.app.i18n.I18nService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HomeController {

    @FXML
    private Label lbl;

    private I18nService i18n;

    public HomeController() {
    }

    @FXML
    public void initialize() {
        try {
            this.i18n = AppContext.get().getService(I18nService.class);
        } catch (Exception e) {
            // I18nService may not be registered yet
        }
        
        if (i18n != null) {
            lbl.setText(i18n.getString("home.welcome"));
        } else {
            lbl.setText("Welcome — JavaFX Advanced Sample\nUse the top buttons to navigate.");
        }
    }
}