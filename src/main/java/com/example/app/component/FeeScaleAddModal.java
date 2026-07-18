package com.example.app.component;

import atlantafx.base.theme.Styles;
import com.example.app.AppContext;
import com.example.app.model.PeriodUnit;
import com.example.app.service.DialogService;
import com.example.app.service.FinanceManageService;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class FeeScaleAddModal extends VBox {

    private final Long subjectId;
    private final Runnable onSuccess;
    private final FinanceManageService financeService;

    private final TextField standardAmountInput = new TextField();
    private final ComboBox<String> unitCombo = new ComboBox<>();
    private final TextField nameInput = new TextField();
    private final TextArea remarkInput = new TextArea();

    public FeeScaleAddModal(Long subjectId, Runnable onSuccess) {
        this.subjectId = subjectId;
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

        Label title = new Label("新增收费标准");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        VBox form = new VBox(12);
        form.setPadding(new Insets(16));
        form.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 8;");

        standardAmountInput.setPrefWidth(300);

        for (PeriodUnit pu : PeriodUnit.values()) {
            unitCombo.getItems().add(pu.name());
        }
        unitCombo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("");
                } else {
                    PeriodUnit pu = PeriodUnit.fromString(item);
                    setText(pu != null ? pu.getLabel() + " (" + pu.name() + ")" : item);
                }
            }
        });
        unitCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("");
                } else {
                    PeriodUnit pu = PeriodUnit.fromString(item);
                    setText(pu != null ? pu.getLabel() + " (" + pu.name() + ")" : item);
                }
            }
        });
        unitCombo.setPrefWidth(300);
        unitCombo.setValue(PeriodUnit.M.name());

        nameInput.setPrefWidth(300);
        remarkInput.setPrefWidth(300);
        remarkInput.setPrefRowCount(3);

        form.getChildren().addAll(
            createFormRow("标准金额 *", standardAmountInput),
            createFormRow("周期单位", unitCombo),
            createFormRow("名称", nameInput),
            createFormRow("备注", remarkInput)
        );

        HBox buttonBox = new HBox(12);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        Button cancelBtn = new Button("取消");
        cancelBtn.getStyleClass().addAll(Styles.BUTTON_OUTLINED);
        cancelBtn.setOnAction(e -> close());

        Button saveBtn = new Button("保存");
        saveBtn.getStyleClass().addAll(Styles.ACCENT);
        saveBtn.setOnAction(e -> handleSave());

        buttonBox.getChildren().addAll(cancelBtn, saveBtn);

        getChildren().addAll(title, form, buttonBox);
    }

    private void handleSave() {
        String amountText = standardAmountInput.getText().trim();
        if (amountText.isEmpty()) {
            showAlert("请输入标准金额");
            return;
        }

        Number amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            showAlert("请输入有效金额");
            return;
        }

        String unit = unitCombo.getValue();
        if (unit == null || unit.isEmpty()) {
            showAlert("请选择周期单位");
            return;
        }

        String name = nameInput.getText().trim();
        String remark = remarkInput.getText().trim();

        financeService.addFeeScale(subjectId, amount, unit).whenComplete((v, error) -> {
            Platform.runLater(() -> {
                if (error != null) {
                    showAlert("保存失败: " + error.getMessage());
                } else {
                    if (!name.isEmpty()) {
                        loadAndRename(name);
                    } else {
                        close();
                        if (onSuccess != null) onSuccess.run();
                    }
                }
            });
        });
    }

    private void loadAndRename(String name) {
        financeService.listSubjectWithFeeScale().whenComplete((subjects, error) -> {
            Platform.runLater(() -> {
                if (error != null || subjects == null) {
                    close();
                    if (onSuccess != null) onSuccess.run();
                    return;
                }
                for (var subject : subjects) {
                    if (subject.getId().equals(subjectId) && subject.getFeeScales() != null) {
                        for (var fs : subject.getFeeScales()) {
                            if (fs.getName() != null && fs.getName().startsWith("标准")) {
                                financeService.changeFeeScaleName(fs.getId(), name).whenComplete((v2, e2) -> {
                                    Platform.runLater(() -> {
                                        close();
                                        if (onSuccess != null) onSuccess.run();
                                    });
                                });
                                return;
                            }
                        }
                    }
                }
                close();
                if (onSuccess != null) onSuccess.run();
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
