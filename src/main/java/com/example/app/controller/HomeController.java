package com.example.app.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HomeController {

    @FXML private Label lbl;

    @FXML
    public void initialize() {
        lbl.setText("Welcome — JavaFX Advanced Sample\nUse the top buttons to navigate.");
    }
}
