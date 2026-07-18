package com.example.app.controller;

import atlantafx.base.theme.Styles;
import com.example.app.AppContext;
import com.example.app.component.BillDetailModal;
import com.example.app.i18n.I18nService;
import com.example.app.i18n.LocaleChangeEvent;
import com.example.app.model.BillTimeDTO;
import com.example.app.model.CarryItem;
import com.example.app.model.ClazzOptionVO;
import com.example.app.model.SubjectItem;
import com.example.app.navigation.EventBus;
import com.example.app.service.FinanceManageService;
import com.example.app.viewmodel.FinanceBillsViewModel;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class FinanceBillsController {

    @FXML private VBox root;
    @FXML private Label titleLabel;
    @FXML private TextField searchField;
    @FXML private ComboBox<ClazzOptionVO> clazzCombo;
    @FXML private ComboBox<String> billTypeCombo;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private CheckBox filterInvalidCheck;
    @FXML private Button queryBtn;
    @FXML private Button exportBtn;
    @FXML private TableView<BillTimeDTO> billTable;
    @FXML private ProgressIndicator loadingIndicator;
    @FXML private Label errorLabel;
    @FXML private Button prevBtn;
    @FXML private Button nextBtn;
    @FXML private Label pageInfoLabel;
    @FXML private Label totalLabel;

    private FinanceBillsViewModel viewModel;
    private I18nService i18n;

    private final Consumer<LocaleChangeEvent> localeHandler = e -> refreshI18n();

    @FXML
    public void initialize() {
        FinanceManageService financeService = AppContext.get().getService(FinanceManageService.class);
        this.viewModel = new FinanceBillsViewModel(financeService);
        try {
            this.i18n = AppContext.get().getService(I18nService.class);
        } catch (Exception ignored) {}

        setupTable();
        setupBillTypeCombo();
        setupClazzCombo();
        setupBindings();
        setupEventHandlers();
        subscribeEvents();

        viewModel.loadBills();
        viewModel.loadClazzOptions();
    }

    private void setupBillTypeCombo() {
        billTypeCombo.getItems().addAll("ALL", "BASE", "CHARGE", "CARRY_OVER", "REFUND", "DEPOSIT");
        billTypeCombo.setValue("ALL");
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
        TableColumn<BillTimeDTO, String> bizDateCol = new TableColumn<>("业务日期");
        bizDateCol.setUserData("bills.col.bizDate");
        bizDateCol.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getBizDate() != null ? data.getValue().getBizDate() : "-"));
        bizDateCol.setPrefWidth(100);

        TableColumn<BillTimeDTO, String> studentCol = new TableColumn<>("学生/班级");
        studentCol.setUserData("bills.col.student");
        studentCol.setCellValueFactory(data -> {
            BillTimeDTO bill = data.getValue();
            String name = bill.getStudentName() != null ? bill.getStudentName() : "";
            String clazz = bill.getClazzName() != null ? bill.getClazzName() : "";
            return new SimpleStringProperty(name + (clazz.isEmpty() ? "" : " / " + clazz));
        });
        studentCol.setPrefWidth(150);

        TableColumn<BillTimeDTO, String> typeCol = new TableColumn<>("类型");
        typeCol.setUserData("bills.col.type");
        typeCol.setCellValueFactory(data -> new SimpleStringProperty(formatBillType(data.getValue().getType())));
        typeCol.setPrefWidth(80);
        typeCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String type, boolean empty) {
                super.updateItem(type, empty);
                if (empty || type == null) {
                    setGraphic(null);
                } else {
                    Label badge = new Label(type);
                    badge.getStyleClass().addAll("badge", Styles.ACCENT);
                    setGraphic(badge);
                }
            }
        });

        TableColumn<BillTimeDTO, String> receivableCol = new TableColumn<>("应收");
        receivableCol.setUserData("bills.col.receivable");
        receivableCol.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getTotalAmount() != null ? "¥" + data.getValue().getTotalAmount() : "¥0"));
        receivableCol.setPrefWidth(90);

        TableColumn<BillTimeDTO, String> realCol = new TableColumn<>("实收");
        realCol.setUserData("bills.col.real");
        realCol.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getRealAmount() != null ? "¥" + data.getValue().getRealAmount() : "¥0"));
        realCol.setPrefWidth(90);

        TableColumn<BillTimeDTO, String> deductionCol = new TableColumn<>("抵扣");
        deductionCol.setUserData("bills.col.deduction");
        deductionCol.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getDeductionAmount() != null ? "¥" + data.getValue().getDeductionAmount() : "¥0"));
        deductionCol.setPrefWidth(80);

        TableColumn<BillTimeDTO, String> detailsCol = new TableColumn<>("明细");
        detailsCol.setUserData("bills.col.details");
        detailsCol.setCellValueFactory(data -> {
            BillTimeDTO bill = data.getValue();
            if (bill.getSubjectItems() == null || bill.getSubjectItems().isEmpty()) return new SimpleStringProperty("-");
            return new SimpleStringProperty(bill.getSubjectItems().stream()
                .map(si -> formatSubjectItem(si))
                .collect(Collectors.joining(", ")));
        });
        detailsCol.setPrefWidth(200);

        TableColumn<BillTimeDTO, String> payInfoCol = new TableColumn<>("缴费信息");
        payInfoCol.setUserData("bills.col.payInfo");
        payInfoCol.setCellValueFactory(data -> {
            BillTimeDTO bill = data.getValue();
            String payer = bill.getPayUserName() != null ? bill.getPayUserName() : "";
            if (bill.getPayChancels() != null && !bill.getPayChancels().isEmpty()) {
                String channels = bill.getPayChancels().stream()
                    .map(c -> "¥" + (c.getAmount() != null ? c.getAmount() : "0"))
                    .collect(Collectors.joining("+"));
                return new SimpleStringProperty(payer + (payer.isEmpty() ? "" : " ") + channels);
            }
            return new SimpleStringProperty(payer);
        });
        payInfoCol.setPrefWidth(140);

        TableColumn<BillTimeDTO, String> opTimeCol = new TableColumn<>("操作时间");
        opTimeCol.setUserData("bills.col.opTime");
        opTimeCol.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getCreatedAt() != null ? formatTimestamp(data.getValue().getCreatedAt()) : "-"));
        opTimeCol.setPrefWidth(140);

        TableColumn<BillTimeDTO, Void> actionCol = new TableColumn<>("操作");
        actionCol.setUserData("bills.col.action");
        actionCol.setPrefWidth(100);
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button viewBtn = new Button("查看");
            {
                viewBtn.getStyleClass().addAll(Styles.BUTTON_OUTLINED, Styles.SMALL);
                viewBtn.setOnAction(e -> {
                    BillTimeDTO bill = getTableView().getItems().get(getIndex());
                    openDetailModal(bill);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    BillTimeDTO bill = getTableView().getItems().get(getIndex());
                    HBox box = new HBox(6);
                    box.setAlignment(Pos.CENTER_LEFT);
                    box.getChildren().add(viewBtn);
                    if (bill.getInvalid() != null) {
                        Label invalidBadge = new Label("已作废");
                        invalidBadge.getStyleClass().addAll("badge", Styles.DANGER);
                        box.getChildren().add(invalidBadge);
                    }
                    setGraphic(box);
                }
            }
        });

        billTable.getColumns().addAll(bizDateCol, studentCol, typeCol, receivableCol, realCol,
            deductionCol, detailsCol, payInfoCol, opTimeCol, actionCol);
    }

    private void setupBindings() {
        billTable.setItems(viewModel.getBillList());
        loadingIndicator.visibleProperty().bind(viewModel.loadingProperty());
        errorLabel.textProperty().bind(viewModel.errorMessageProperty());
        errorLabel.visibleProperty().bind(Bindings.isNotEmpty(viewModel.errorMessageProperty()));

        viewModel.selectedClazzProperty().bind(clazzCombo.valueProperty());
        viewModel.searchWordProperty().bindBidirectional(searchField.textProperty());
        viewModel.billTypeProperty().bind(billTypeCombo.valueProperty());
        viewModel.filterInvalidProperty().bind(filterInvalidCheck.selectedProperty());

        pageInfoLabel.textProperty().bind(Bindings.createStringBinding(
            () -> viewModel.getCurrentPage() + "/" + viewModel.getTotalPages(),
            viewModel.currentPageProperty(), viewModel.totalPagesProperty()
        ));

        totalLabel.textProperty().bind(Bindings.createStringBinding(
            () -> "共 " + viewModel.getTotalRecords() + " 条",
            viewModel.totalRecordsProperty()
        ));

        prevBtn.disableProperty().bind(viewModel.currentPageProperty().lessThanOrEqualTo(1));
        nextBtn.disableProperty().bind(viewModel.currentPageProperty().greaterThanOrEqualTo(viewModel.totalPagesProperty()));
    }

    private void setupEventHandlers() {
        queryBtn.setOnAction(e -> {
            applyDateFilter();
            viewModel.loadBills(1);
        });
        searchField.setOnAction(e -> {
            applyDateFilter();
            viewModel.loadBills(1);
        });
        prevBtn.setOnAction(e -> viewModel.prevPage());
        nextBtn.setOnAction(e -> viewModel.nextPage());
        exportBtn.setOnAction(e -> {
            Alert info = new Alert(Alert.AlertType.INFORMATION);
            info.setTitle("导出");
            info.setHeaderText(null);
            info.setContentText("导出功能开发中...");
            info.showAndWait();
        });
    }

    private void applyDateFilter() {
        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();
        viewModel.startDateProperty().set(start != null ? start.format(DateTimeFormatter.ISO_LOCAL_DATE) : "");
        viewModel.endDateProperty().set(end != null ? end.format(DateTimeFormatter.ISO_LOCAL_DATE) : "");
    }

    private void openDetailModal(BillTimeDTO bill) {
        new BillDetailModal(bill, () -> viewModel.loadBills()).show();
    }

    private String formatBillType(String type) {
        if (type == null) return "-";
        switch (type) {
            case "BASE": return "基础";
            case "CHARGE": return "收费";
            case "CARRY_OVER": return "结转";
            case "REFUND": return "退费";
            case "DEPOSIT": return "押金";
            default: return type;
        }
    }

    private String formatSubjectItem(SubjectItem si) {
        if (si == null) return "";
        Number amount = si.getReceivableAmount() != null ? si.getReceivableAmount() : 0;
        String period = si.getPeriod() != null && si.getPeriod().getStart() != null ? si.getPeriod().getStart() : "";
        return "¥" + amount + (period.isEmpty() ? "" : "(" + period + ")");
    }

    private String formatTimestamp(Long ts) {
        if (ts == null) return "-";
        java.time.Instant instant = java.time.Instant.ofEpochMilli(ts);
        return java.time.LocalDateTime.ofInstant(instant, java.time.ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    private void subscribeEvents() {
        EventBus.getInstance().subscribe(LocaleChangeEvent.class, localeHandler);
    }

    private void refreshI18n() {
        if (i18n == null) return;
        titleLabel.setText(i18n.getString("bills.title"));
        searchField.setPromptText(i18n.getString("bills.search.placeholder"));
        queryBtn.setText(i18n.getString("bills.query"));

        for (TableColumn<BillTimeDTO, ?> col : billTable.getColumns()) {
            String colId = (String) col.getUserData();
            if (colId != null) col.setText(i18n.getString(colId));
        }
    }

    public void cleanup() {
        EventBus.getInstance().unsubscribe(LocaleChangeEvent.class, localeHandler);
    }
}
