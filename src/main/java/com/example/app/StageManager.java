package com.example.app;

import javafx.stage.Stage;

public class StageManager {
    private static Stage primaryStage;
    public static void setPrimaryStage(Stage s) { primaryStage = s; }
    public static Stage getPrimaryStage() { return primaryStage; }
}
