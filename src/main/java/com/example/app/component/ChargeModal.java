package com.example.app.component;

import atlantafx.base.theme.Styles;
import com.example.app.AppContext;
import com.example.app.model.*;
import com.example.app.service.DialogService;
import com.example.app.service.FinanceManageService;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChargeModal extends VBox {

    private final FinAccountVO account;
    private final Runnable onSuccess;
    private final FinanceManageService financeService;

    private final VBox feeItemsBox = new VBox(8);
    private final ComboBox<PayChancel> payChancelCombo = new ComboBox<>();
    private final TextField payChancelAmountField = new TextField();
    private final ComboBox<StudentParent> payerCombo = new ComboBox<>();
    private final TextField totalAmountField = new TextField();
    private final TextField deductionField = new TextField("0");
    private final DatePicker bizDatePicker = new DatePicker();
    private final TextArea remarkArea = new TextArea();
    private final ProgressIndicator loadingIndicator = new ProgressIndicator();
    private final Label balanceLabel = new Label();

    private ChargeFormBuildDTO formData;
    private final Map<Long, SubjectItemRow> subjectItemRows = new HashMap<>();

    public ChargeModal(FinAccountVO account, Runnable onSuccess) {
        this.account = account;
        this.onSuccess = onSuccess;
        this.financeService = AppContext.get().getService(FinanceManageService.class);
        initialize();
    }

    private void initialize() {
        setSpacing(16);
        setPadding(new Insets(24));
        setAlignment(Pos.TOP_CENTER);
        setMaxWidth(600);
        setMaxHeight(700);
        setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                 "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 20, 0, 0, 5);");

        StudentVO student = account.getStudent();
        String studentName = student != null ? student.getName() : "未知";

        Label title = new Label("收费 - " + studentName);
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        VBox infoRow = new VBox(8);
        infoRow.setPadding(new Insets(12));
        infoRow.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 8;");
        infoRow.getChildren().addAll(
            createInfoRow("班级", student != null ? student.getClazzname() : "-"),
            createInfoRow("当前余额", account.getBalancesAmount() != null ? "¥" + account.getBalancesAmount() : "¥0")
        );
        balanceLabel.setText(account.getBalancesAmount() != null ? account.getBalancesAmount().toString() : "0");

        Label feeTitle = new Label("收费项目");
        feeTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        feeItemsBox.setPadding(new Insets(12));
        feeItemsBox.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 8;");

        loadingIndicator.setMaxSize(30, 30);
        loadingIndicator.setVisible(true);

        VBox paySection = new VBox(8);
        paySection.setPadding(new Insets(12));
        paySection.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 8;");

        Label payTitle = new Label("支付信息");
        payTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        payChancelCombo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(PayChancel item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName());
            }
        });
        payChancelCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(PayChancel item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName());
            }
        });

        payerCombo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(StudentParent item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName());
            }
        });
        payerCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(StudentParent item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName());
            }
        });

        paySection.getChildren().addAll(
            payTitle,
            createFormRow("支付方式", payChancelCombo),
            createFormRow("支付金额", payChancelAmountField),
            createFormRow("缴费人", payerCombo),
            createFormRow("业务日期", bizDatePicker),
            createFormRow("抵扣金额", deductionField),
            createFormRow("备注", remarkArea)
        );

        HBox totalRow = new HBox(12);
        totalRow.setAlignment(Pos.CENTER_LEFT);
        Label totalLabel = new Label("应收合计:");
        totalLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        totalAmountField.setEditable(false);
        totalAmountField.setPrefWidth(120);
        totalAmountField.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        totalRow.getChildren().addAll(totalLabel, totalAmountField);

        HBox buttonBox = new HBox(12);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        Button cancelBtn = new Button("取消");
        cancelBtn.getStyleClass().addAll(Styles.BUTTON_OUTLINED);
        cancelBtn.setOnAction(e -> close());

        Button submitBtn = new Button("确认收费");
        submitBtn.getStyleClass().addAll(Styles.ACCENT);
        submitBtn.setOnAction(e -> handleSubmit());

        buttonBox.getChildren().addAll(cancelBtn, submitBtn);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(400);
        scrollPane.setContent(new VBox(16, feeTitle, feeItemsBox, loadingIndicator, paySection, totalRow));

        getChildren().addAll(title, infoRow, scrollPane, buttonBox);

        loadFormData();
    }

    private void loadFormData() {
        financeService.buildChargeForm(account.getStudentId()).whenComplete((data, error) -> {
            Platform.runLater(() -> {
                loadingIndicator.setVisible(false);
                if (error != null) {
                    feeItemsBox.getChildren().add(new Label("加载失败: " + error.getMessage()));
                    return;
                }
                formData = data;
                populateForm(data);
            });
        });
    }

    private void populateForm(ChargeFormBuildDTO data) {
        feeItemsBox.getChildren().clear();

        if (data.getSubjectFeeSales() != null) {
            for (SubjectFeeSale sale : data.getSubjectFeeSales()) {
                SubjectItemRow row = new SubjectItemRow(sale);
                subjectItemRows.put(sale.getId(), row);
                row.selectedProperty().addListener((obs, old, val) -> recalculateTotal());
                row.periodProperty().addListener((obs, old, val) -> recalculateTotal());
                feeItemsBox.getChildren().add(row);
            }
        }

        if (data.getChancels() != null) {
            List<PayChancel> visible = data.getChancels().stream()
                .filter(c -> c.getHide() == null || !c.getHide())
                .toList();
            payChancelCombo.getItems().setAll(visible);
            if (!visible.isEmpty()) payChancelCombo.setValue(visible.get(0));
        }

        if (data.getParents() != null) {
            payerCombo.getItems().setAll(data.getParents());
            if (!data.getParents().isEmpty()) payerCombo.setValue(data.getParents().get(0));
        }

        bizDatePicker.setValue(java.time.LocalDate.now());
        recalculateTotal();
    }

    private void recalculateTotal() {
        double total = 0;
        for (SubjectItemRow row : subjectItemRows.values()) {
            if (row.isSelected()) {
                total += row.getCalculatedAmount();
            }
        }
        totalAmountField.setText(String.format("¥%.2f", total));
    }

    private void handleSubmit() {
        List<SubjectItem> items = new ArrayList<>();
        double totalPay = 0;

        for (SubjectItemRow row : subjectItemRows.values()) {
            if (row.isSelected()) {
                SubjectItem item = new SubjectItem();
                item.setFeeScaleId(row.getFeeScaleId());
                item.setUnitAmount(row.getPeriod());
                double receivable = row.getCalculatedAmount();
                item.setReceivableAmount(receivable);
                item.setPayAmount(receivable);
                item.setBalancesAmount(0);
                items.add(item);
                totalPay += receivable;
            }
        }

        if (items.isEmpty()) {
            showAlert("请至少选择一个收费项目");
            return;
        }

        double deduction = 0;
        try {
            deduction = Double.parseDouble(deductionField.getText().trim());
        } catch (NumberFormatException ignored) {}

        double payAmount = totalPay - deduction;

        List<ChancelAmount> chancelAmounts = new ArrayList<>();
        PayChancel selectedChancel = payChancelCombo.getValue();
        if (selectedChancel != null) {
            ChancelAmount ca = new ChancelAmount();
            ca.setId(selectedChancel.getId());
            ca.setAmount(payAmount);
            chancelAmounts.add(ca);
        }

        ChargeBillDTO dto = new ChargeBillDTO();
        dto.setStudentId(account.getStudentId());
        dto.setTotalAmount(totalPay);
        dto.setPayAmount(payAmount);
        dto.setDeductionAmount(deduction);
        dto.setBizDate(bizDatePicker.getValue() != null ? bizDatePicker.getValue().toString() : null);

        StudentParent payer = payerCombo.getValue();
        if (payer != null) {
            dto.setPayUserId(String.valueOf(payer.getId()));
            dto.setPayUserName(payer.getName());
        }

        dto.setSubjectItems(items);
        dto.setPayChancels(chancelAmounts);
        dto.setRemark(remarkArea.getText().trim());

        financeService.charge(dto).whenComplete((result, error) -> {
            Platform.runLater(() -> {
                if (error != null) {
                    showAlert("收费失败: " + error.getMessage());
                } else {
                    close();
                    if (onSuccess != null) onSuccess.run();
                }
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

    private HBox createFormRow(String label, javafx.scene.Node input) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        Label labelNode = new Label(label + ":");
        labelNode.setPrefWidth(80);
        labelNode.setStyle("-fx-text-fill: #333; -fx-font-size: 13px;");
        if (input instanceof TextField tf) tf.setPrefWidth(300);
        else if (input instanceof ComboBox cb) cb.setPrefWidth(300);
        else if (input instanceof DatePicker dp) dp.setPrefWidth(300);
        else if (input instanceof TextArea ta) { ta.setPrefWidth(300); ta.setPrefRowCount(2); }
        row.getChildren().addAll(labelNode, input);
        return row;
    }

    private static class SubjectItemRow extends HBox {
        private final CheckBox selected = new CheckBox();
        private final Spinner<Integer> periodSpinner = new Spinner<>(1, 99, 1);
        private final Label amountLabel = new Label();
        private final SubjectFeeSale sale;
        private final Long feeScaleId;

        public SubjectItemRow(SubjectFeeSale sale) {
            super(8);
            this.sale = sale;
            this.feeScaleId = sale.getId();
            setAlignment(Pos.CENTER_LEFT);

            selected.setText(sale.getSubjectName());

            String unitLabel = sale.getUnit() != null ? sale.getUnit() : "";
            double unitAmount = sale.getAmount() != null ? sale.getAmount().doubleValue() : 0;

            periodSpinner.setPrefWidth(70);
            periodSpinner.valueProperty().addListener((obs, old, val) -> updateAmount(unitAmount));

            updateAmount(unitAmount);

            Label unitLabelNode = new Label(unitLabel);
            unitLabelNode.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");

            if (sale.getLastDate() != null) {
                Label lastDateLabel = new Label("到期: " + sale.getLastDate());
                lastDateLabel.setStyle("-fx-text-fill: #999; -fx-font-size: 11px;");
                getChildren().addAll(selected, periodSpinner, unitLabelNode, amountLabel, lastDateLabel);
            } else {
                getChildren().addAll(selected, periodSpinner, unitLabelNode, amountLabel);
            }
        }

        private void updateAmount(double unitAmount) {
            double total = unitAmount * periodSpinner.getValue();
            amountLabel.setText(String.format("¥%.2f", total));
            amountLabel.setStyle("-fx-font-weight: 500;");
        }

        public boolean isSelected() { return selected.isSelected(); }
        public BooleanProperty selectedProperty() { return selected.selectedProperty(); }
        public int getPeriod() { return periodSpinner.getValue(); }
        public javafx.beans.property.ReadOnlyObjectProperty<Integer> periodProperty() { return periodSpinner.valueProperty(); }
        public double getCalculatedAmount() {
            double unitAmount = sale.getAmount() != null ? sale.getAmount().doubleValue() : 0;
            return unitAmount * periodSpinner.getValue();
        }
        public Long getFeeScaleId() { return feeScaleId; }
    }
}
