package com.example.app.controller;

import atlantafx.base.theme.Styles;
import com.example.app.AppContext;
import com.example.app.component.FeeScaleAddModal;
import com.example.app.component.RefundSettingModal;
import com.example.app.component.SubjectAddModal;
import com.example.app.i18n.I18nService;
import com.example.app.i18n.LocaleChangeEvent;
import com.example.app.model.FeeScaleDTO;
import com.example.app.model.PeriodUnit;
import com.example.app.model.SubjectWithFeeScaleDTO;
import com.example.app.navigation.EventBus;
import com.example.app.service.FinanceManageService;
import com.example.app.viewmodel.FinanceSubjectViewModel;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public class FinanceSubjectController {

    @FXML private VBox root;
    @FXML private Label titleLabel;
    @FXML private Button addSubjectBtn;
    @FXML private ToggleGroup typeFilterGroup;
    @FXML private RadioButton allRadio;
    @FXML private RadioButton periodRadio;
    @FXML private RadioButton onceRadio;
    @FXML private TableView<SubjectWithFeeScaleDTO> subjectTable;
    @FXML private ProgressIndicator loadingIndicator;
    @FXML private Label errorLabel;

    private FinanceSubjectViewModel viewModel;
    private I18nService i18n;
    private FinanceManageService financeService;

    private final Consumer<LocaleChangeEvent> localeHandler = e -> refreshI18n();

    @FXML
    public void initialize() {
        financeService = AppContext.get().getService(FinanceManageService.class);
        this.viewModel = new FinanceSubjectViewModel(financeService);
        try {
            this.i18n = AppContext.get().getService(I18nService.class);
        } catch (Exception ignored) {}

        setupTable();
        setupBindings();
        setupEventHandlers();
        subscribeEvents();

        viewModel.loadSubjects();
    }

    private void setupTable() {
        TableColumn<SubjectWithFeeScaleDTO, Void> expandCol = new TableColumn<>("");
        expandCol.setPrefWidth(40);
        expandCol.setCellFactory(col -> new TableCell<>() {
            private final Button expandBtn = new Button();
            {
                expandBtn.getStyleClass().addAll(Styles.BUTTON_OUTLINED, Styles.SMALL);
                expandBtn.setPrefWidth(28);
                expandBtn.setOnAction(e -> toggleExpand());
            }

            private void toggleExpand() {
                SubjectWithFeeScaleDTO subject = getTableView().getItems().get(getIndex());
                TableRow<SubjectWithFeeScaleDTO> row = getTableRow();
                if (row == null) return;

                if (row.getGraphic() != null) {
                    row.setGraphic(null);
                    expandBtn.setText("+");
                } else {
                    VBox detailBox = createFeeScaleDetail(subject);
                    row.setGraphic(detailBox);
                    expandBtn.setText("-");
                }
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    expandBtn.setText("+");
                    TableRow<SubjectWithFeeScaleDTO> row = getTableRow();
                    if (row != null && row.getGraphic() != null) {
                        expandBtn.setText("-");
                    }
                    setGraphic(expandBtn);
                }
            }
        });

        TableColumn<SubjectWithFeeScaleDTO, String> nameCol = new TableColumn<>("项目名称");
        nameCol.setUserData("subject.col.name");
        nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        nameCol.setPrefWidth(150);

        TableColumn<SubjectWithFeeScaleDTO, String> typeCol = new TableColumn<>("类型");
        typeCol.setUserData("subject.col.type");
        typeCol.setCellValueFactory(data -> new SimpleStringProperty(
            "PERIOD".equals(data.getValue().getType()) ? "周期" : "一次性"));
        typeCol.setPrefWidth(80);
        typeCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String val, boolean empty) {
                super.updateItem(val, empty);
                if (empty || val == null) {
                    setGraphic(null);
                } else {
                    Label badge = new Label(val);
                    badge.getStyleClass().addAll("badge",
                        "周期".equals(val) ? Styles.ACCENT : Styles.SUCCESS);
                    setGraphic(badge);
                }
            }
        });

        TableColumn<SubjectWithFeeScaleDTO, String> statusCol = new TableColumn<>("状态");
        statusCol.setUserData("subject.col.status");
        statusCol.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getDisabled() != null && data.getValue().getDisabled() ? "已停用" : "启用"));
        statusCol.setPrefWidth(80);
        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String val, boolean empty) {
                super.updateItem(val, empty);
                if (empty || val == null) {
                    setGraphic(null);
                } else {
                    Label badge = new Label(val);
                    badge.getStyleClass().addAll("badge",
                        "启用".equals(val) ? Styles.SUCCESS : Styles.DANGER);
                    setGraphic(badge);
                }
            }
        });

        TableColumn<SubjectWithFeeScaleDTO, String> dayCol = new TableColumn<>("提醒天数");
        dayCol.setUserData("subject.col.day");
        dayCol.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getDay() != null ? data.getValue().getDay() + "天" : "-"));
        dayCol.setPrefWidth(80);

        TableColumn<SubjectWithFeeScaleDTO, String> scaleCountCol = new TableColumn<>("标准数");
        scaleCountCol.setUserData("subject.col.scaleCount");
        scaleCountCol.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getFeeScales() != null ? data.getValue().getFeeScales().size() + "个" : "0个"));
        scaleCountCol.setPrefWidth(80);

        TableColumn<SubjectWithFeeScaleDTO, String> refundCol = new TableColumn<>("退费标准");
        refundCol.setUserData("subject.col.refund");
        refundCol.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getRefund() != null ? data.getValue().getRefund() : "-"));
        refundCol.setPrefWidth(100);

        TableColumn<SubjectWithFeeScaleDTO, Void> actionCol = new TableColumn<>("操作");
        actionCol.setUserData("subject.col.action");
        actionCol.setPrefWidth(300);
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("编辑");
            private final Button disableBtn = new Button("停用");
            private final Button refundBtn = new Button("退费设置");
            private final Button addScaleBtn = new Button("新增标准");
            {
                editBtn.getStyleClass().addAll(Styles.BUTTON_OUTLINED, Styles.SMALL);
                disableBtn.getStyleClass().addAll(Styles.DANGER, Styles.SMALL);
                refundBtn.getStyleClass().addAll(Styles.BUTTON_OUTLINED, Styles.SMALL);
                addScaleBtn.getStyleClass().addAll(Styles.ACCENT, Styles.SMALL);

                editBtn.setOnAction(e -> handleEdit(getTableView().getItems().get(getIndex())));
                disableBtn.setOnAction(e -> handleDisable(getTableView().getItems().get(getIndex())));
                refundBtn.setOnAction(e -> handleRefundSetting(getTableView().getItems().get(getIndex())));
                addScaleBtn.setOnAction(e -> handleAddFeeScale(getTableView().getItems().get(getIndex())));
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    SubjectWithFeeScaleDTO subject = getTableView().getItems().get(getIndex());
                    HBox box = new HBox(6);
                    box.setAlignment(Pos.CENTER_LEFT);
                    if (subject.getDisabled() != null && subject.getDisabled()) {
                        disableBtn.setText("启用");
                        disableBtn.getStyleClass().setAll(Styles.SUCCESS, Styles.SMALL);
                    } else {
                        disableBtn.setText("停用");
                        disableBtn.getStyleClass().setAll(Styles.DANGER, Styles.SMALL);
                    }
                    box.getChildren().addAll(editBtn, disableBtn, refundBtn, addScaleBtn);
                    setGraphic(box);
                }
            }
        });

        subjectTable.getColumns().addAll(expandCol, nameCol, typeCol, statusCol, dayCol, scaleCountCol, refundCol, actionCol);
    }

    private VBox createFeeScaleDetail(SubjectWithFeeScaleDTO subject) {
        VBox box = new VBox(8);
        box.setPadding(new Insets(8, 0, 8, 40));

        Label feeTitle = new Label("收费标准");
        feeTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        box.getChildren().add(feeTitle);

        if (subject.getFeeScales() == null || subject.getFeeScales().isEmpty()) {
            box.getChildren().add(new Label("暂无收费标准"));
            return box;
        }

        TableView<FeeScaleDTO> feeTable = new TableView<>();
        feeTable.setItems(FXCollections.observableArrayList(subject.getFeeScales()));
        feeTable.setPrefHeight(Math.min(120, subject.getFeeScales().size() * 32 + 40));
        feeTable.setMaxHeight(200);

        TableColumn<FeeScaleDTO, String> fNameCol = new TableColumn<>("名称");
        fNameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        fNameCol.setPrefWidth(120);

        TableColumn<FeeScaleDTO, String> fAmountCol = new TableColumn<>("标准金额");
        fAmountCol.setCellValueFactory(data -> {
            FeeScaleDTO fs = data.getValue();
            String unitLabel = "";
            if (fs.getUnit() != null) {
                PeriodUnit pu = PeriodUnit.fromString(fs.getUnit());
                unitLabel = pu != null ? "/" + pu.getLabel() : "/" + fs.getUnit();
            }
            return new SimpleStringProperty(
                fs.getStandardAmount() != null ? "¥" + fs.getStandardAmount() + unitLabel : "-");
        });
        fAmountCol.setPrefWidth(150);

        TableColumn<FeeScaleDTO, String> fUsedCol = new TableColumn<>("使用次数");
        fUsedCol.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getUsed() != null ? String.valueOf(data.getValue().getUsed()) : "0"));
        fUsedCol.setPrefWidth(80);

        TableColumn<FeeScaleDTO, String> fRemarkCol = new TableColumn<>("备注");
        fRemarkCol.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getRemark() != null ? data.getValue().getRemark() : ""));
        fRemarkCol.setPrefWidth(120);

        TableColumn<FeeScaleDTO, Void> fActionCol = new TableColumn<>("操作");
        fActionCol.setPrefWidth(150);
        fActionCol.setCellFactory(col -> new TableCell<>() {
            private final Button renameBtn = new Button("重命名");
            private final Button disableBtn = new Button("停用");
            {
                renameBtn.getStyleClass().addAll(Styles.BUTTON_OUTLINED, Styles.SMALL);
                disableBtn.getStyleClass().addAll(Styles.DANGER, Styles.SMALL);
                renameBtn.setOnAction(e -> handleRenameFeeScale(getTableView().getItems().get(getIndex())));
                disableBtn.setOnAction(e -> handleDisableFeeScale(subject, getTableView().getItems().get(getIndex())));
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    FeeScaleDTO fs = getTableView().getItems().get(getIndex());
                    HBox hBox = new HBox(6);
                    hBox.setAlignment(Pos.CENTER_LEFT);
                    if (fs.getDisabled() != null && fs.getDisabled()) {
                        disableBtn.setText("启用");
                        disableBtn.getStyleClass().setAll(Styles.SUCCESS, Styles.SMALL);
                    } else {
                        disableBtn.setText("停用");
                        disableBtn.getStyleClass().setAll(Styles.DANGER, Styles.SMALL);
                    }
                    hBox.getChildren().addAll(renameBtn, disableBtn);
                    setGraphic(hBox);
                }
            }
        });

        feeTable.getColumns().addAll(fNameCol, fAmountCol, fUsedCol, fRemarkCol, fActionCol);
        box.getChildren().add(feeTable);

        return box;
    }

    private void setupBindings() {
        subjectTable.setItems(viewModel.getSubjectList());
        loadingIndicator.visibleProperty().bind(viewModel.loadingProperty());
        errorLabel.textProperty().bind(viewModel.errorMessageProperty());
        errorLabel.visibleProperty().bind(Bindings.isNotEmpty(viewModel.errorMessageProperty()));
    }

    private void setupEventHandlers() {
        addSubjectBtn.setOnAction(e -> openAddSubjectModal());

        typeFilterGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == allRadio) {
                viewModel.filterByType("ALL");
            } else if (newVal == periodRadio) {
                viewModel.filterByType("PERIOD");
            } else if (newVal == onceRadio) {
                viewModel.filterByType("ONCE");
            }
        });
    }

    private void openAddSubjectModal() {
        new SubjectAddModal(() -> viewModel.refresh()).show();
    }

    private void handleEdit(SubjectWithFeeScaleDTO subject) {
        new SubjectAddModal(subject, () -> viewModel.refresh()).show();
    }

    private void handleDisable(SubjectWithFeeScaleDTO subject) {
        if (subject.getDisabled() != null && subject.getDisabled()) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("确认启用");
            confirm.setHeaderText(null);
            confirm.setContentText("确定要启用项目「" + subject.getName() + "」吗？");
            confirm.showAndWait().ifPresent(btn -> {
                if (btn == ButtonType.OK) {
                    financeService.disableSubject(subject.getId()).whenComplete((v, error) -> {
                        if (error != null) {
                            showAlert("操作失败: " + error.getMessage());
                        } else {
                            viewModel.refresh();
                        }
                    });
                }
            });
        } else {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("确认停用");
            confirm.setHeaderText(null);
            confirm.setContentText("确定要停用项目「" + subject.getName() + "」吗？");
            confirm.showAndWait().ifPresent(btn -> {
                if (btn == ButtonType.OK) {
                    financeService.disableSubject(subject.getId()).whenComplete((v, error) -> {
                        if (error != null) {
                            showAlert("操作失败: " + error.getMessage());
                        } else {
                            viewModel.refresh();
                        }
                    });
                }
            });
        }
    }

    private void handleRefundSetting(SubjectWithFeeScaleDTO subject) {
        new RefundSettingModal(subject, () -> viewModel.refresh()).show();
    }

    private void handleAddFeeScale(SubjectWithFeeScaleDTO subject) {
        new FeeScaleAddModal(subject.getId(), () -> viewModel.refresh()).show();
    }

    private void handleRenameFeeScale(FeeScaleDTO feeScale) {
        TextInputDialog dialog = new TextInputDialog(feeScale.getName());
        dialog.setTitle("重命名标准");
        dialog.setHeaderText(null);
        dialog.setContentText("新名称:");
        dialog.showAndWait().ifPresent(newName -> {
            if (!newName.trim().isEmpty()) {
                financeService.changeFeeScaleName(feeScale.getId(), newName.trim()).whenComplete((v, error) -> {
                    if (error != null) {
                        showAlert("重命名失败: " + error.getMessage());
                    } else {
                        viewModel.refresh();
                    }
                });
            }
        });
    }

    private void handleDisableFeeScale(SubjectWithFeeScaleDTO subject, FeeScaleDTO feeScale) {
        String action = feeScale.getDisabled() != null && feeScale.getDisabled() ? "启用" : "停用";
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("确认" + action);
        confirm.setHeaderText(null);
        confirm.setContentText("确定要" + action + "标准「" + feeScale.getName() + "」吗？");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                financeService.disableFeeScale(feeScale.getId()).whenComplete((v, error) -> {
                    if (error != null) {
                        showAlert("操作失败: " + error.getMessage());
                    } else {
                        viewModel.refresh();
                    }
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

    private void subscribeEvents() {
        EventBus.getInstance().subscribe(LocaleChangeEvent.class, localeHandler);
    }

    private void refreshI18n() {
        if (i18n == null) return;
        titleLabel.setText(i18n.getString("finance.subject.title"));
        addSubjectBtn.setText(i18n.getString("subject.add"));
        allRadio.setText(i18n.getString("subject.filter.all"));
        periodRadio.setText(i18n.getString("subject.filter.period"));
        onceRadio.setText(i18n.getString("subject.filter.once"));

        for (TableColumn<SubjectWithFeeScaleDTO, ?> col : subjectTable.getColumns()) {
            String colId = (String) col.getUserData();
            if (colId != null) col.setText(i18n.getString(colId));
        }
    }

    public void cleanup() {
        EventBus.getInstance().unsubscribe(LocaleChangeEvent.class, localeHandler);
    }
}
