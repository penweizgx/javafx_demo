package com.example.app.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ListController {

    @FXML private Label lbl;

    @FXML
    public void initialize() {
        lbl.setText("示例列表页（可以扩展为 TableView）。\n点击顶部导航切换页面，试试回退按钮。");
    }
}
