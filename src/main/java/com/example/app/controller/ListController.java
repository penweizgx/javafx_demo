package com.example.app.controller;

import com.example.app.AppContext;
import com.example.app.i18n.I18nService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ListController {

    @FXML
    private Label lbl;

    private I18nService i18n;

    public ListController() {
    }

    @FXML
    public void initialize() {
        try {
            this.i18n = AppContext.get().getService(I18nService.class);
        } catch (Exception e) {
            // I18nService may not be registered yet
        }
        
        if (i18n != null) {
            lbl.setText(i18n.getString("list.placeholder"));
        } else {
            lbl.setText("示例列表页（可以扩展为 TableView）。\n点击顶部导航切换页面，试试回退按钮。");
        }
    }
}