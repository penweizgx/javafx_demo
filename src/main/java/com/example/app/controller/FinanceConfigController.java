package com.example.app.controller;

import atlantafx.base.theme.Styles;
import com.example.app.AppContext;
import com.example.app.i18n.I18nService;
import com.example.app.i18n.LocaleChangeEvent;
import com.example.app.model.BillNumberRule;
import com.example.app.model.FinanceConfig;
import com.example.app.model.PayChancel;
import com.example.app.navigation.EventBus;
import com.example.app.service.FinanceManageService;
import com.example.app.viewmodel.FinanceConfigViewModel;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Map;
import java.util.function.Consumer;

public class FinanceConfigController {

    @FXML private VBox root;
    @FXML private Label titleLabel;
    @FXML private ProgressIndicator loadingIndicator;
    @FXML private Label errorLabel;
    @FXML private Button addPayChancelBtn;
    @FXML private TableView<PayChancel> payChancelTable;
    @FXML private Spinner<Integer> lengthSpinner;
    @FXML private TextField prefixField;
    @FXML private TextField startField;
    @FXML private TextField serialnoField;
    @FXML private Button saveBillRuleBtn;
    @FXML private Spinner<Integer> remindSpinner;
    @FXML private Button saveRemindBtn;
    @FXML private Spinner<Integer> continuousSpinner;
    @FXML private Button saveContinuousBtn;
    @FXML private Button validateBtn;

    private FinanceConfigViewModel viewModel;
    private I18nService i18n;
    private final Consumer<LocaleChangeEvent> localeHandler = e -> refreshI18n();

    @FXML
    public void initialize() {
        FinanceManageService financeService = AppContext.get().getService(FinanceManageService.class);
        this.viewModel = new FinanceConfigViewModel(financeService);
        try {
            this.i18n = AppContext.get().getService(I18nService.class);
        } catch (Exception ignored) {}

        setupPayChancelTable();
        setupSpinners();
        setupBindings();
        setupEventHandlers();
        subscribeEvents();

        viewModel.loadConfig();
        viewModel.loadBillNumberRule();
        viewModel.loadPayChancels();
    }

    private void setupPayChancelTable() {
        TableColumn<PayChancel, String> nameCol = new TableColumn<>("名称");
        nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        nameCol.setPrefWidth(200);

        TableColumn<PayChancel, String> hideCol = new TableColumn<>("隐藏");
        hideCol.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getHide() != null && data.getValue().getHide() ? "是" : "否"));
        hideCol.setPrefWidth(80);
        hideCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String val, boolean empty) {
                super.updateItem(val, empty);
                if (empty || val == null) {
                    setGraphic(null);
                } else {
                    Label badge = new Label(val);
                    badge.getStyleClass().addAll("badge", "是".equals(val) ? Styles.WARNING : Styles.SUCCESS);
                    setGraphic(badge);
                }
            }
        });

        TableColumn<PayChancel, Void> actionCol = new TableColumn<>("操作");
        actionCol.setPrefWidth(100);
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button removeBtn = new Button("删除");
            {
                removeBtn.getStyleClass().addAll(Styles.DANGER, Styles.SMALL);
                removeBtn.setOnAction(e -> {
                    PayChancel item = getTableView().getItems().get(getIndex());
                    handleRemovePayChancel(item);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : removeBtn);
            }
        });

        payChancelTable.getColumns().addAll(nameCol, hideCol, actionCol);
    }

    private void setupSpinners() {
        lengthSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 30, 6));
        remindSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 365, 7));
        continuousSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 365, 3));
        lengthSpinner.setEditable(true);
        remindSpinner.setEditable(true);
        continuousSpinner.setEditable(true);
    }

    private void setupBindings() {
        payChancelTable.setItems(viewModel.getPayChancels());
        loadingIndicator.visibleProperty().bind(viewModel.loadingProperty());
        errorLabel.textProperty().bind(viewModel.errorMessageProperty());
        errorLabel.visibleProperty().bind(Bindings.isNotEmpty(viewModel.errorMessageProperty()));

        viewModel.configProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                if (newVal.getRemind() != null) remindSpinner.getValueFactory().setValue(newVal.getRemind());
                if (newVal.getContinuous() != null) continuousSpinner.getValueFactory().setValue(newVal.getContinuous());
            }
        });

        viewModel.billNumberRuleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                if (newVal.getLength() != null) lengthSpinner.getValueFactory().setValue(newVal.getLength());
                prefixField.setText(newVal.getPrefix() != null ? newVal.getPrefix() : "");
                startField.setText(newVal.getStart() != null ? newVal.getStart() : "");
                serialnoField.setText(newVal.getSerialno() != null ? String.valueOf(newVal.getSerialno()) : "");
            }
        });
    }

    private void setupEventHandlers() {
        addPayChancelBtn.setOnAction(e -> handleAddPayChancel());
        saveBillRuleBtn.setOnAction(e -> handleSaveBillRule());
        saveRemindBtn.setOnAction(e -> handleSaveRemind());
        saveContinuousBtn.setOnAction(e -> handleSaveContinuous());
        validateBtn.setOnAction(e -> handleValidate());
    }

    private void handleAddPayChancel() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("新增收款方式");
        dialog.setHeaderText(null);
        dialog.setContentText("名称:");
        dialog.showAndWait().ifPresent(name -> {
            if (!name.trim().isEmpty()) {
                viewModel.addPayChancel(name.trim());
            }
        });
    }

    private void handleRemovePayChancel(PayChancel item) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("确认删除");
        confirm.setHeaderText(null);
        confirm.setContentText("确定要删除收款方式「" + item.getName() + "」吗？");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                viewModel.removePayChancel(item.getId());
            }
        });
    }

    private void handleSaveBillRule() {
        BillNumberRule rule = viewModel.getBillNumberRule().get();
        if (rule == null) rule = new BillNumberRule();
        rule.setLength(lengthSpinner.getValue());
        rule.setPrefix(prefixField.getText());
        rule.setStart(startField.getText());
        try {
            rule.setSerialno(Long.parseLong(serialnoField.getText()));
        } catch (NumberFormatException ex) {
            rule.setSerialno(0L);
        }
        viewModel.billNumberRuleProperty().set(rule);
        viewModel.saveBillNumberRule();
        showSuccess("账单编号规则已保存");
    }

    private void handleSaveRemind() {
        FinanceConfig config = viewModel.getConfig().get();
        if (config == null) config = new FinanceConfig();
        config.setRemind(remindSpinner.getValue());
        viewModel.configProperty().set(config);
        viewModel.saveConfig();
        showSuccess("提醒天数已保存");
    }

    private void handleSaveContinuous() {
        FinanceConfig config = viewModel.getConfig().get();
        if (config == null) config = new FinanceConfig();
        config.setContinuous(continuousSpinner.getValue());
        viewModel.configProperty().set(config);
        viewModel.saveConfig();
        showSuccess("考勤退费阈值已保存");
    }

    private void handleValidate() {
        viewModel.validate(result -> {
            if (result == null || result.isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "校验通过", "所有配置校验通过，无异常。");
            } else {
                StringBuilder sb = new StringBuilder();
                result.forEach((key, value) -> sb.append(key).append(": ").append(value).append("\n"));
                showAlert(Alert.AlertType.WARNING, "校验结果", sb.toString());
            }
        });
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("成功");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void subscribeEvents() {
        EventBus.getInstance().subscribe(LocaleChangeEvent.class, localeHandler);
    }

    private void refreshI18n() {
        if (i18n == null) return;
        titleLabel.setText(i18n.getString("finance.config.title"));
    }

    public void cleanup() {
        EventBus.getInstance().unsubscribe(LocaleChangeEvent.class, localeHandler);
    }
}
