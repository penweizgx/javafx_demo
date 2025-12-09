package com.example.app.component;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.io.IOException;

/**
 * Reusable FormItem backed by form_item.fxml
 */
public class FormItem extends HBox {
    @FXML private Label lbl;
    @FXML private TextField input;
    @FXML private Label error;

    public FormItem() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/form_item.fxml"));
        loader.setRoot(this);          // ensure FXML content attaches to this HBox
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setLabel(String text) { lbl.setText(text); }
    public String getValue() { return input.getText(); }
    public void setValue(String v) { input.setText(v); }
    public void showError(String msg) {
        error.setText(msg == null ? "" : msg);
        error.setVisible(msg != null && !msg.isEmpty());
        if (msg != null && !msg.isEmpty()) {
            if (!input.getStyleClass().contains("err")) input.getStyleClass().add("err");
        } else {
            input.getStyleClass().remove("err");
        }
    }
}
