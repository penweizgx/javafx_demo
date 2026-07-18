package com.example.app.controller;

import atlantafx.base.theme.Styles;
import com.example.app.AppContext;
import com.example.app.i18n.I18nService;
import com.example.app.i18n.LocaleChangeEvent;
import com.example.app.model.CarryItem;
import com.example.app.model.ClazzOptionVO;
import com.example.app.model.MonthAttendCarryOverVO;
import com.example.app.navigation.EventBus;
import com.example.app.service.FinanceManageService;
import com.example.app.service.StudentManageService;
import com.example.app.viewmodel.FinanceAttendViewModel;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class FinanceAttendController {

    @FXML private VBox root;
    @FXML private Label titleLabel;
    @FXML private TextField monthField;
    @FXML private ComboBox<ClazzOptionVO> clazzCombo;
    @FXML private CheckBox onlyRefundCheck;
    @FXML private Button queryBtn;
    @FXML private Button confirmBtn;
    @FXML private TableView<MonthAttendCarryOverVO> refundTable;
    @FXML private ProgressIndicator loadingIndicator;
    @FXML private Label errorLabel;
    @FXML private Label confirmLabel;
    @FXML private Label totalLabel;

    private FinanceAttendViewModel viewModel;
    private I18nService i18n;

    private final Consumer<LocaleChangeEvent> localeHandler = e -> refreshI18n();

    @FXML
    public void initialize() {
        FinanceManageService financeService = AppContext.get().getService(FinanceManageService.class);
        StudentManageService studentService = AppContext.get().getService(StudentManageService.class);
        this.viewModel = new FinanceAttendViewModel(financeService, studentService);
        try {
            this.i18n = AppContext.get().getService(I18nService.class);
        } catch (Exception ignored) {}

        setupTable();
        setupClazzCombo();
        setupBindings();
        setupEventHandlers();
        subscribeEvents();

        viewModel.loadClazzOptions();
    }

    private void setupClazzCombo() {
        clazzCombo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(ClazzOptionVO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName());
            }
        });
        clazzCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(ClazzOptionVO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName());
            }
        });
        clazzCombo.setItems(viewModel.getClazzOptions());
    }

    private void setupTable() {
        TableColumn<MonthAttendCarryOverVO, String> studentCol = new TableColumn<>("学生");
        studentCol.setUserData("attend.col.student");
        studentCol.setCellValueFactory(data -> {
            MonthAttendCarryOverVO vo = data.getValue();
            String name = vo.getStudentName() != null ? vo.getStudentName() : "";
            String clazz = vo.getClazzName() != null ? vo.getClazzName() : "";
            return new SimpleStringProperty(name + (clazz.isEmpty() ? "" : " / " + clazz));
        });
        studentCol.setPrefWidth(150);

        TableColumn<MonthAttendCarryOverVO, String> missCol = new TableColumn<>("共休假");
        missCol.setUserData("attend.col.miss");
        missCol.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getMiss() != null ? String.valueOf(data.getValue().getMiss()) : "0"));
        missCol.setPrefWidth(80);

        TableColumn<MonthAttendCarryOverVO, String> leaveCol = new TableColumn<>("休假");
        leaveCol.setUserData("attend.col.leave");
        leaveCol.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getLeave() != null ? String.valueOf(data.getValue().getLeave()) : "0"));
        leaveCol.setPrefWidth(80);

        TableColumn<MonthAttendCarryOverVO, String> refundCol = new TableColumn<>("退费天数");
        refundCol.setUserData("attend.col.refund");
        refundCol.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getRefund() != null ? String.valueOf(data.getValue().getRefund()) : "0"));
        refundCol.setPrefWidth(80);
        refundCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String val, boolean empty) {
                super.updateItem(val, empty);
                if (empty || val == null) {
                    setGraphic(null);
                } else {
                    int refund = Integer.parseInt(val);
                    Label label = new Label(val);
                    if (refund > 0) {
                        label.getStyleClass().addAll("badge", Styles.WARNING);
                    }
                    setGraphic(label);
                }
            }
        });

        TableColumn<MonthAttendCarryOverVO, String> itemsCol = new TableColumn<>("退费明细");
        itemsCol.setUserData("attend.col.items");
        itemsCol.setCellValueFactory(data -> {
            MonthAttendCarryOverVO vo = data.getValue();
            if (vo.getItems() == null || vo.getItems().isEmpty()) return new SimpleStringProperty("-");
            return new SimpleStringProperty(vo.getItems().stream()
                .map(item -> {
                    String name = item.getSubjectName() != null ? item.getSubjectName() : "未知";
                    String amount = item.getRealAmount() != null ? "¥" + item.getRealAmount() : "¥0";
                    return name + ":" + amount;
                })
                .collect(Collectors.joining(", ")));
        });
        itemsCol.setPrefWidth(250);

        TableColumn<MonthAttendCarryOverVO, String> anomalyCol = new TableColumn<>("异常信息");
        anomalyCol.setUserData("attend.col.anomaly");
        anomalyCol.setCellValueFactory(data -> {
            MonthAttendCarryOverVO vo = data.getValue();
            if (vo.getItems() == null || vo.getItems().isEmpty()) return new SimpleStringProperty("");
            List<String> anomalies = new ArrayList<>();
            for (CarryItem item : vo.getItems()) {
                if (item.getDefalutAmount() != null && item.getRealAmount() != null) {
                    double def = item.getDefalutAmount().doubleValue();
                    double real = item.getRealAmount().doubleValue();
                    if (def != real && real != 0) {
                        anomalies.add((item.getSubjectName() != null ? item.getSubjectName() : "未知") +
                            " 默认¥" + def + " 实际¥" + real);
                    }
                }
            }
            return new SimpleStringProperty(String.join("; ", anomalies));
        });
        anomalyCol.setPrefWidth(200);
        anomalyCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String val, boolean empty) {
                super.updateItem(val, empty);
                if (empty || val == null || val.isEmpty()) {
                    setGraphic(null);
                } else {
                    Label badge = new Label("异常");
                    badge.getStyleClass().addAll("badge", Styles.DANGER);
                    Tooltip tooltip = new Tooltip(val);
                    badge.setTooltip(tooltip);
                    setGraphic(badge);
                }
            }
        });

        refundTable.getColumns().addAll(studentCol, missCol, leaveCol, refundCol, itemsCol, anomalyCol);
    }

    private void setupBindings() {
        refundTable.setItems(viewModel.getRefundList());
        loadingIndicator.visibleProperty().bind(viewModel.loadingProperty());
        errorLabel.textProperty().bind(viewModel.errorMessageProperty());
        errorLabel.visibleProperty().bind(Bindings.isNotEmpty(viewModel.errorMessageProperty()));
        confirmLabel.textProperty().bind(viewModel.confirmMessageProperty());
        confirmLabel.visibleProperty().bind(Bindings.isNotEmpty(viewModel.confirmMessageProperty()));

        viewModel.selectedClazzProperty().bind(clazzCombo.valueProperty());
        viewModel.selectedMonthProperty().bindBidirectional(monthField.textProperty());
        viewModel.onlyRefundProperty().bind(onlyRefundCheck.selectedProperty());

        totalLabel.textProperty().bind(Bindings.createStringBinding(
            () -> "共 " + viewModel.getRefundList().size() + " 条",
            viewModel.getRefundList()
        ));
    }

    private void setupEventHandlers() {
        queryBtn.setOnAction(e -> viewModel.loadRefundList());
        monthField.setOnAction(e -> viewModel.loadRefundList());
        confirmBtn.setOnAction(e -> handleConfirm());
    }

    private void handleConfirm() {
        List<MonthAttendCarryOverVO> items = refundTable.getItems();
        if (items.isEmpty()) {
            showAlert("没有可结转的数据");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("确认结转");
        confirm.setHeaderText(null);
        confirm.setContentText("确定要确认结转 " + items.size() + " 条记录吗？");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                viewModel.confirmCarryOver(new ArrayList<>(items));
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
        titleLabel.setText(i18n.getString("attend.title"));
        monthField.setPromptText(i18n.getString("attend.month.placeholder"));
        queryBtn.setText(i18n.getString("attend.query"));
        confirmBtn.setText(i18n.getString("attend.confirm"));

        for (TableColumn<MonthAttendCarryOverVO, ?> col : refundTable.getColumns()) {
            String colId = (String) col.getUserData();
            if (colId != null) col.setText(i18n.getString(colId));
        }
    }

    public void cleanup() {
        EventBus.getInstance().unsubscribe(LocaleChangeEvent.class, localeHandler);
    }
}
