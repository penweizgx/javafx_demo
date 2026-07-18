package com.example.app.component;

import atlantafx.base.theme.Styles;
import com.example.app.AppContext;
import com.example.app.model.SubjectDTO;
import com.example.app.model.SubjectWithFeeScaleDTO;
import com.example.app.service.DialogService;
import com.example.app.service.FinanceManageService;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class SubjectAddModal extends VBox {

    private final Runnable onSuccess;
    private final FinanceManageService financeService;
    private final SubjectWithFeeScaleDTO existingSubject;

    private final TextField nameInput = new TextField();
    private final RadioButton periodRadio = new RadioButton("周期");
    private final RadioButton onceRadio = new RadioButton("一次性");
    private final Spinner<Integer> daySpinner = new Spinner<>(0, 365, 0);
    private final TextArea remarkInput = new TextArea();

    public SubjectAddModal(Runnable onSuccess) {
        this(null, onSuccess);
    }

    public SubjectAddModal(SubjectWithFeeScaleDTO existingSubject, Runnable onSuccess) {
        this.existingSubject = existingSubject;
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

        boolean isEdit = existingSubject != null;
        Label title = new Label(isEdit ? "编辑项目" : "新增项目");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        VBox form = new VBox(12);
        form.setPadding(new Insets(16));
        form.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 8;");

        nameInput.setPrefWidth(300);
        if (isEdit) {
            nameInput.setText(existingSubject.getName() != null ? existingSubject.getName() : "");
        }

        ToggleGroup typeGroup = new ToggleGroup();
        periodRadio.setToggleGroup(typeGroup);
        onceRadio.setToggleGroup(typeGroup);
        if (isEdit) {
            if ("ONCE".equals(existingSubject.getType())) {
                onceRadio.setSelected(true);
            } else {
                periodRadio.setSelected(true);
            }
            periodRadio.setDisable(true);
            onceRadio.setDisable(true);
        } else {
            periodRadio.setSelected(true);
        }

        HBox typeBox = new HBox(16);
        typeBox.setAlignment(Pos.CENTER_LEFT);
        typeBox.getChildren().addAll(periodRadio, onceRadio);

        daySpinner.setPrefWidth(300);
        daySpinner.setEditable(true);
        if (isEdit && existingSubject.getDay() != null) {
            daySpinner.getValueFactory().setValue(existingSubject.getDay());
        }

        remarkInput.setPrefWidth(300);
        remarkInput.setPrefRowCount(3);
        if (isEdit && existingSubject.getRemark() != null) {
            remarkInput.setText(existingSubject.getRemark());
        }

        form.getChildren().addAll(
            createFormRow("名称 *", nameInput),
            createFormRow("类型", typeBox),
            createFormRow("提醒天数", daySpinner),
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
        String name = nameInput.getText().trim();
        if (name.isEmpty()) {
            showAlert("请输入项目名称");
            return;
        }

        String type = onceRadio.isSelected() ? "ONCE" : "PERIOD";
        Integer day = daySpinner.getValue();

        SubjectDTO dto = new SubjectDTO();
        dto.setName(name);
        dto.setType(type);
        dto.setDay(day);
        dto.setRemark(remarkInput.getText().trim());

        if (existingSubject != null) {
            dto.setId(existingSubject.getId());
            dto.setAlloc(existingSubject.getAlloc());
            dto.setDisabled(existingSubject.getDisabled());
            dto.setRefund(existingSubject.getRefund());
            financeService.editSubject(dto).whenComplete((v, error) -> {
                Platform.runLater(() -> {
                    if (error != null) {
                        showAlert("保存失败: " + error.getMessage());
                    } else {
                        close();
                        if (onSuccess != null) onSuccess.run();
                    }
                });
            });
        } else {
            financeService.addSubject(dto).whenComplete((v, error) -> {
                Platform.runLater(() -> {
                    if (error != null) {
                        showAlert("保存失败: " + error.getMessage());
                    } else {
                        close();
                        if (onSuccess != null) onSuccess.run();
                    }
                });
            });
        }
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
