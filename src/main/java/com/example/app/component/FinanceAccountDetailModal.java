package com.example.app.component;

import atlantafx.base.theme.Styles;
import atlantafx.base.controls.Card;
import com.example.app.AppContext;
import com.example.app.model.*;
import com.example.app.service.DialogService;
import com.example.app.service.FinanceManageService;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;
import java.util.stream.Collectors;

public class FinanceAccountDetailModal extends VBox {

    private final FinAccountVO account;
    private final Runnable onSuccess;
    private final FinanceManageService financeService;

    private final TableView<BillTimeDTO> billTable = new TableView<>();
    private final ProgressIndicator loadingBills = new ProgressIndicator();

    public FinanceAccountDetailModal(FinAccountVO account, Runnable onSuccess) {
        this.account = account;
        this.onSuccess = onSuccess;
        this.financeService = AppContext.get().getService(FinanceManageService.class);
        initialize();
    }

    private void initialize() {
        setSpacing(16);
        setPadding(new Insets(24));
        setAlignment(Pos.TOP_CENTER);
        setMaxWidth(700);
        setMaxHeight(600);
        setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                 "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 20, 0, 0, 5);");

        StudentVO student = account.getStudent();
        String studentName = student != null ? student.getName() : "未知";

        Label title = new Label("账户详情 - " + studentName);
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        VBox infoCard = new VBox(12);
        infoCard.setPadding(new Insets(16));
        infoCard.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 8;");

        infoCard.getChildren().addAll(
            createInfoRow("姓名", studentName),
            createInfoRow("班级", student != null ? student.getClazzname() : "-"),
            createInfoRow("余额", account.getBalancesAmount() != null ? "¥" + account.getBalancesAmount() : "¥0"),
            createInfoRow("累计缴费", account.getTotalAmount() != null ? "¥" + account.getTotalAmount() : "¥0"),
            createInfoRow("退费金额", account.getRefundAmount() != null ? "¥" + account.getRefundAmount() : "¥0"),
            createInfoRow("状态", account.getClosed() != null && account.getClosed() ? "已封账" : "正常")
        );

        VBox feeCard = new VBox(8);
        feeCard.setPadding(new Insets(16));
        feeCard.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 8;");

        Label feeTitle = new Label("收费项目");
        feeTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        feeCard.getChildren().add(feeTitle);

        if (account.getFeeScales() != null && !account.getFeeScales().isEmpty()) {
            for (FeeScale fs : account.getFeeScales()) {
                String unitLabel = fs.getUnit() != null ? fs.getUnit() : "";
                String amountStr = fs.getStandardAmount() != null ? "¥" + fs.getStandardAmount() + "/" + unitLabel : "-";
                feeCard.getChildren().add(createInfoRow(fs.getName(), amountStr));
            }
        } else {
            feeCard.getChildren().add(new Label("未绑定收费项目"));
        }

        Label billTitle = new Label("收费记录");
        billTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        setupBillTable();
        loadingBills.setMaxSize(30, 30);

        HBox buttonBox = new HBox(12);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        Button chargeBtn = new Button("收费");
        chargeBtn.getStyleClass().addAll(Styles.ACCENT);
        chargeBtn.setOnAction(e -> {
            close();
            new ChargeModal(account, onSuccess).show();
        });

        Button feeScaleBtn = new Button("设标准");
        feeScaleBtn.getStyleClass().addAll(Styles.BUTTON_OUTLINED);
        feeScaleBtn.setOnAction(e -> {
            close();
            new FeeScaleBindModal(account, onSuccess).show();
        });

        Button closeAccountBtn = new Button("封账");
        closeAccountBtn.getStyleClass().addAll(Styles.DANGER);
        closeAccountBtn.setOnAction(e -> handleCloseAccount());

        Button closeBtn = new Button("关闭");
        closeBtn.getStyleClass().addAll(Styles.BUTTON_OUTLINED);
        closeBtn.setOnAction(e -> close());

        buttonBox.getChildren().addAll(chargeBtn, feeScaleBtn, closeAccountBtn, closeBtn);

        ScrollPane scrollPane = new ScrollPane(billTable);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(200);
        scrollPane.setMaxHeight(200);

        getChildren().addAll(title, infoCard, feeCard, billTitle, loadingBills, scrollPane, buttonBox);

        loadBills();
    }

    private void setupBillTable() {
        TableColumn<BillTimeDTO, String> dateCol = new TableColumn<>("日期");
        dateCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBizDate()));
        dateCol.setPrefWidth(100);

        TableColumn<BillTimeDTO, String> typeCol = new TableColumn<>("类型");
        typeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getType()));
        typeCol.setPrefWidth(60);

        TableColumn<BillTimeDTO, String> amountCol = new TableColumn<>("金额");
        amountCol.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getTotalAmount() != null ? "¥" + data.getValue().getTotalAmount() : "¥0"));
        amountCol.setPrefWidth(80);

        TableColumn<BillTimeDTO, String> payerCol = new TableColumn<>("缴费人");
        payerCol.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getPayUserName() != null ? data.getValue().getPayUserName() : "-"));
        payerCol.setPrefWidth(80);

        TableColumn<BillTimeDTO, String> statusCol = new TableColumn<>("状态");
        statusCol.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getInvalid() != null ? "已作废" : "有效"));
        statusCol.setPrefWidth(60);
        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setGraphic(null);
                } else {
                    Label badge = new Label(status);
                    badge.getStyleClass().addAll("badge",
                        "有效".equals(status) ? Styles.SUCCESS : Styles.DANGER);
                    setGraphic(badge);
                }
            }
        });

        billTable.getColumns().addAll(dateCol, typeCol, amountCol, payerCol, statusCol);
    }

    private void loadBills() {
        loadingBills.setVisible(true);
        financeService.chargeBills(account.getStudentId()).whenComplete((bills, error) -> {
            Platform.runLater(() -> {
                loadingBills.setVisible(false);
                if (error != null) {
                    billTable.setPlaceholder(new Label("加载失败: " + error.getMessage()));
                } else if (bills != null) {
                    billTable.getItems().setAll(bills);
                    if (bills.isEmpty()) {
                        billTable.setPlaceholder(new Label("暂无收费记录"));
                    }
                }
            });
        });
    }

    private void handleCloseAccount() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("确认封账");
        confirm.setHeaderText(null);
        confirm.setContentText("确定要封账吗？封账后将无法继续收费。");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                financeService.close(account.getStudentId()).whenComplete((v, error) -> {
                    Platform.runLater(() -> {
                        if (error != null) {
                            showAlert("封账失败: " + error.getMessage());
                        } else {
                            close();
                            if (onSuccess != null) onSuccess.run();
                        }
                    });
                });
            }
        });
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("提示");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void close() {
        Runnable closer = (Runnable) getProperties().get("dialog-complete");
        if (closer != null) closer.run();
    }

    public void show() {
        DialogService dialogService = AppContext.get().getService(DialogService.class);
        if (dialogService != null) {
            dialogService.showModal(this, true);
        }
    }

    private HBox createInfoRow(String label, String value) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        Label labelNode = new Label(label + ":");
        labelNode.setPrefWidth(80);
        labelNode.setStyle("-fx-text-fill: #666; -fx-font-size: 13px;");
        Label valueNode = new Label(value != null ? value : "-");
        valueNode.setStyle("-fx-font-size: 13px; -fx-font-weight: 500;");
        HBox.setHgrow(valueNode, Priority.ALWAYS);
        row.getChildren().addAll(labelNode, valueNode);
        return row;
    }
}
