package com.example.app.component;

import atlantafx.base.theme.Styles;
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

public class BillDetailModal extends VBox {

    private final BillTimeDTO bill;
    private final Runnable onSuccess;
    private final FinanceManageService financeService;

    private final ProgressIndicator loadingDetail = new ProgressIndicator();
    private final VBox contentBox = new VBox(16);

    public BillDetailModal(BillTimeDTO bill, Runnable onSuccess) {
        this.bill = bill;
        this.onSuccess = onSuccess;
        this.financeService = AppContext.get().getService(FinanceManageService.class);
        initialize();
    }

    private void initialize() {
        setSpacing(16);
        setPadding(new Insets(24));
        setAlignment(Pos.TOP_CENTER);
        setMaxWidth(750);
        setMaxHeight(650);
        setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                 "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 20, 0, 0, 5);");

        String titleText = "账单详情 - " + (bill.getStudentName() != null ? bill.getStudentName() : "未知");
        Label title = new Label(titleText);
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        VBox infoCard = createInfoCard();
        contentBox.getChildren().add(infoCard);

        if (bill.getSubjectItems() != null && !bill.getSubjectItems().isEmpty()) {
            contentBox.getChildren().add(createSubjectItemsCard(bill.getSubjectItems()));
        }

        if (bill.getPayChancels() != null && !bill.getPayChancels().isEmpty()) {
            contentBox.getChildren().add(createPayChannelsCard(bill.getPayChancels()));
        }

        if (bill.getInvalid() != null) {
            VBox invalidCard = new VBox(8);
            invalidCard.setPadding(new Insets(16));
            invalidCard.setStyle("-fx-background-color: #fff3e0; -fx-background-radius: 8;");
            Label invalidTitle = new Label("作废信息");
            invalidTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #e65100;");
            invalidCard.getChildren().add(invalidTitle);
            invalidCard.getChildren().add(createInfoRow("作废日期", bill.getInvalid().getDate() != null ? bill.getInvalid().getDate() : "-"));
            invalidCard.getChildren().add(createInfoRow("作废备注", bill.getInvalid().getRemark() != null ? bill.getInvalid().getRemark() : "-"));
            contentBox.getChildren().add(invalidCard);
        }

        loadingDetail.setMaxSize(30, 30);
        loadingDetail.setVisible(false);

        HBox buttonBox = new HBox(12);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        if (bill.getInvalid() == null && bill.getChargeBillId() != null) {
            Button invalidBtn = new Button("作废");
            invalidBtn.getStyleClass().addAll(Styles.DANGER);
            invalidBtn.setOnAction(e -> handleInvalidCharge());
            buttonBox.getChildren().add(invalidBtn);
        }

        if (bill.getInvalid() == null && bill.getCarryOverSubjectItems() != null && !bill.getCarryOverSubjectItems().isEmpty()) {
            Button invalidCarryBtn = new Button("作废结转");
            invalidCarryBtn.getStyleClass().addAll(Styles.WARNING);
            invalidCarryBtn.setOnAction(e -> handleInvalidCarry());
            buttonBox.getChildren().add(invalidCarryBtn);
        }

        Button closeBtn = new Button("关闭");
        closeBtn.getStyleClass().addAll(Styles.BUTTON_OUTLINED);
        closeBtn.setOnAction(e -> close());
        buttonBox.getChildren().add(closeBtn);

        ScrollPane scrollPane = new ScrollPane(contentBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(450);
        scrollPane.setMaxHeight(450);

        getChildren().addAll(title, loadingDetail, scrollPane, buttonBox);

        if ("CHARGE".equals(bill.getType()) && bill.getChargeBillId() != null) {
            loadChargeDetail(bill.getChargeBillId());
        }
    }

    private VBox createInfoCard() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 8;");

        card.getChildren().addAll(
            createInfoRow("业务日期", bill.getBizDate() != null ? bill.getBizDate() : "-"),
            createInfoRow("学生", bill.getStudentName() != null ? bill.getStudentName() : "-"),
            createInfoRow("班级", bill.getClazzName() != null ? bill.getClazzName() : "-"),
            createInfoRow("应收金额", bill.getTotalAmount() != null ? "¥" + bill.getTotalAmount() : "¥0"),
            createInfoRow("实收金额", bill.getRealAmount() != null ? "¥" + bill.getRealAmount() : "¥0"),
            createInfoRow("抵扣金额", bill.getDeductionAmount() != null ? "¥" + bill.getDeductionAmount() : "¥0"),
            createInfoRow("余额支付", bill.getBalancesAmount() != null ? "¥" + bill.getBalancesAmount() : "¥0"),
            createInfoRow("缴费人", bill.getPayUserName() != null ? bill.getPayUserName() : "-"),
            createInfoRow("备注", bill.getRemark() != null ? bill.getRemark() : "-")
        );
        return card;
    }

    private VBox createSubjectItemsCard(List<SubjectItem> items) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 8;");

        Label cardTitle = new Label("收费项目明细");
        cardTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        card.getChildren().add(cardTitle);

        TableView<SubjectItem> table = new TableView<>();
        table.setPrefHeight(Math.min(items.size() * 32 + 30, 150));

        TableColumn<SubjectItem, String> periodCol = new TableColumn<>("周期");
        periodCol.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getPeriod() != null && data.getValue().getPeriod().getStart() != null
                ? data.getValue().getPeriod().getStart() : "-"));
        periodCol.setPrefWidth(120);

        TableColumn<SubjectItem, String> unitCol = new TableColumn<>("单价");
        unitCol.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getUnitAmount() != null ? "¥" + data.getValue().getUnitAmount() : "¥0"));
        unitCol.setPrefWidth(80);

        TableColumn<SubjectItem, String> receivableCol = new TableColumn<>("应收");
        receivableCol.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getReceivableAmount() != null ? "¥" + data.getValue().getReceivableAmount() : "¥0"));
        receivableCol.setPrefWidth(80);

        TableColumn<SubjectItem, String> payCol = new TableColumn<>("实收");
        payCol.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getPayAmount() != null ? "¥" + data.getValue().getPayAmount() : "¥0"));
        payCol.setPrefWidth(80);

        TableColumn<SubjectItem, String> balanceCol = new TableColumn<>("余额");
        balanceCol.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getBalancesAmount() != null ? "¥" + data.getValue().getBalancesAmount() : "¥0"));
        balanceCol.setPrefWidth(80);

        table.getColumns().addAll(periodCol, unitCol, receivableCol, payCol, balanceCol);
        table.getItems().setAll(items);
        card.getChildren().add(table);
        return card;
    }

    private VBox createPayChannelsCard(List<ChancelAmount> channels) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 8;");

        Label cardTitle = new Label("支付渠道");
        cardTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        card.getChildren().add(cardTitle);

        for (ChancelAmount ch : channels) {
            card.getChildren().add(createInfoRow("渠道#" + ch.getId(), ch.getAmount() != null ? "¥" + ch.getAmount() : "¥0"));
        }
        return card;
    }

    private void loadChargeDetail(Long chargeBillId) {
        loadingDetail.setVisible(true);
        financeService.chargeDetail(chargeBillId).whenComplete((vo, error) -> {
            Platform.runLater(() -> {
                loadingDetail.setVisible(false);
                if (error != null) {
                    contentBox.getChildren().add(new Label("加载收费详情失败: " + error.getMessage()));
                } else if (vo != null) {
                    appendChargeDetail(vo);
                }
            });
        });
    }

    private void appendChargeDetail(ChargeBillVO vo) {
        ChargeBill chargeBill = vo.getBill();
        if (chargeBill == null) return;

        if (chargeBill.getSubjectItems() != null && !chargeBill.getSubjectItems().isEmpty()) {
            contentBox.getChildren().add(createSubjectItemsCard(chargeBill.getSubjectItems()));
        }

        if (chargeBill.getPayChancels() != null && !chargeBill.getPayChancels().isEmpty()) {
            contentBox.getChildren().add(createPayChannelsCard(chargeBill.getPayChancels()));
        }

        if (vo.getCarryOverBillList() != null && !vo.getCarryOverBillList().isEmpty()) {
            contentBox.getChildren().add(createCarryOverCard(vo.getCarryOverBillList()));
        }
    }

    private VBox createCarryOverCard(List<CarryOverBillDTO> carryOverBills) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: #e8f5e9; -fx-background-radius: 8;");

        Label cardTitle = new Label("结转记录");
        cardTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #2e7d32;");
        card.getChildren().add(cardTitle);

        for (CarryOverBillDTO cob : carryOverBills) {
            VBox itemBox = new VBox(4);
            itemBox.setPadding(new Insets(8));
            itemBox.setStyle("-fx-background-color: white; -fx-background-radius: 4;");

            itemBox.getChildren().add(createInfoRow("日期", cob.getBizDate() != null ? cob.getBizDate() : "-"));
            itemBox.getChildren().add(createInfoRow("金额", cob.getTotalAmount() != null ? "¥" + cob.getTotalAmount() : "¥0"));
            itemBox.getChildren().add(createInfoRow("操作人", cob.getWokerName() != null ? cob.getWokerName() : "-"));
            if (cob.getRemark() != null && !cob.getRemark().isEmpty()) {
                itemBox.getChildren().add(createInfoRow("备注", cob.getRemark()));
            }

            if (cob.getInvalid() != null) {
                Label invalidBadge = new Label("已作废");
                invalidBadge.getStyleClass().addAll("badge", Styles.DANGER);
                itemBox.getChildren().add(invalidBadge);
            }

            card.getChildren().add(itemBox);
        }
        return card;
    }

    private void handleInvalidCharge() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("作废账单");
        dialog.setHeaderText("请输入作废原因");
        dialog.setContentText("备注:");
        dialog.showAndWait().ifPresent(remark -> {
            financeService.invalidCharge(bill.getChargeBillId(), remark).whenComplete((v, error) -> {
                Platform.runLater(() -> {
                    if (error != null) {
                        showAlert("作废失败: " + error.getMessage());
                    } else {
                        close();
                        if (onSuccess != null) onSuccess.run();
                    }
                });
            });
        });
    }

    private void handleInvalidCarry() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("作废结转");
        dialog.setHeaderText("请输入作废原因");
        dialog.setContentText("备注:");
        dialog.showAndWait().ifPresent(remark -> {
            financeService.invalidCarry(bill.getId(), remark).whenComplete((v, error) -> {
                Platform.runLater(() -> {
                    if (error != null) {
                        showAlert("作废失败: " + error.getMessage());
                    } else {
                        close();
                        if (onSuccess != null) onSuccess.run();
                    }
                });
            });
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
