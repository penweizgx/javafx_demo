package com.example.app.component;

import atlantafx.base.theme.Styles;
import com.example.app.AppContext;
import com.example.app.model.CarryOverConfig;
import com.example.app.model.SubjectWithFeeScaleDTO;
import com.example.app.service.DialogService;
import com.example.app.service.FinanceManageService;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class RefundSettingModal extends VBox {

    private final SubjectWithFeeScaleDTO subject;
    private final Runnable onSuccess;
    private final FinanceManageService financeService;

    private final TextField amountInput = new TextField();
    private final Label unitLabel = new Label();
    private final ProgressIndicator loading = new ProgressIndicator();

    public RefundSettingModal(SubjectWithFeeScaleDTO subject, Runnable onSuccess) {
        this.subject = subject;
        this.onSuccess = onSuccess;
        this.financeService = AppContext.get().getService(FinanceManageService.class);
        initialize();
    }

    private void initialize() {
        setSpacing(16);
        setPadding(new Insets(24));
        setAlignment(Pos.TOP_CENTER);
        setMaxWidth(480);
        setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                 "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 20, 0, 0, 5);");

        Label title = new Label("退费设置 - " + subject.getName());
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        VBox form = new VBox(12);
        form.setPadding(new Insets(16));
        form.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 8;");

        amountInput.setPrefWidth(240);
        unitLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #666;");

        HBox amountBox = new HBox(8);
        amountBox.setAlignment(Pos.CENTER_LEFT);
        amountBox.getChildren().addAll(amountInput, unitLabel);

        form.getChildren().addAll(
            createFormRow("退费金额", amountBox)
        );

        loading.setMaxSize(30, 30);
        loading.setVisible(false);

        HBox buttonBox = new HBox(12);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        Button cancelBtn = new Button("取消");
        cancelBtn.getStyleClass().addAll(Styles.BUTTON_OUTLINED);
        cancelBtn.setOnAction(e -> close());

        Button saveBtn = new Button("保存");
        saveBtn.getStyleClass().addAll(Styles.ACCENT);
        saveBtn.setOnAction(e -> handleSave());

        buttonBox.getChildren().addAll(cancelBtn, saveBtn);

        getChildren().addAll(title, form, loading, buttonBox);

        loadConfig();
    }

    private void loadConfig() {
        loading.setVisible(true);
        financeService.attendCarryOverConfig(subject.getId()).whenComplete((config, error) -> {
            Platform.runLater(() -> {
                loading.setVisible(false);
                if (error != null) {
                    unitLabel.setText("/天");
                } else if (config != null) {
                    if (config.getAmount() != null) {
                        amountInput.setText(String.valueOf(config.getAmount()));
                    }
                    if (config.getUnit() != null) {
                        unitLabel.setText("/" + config.getUnit());
                    } else {
                        unitLabel.setText("/天");
                    }
                } else {
                    unitLabel.setText("/天");
                }
            });
        });
    }

    private void handleSave() {
        String amountText = amountInput.getText().trim();
        if (amountText.isEmpty()) {
            showAlert("请输入退费金额");
            return;
        }

        Number amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            showAlert("请输入有效金额");
            return;
        }

        loading.setVisible(true);
        financeService.setAttendCarryOverConfig(subject.getId(), amount).whenComplete((v, error) -> {
            Platform.runLater(() -> {
                loading.setVisible(false);
                if (error != null) {
                    showAlert("保存失败: " + error.getMessage());
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

    private HBox createFormRow(String label, javafx.scene.Node input) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);

        Label labelNode = new Label(label);
        labelNode.setPrefWidth(100);
        labelNode.setStyle("-fx-text-fill: #333; -fx-font-size: 13px;");

        row.getChildren().addAll(labelNode, input);
        return row;
    }
}
