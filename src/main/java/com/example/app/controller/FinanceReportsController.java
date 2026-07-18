package com.example.app.controller;

import atlantafx.base.theme.Styles;
import com.example.app.AppContext;
import com.example.app.i18n.I18nService;
import com.example.app.i18n.LocaleChangeEvent;
import com.example.app.model.*;
import com.example.app.navigation.EventBus;
import com.example.app.service.FinanceManageService;
import com.example.app.viewmodel.FinanceReportsViewModel;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.text.DecimalFormat;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.function.Consumer;

public class FinanceReportsController {

    @FXML private VBox root;
    @FXML private Label titleLabel;
    @FXML private TextField monthField;
    @FXML private Button queryBtn;
    @FXML private ProgressIndicator loadingIndicator;
    @FXML private Label errorLabel;
    @FXML private GridPane summaryGrid;
    @FXML private ComboBox<String> dimensionCombo;
    @FXML private TableView<ChargeSubjectReport> subjectTable;
    @FXML private BarChart<String, Number> subjectBarChart;
    @FXML private CategoryAxis subjectXAxis;
    @FXML private NumberAxis subjectYAxis;
    @FXML private TableView<CarryOverSubjectReport> carryOverTable;
    @FXML private PieChart carryOverPieChart;
    @FXML private TableView<PayChancelReport> payChancelTable;
    @FXML private PieChart payChancelPieChart;

    private FinanceReportsViewModel viewModel;
    private I18nService i18n;
    private final Consumer<LocaleChangeEvent> localeHandler = e -> refreshI18n();
    private final DecimalFormat moneyFormat = new DecimalFormat("#,##0.00");

    @FXML
    public void initialize() {
        FinanceManageService financeService = AppContext.get().getService(FinanceManageService.class);
        this.viewModel = new FinanceReportsViewModel(financeService);
        try {
            this.i18n = AppContext.get().getService(I18nService.class);
        } catch (Exception ignored) {}

        setupMonthDefault();
        setupSummaryGrid();
        setupSubjectTable();
        setupCarryOverTable();
        setupPayChancelTable();
        setupDimensionCombo();
        setupBindings();
        setupEventHandlers();
        subscribeEvents();
    }

    private void setupMonthDefault() {
        String currentMonth = YearMonth.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        monthField.setText(currentMonth);
        viewModel.selectedMonthProperty().set(currentMonth);
    }

    private void setupSummaryGrid() {
        String[] rowLabels = {"收费", "余额抵扣", "结转", "退费"};
        String[] colLabels = {"应收金额", "实收金额", "金额", "单据数", "学生数"};

        for (int c = 0; c < colLabels.length; c++) {
            Label colHeader = new Label(colLabels[c]);
            colHeader.setStyle("-fx-font-weight: bold;");
            summaryGrid.add(colHeader, c + 1, 0);
        }

        for (int r = 0; r < rowLabels.length; r++) {
            Label rowHeader = new Label(rowLabels[r]);
            rowHeader.setStyle("-fx-font-weight: bold;");
            summaryGrid.add(rowHeader, 0, r + 1);
        }
    }

    private void updateSummaryGrid() {
        Map<String, ChargeReportItem> report = viewModel.getChargeReport().get();
        if (report == null) return;

        String[] keys = {"charge", "balance", "carryover", "refund"};
        for (int r = 0; r < keys.length; r++) {
            ChargeReportItem item = report.get(keys[r]);
            if (item == null) continue;

            String receivable = item.getReceivableAmount() != null ? moneyFormat.format(item.getReceivableAmount()) : "0.00";
            String pay = item.getPayAmount() != null ? moneyFormat.format(item.getPayAmount()) : "0.00";
            String amount = item.getAmount() != null ? moneyFormat.format(item.getAmount()) : "0.00";
            String billNum = item.getBillnumber() != null ? String.valueOf(item.getBillnumber()) : "0";
            String studentNum = item.getStudentNum() != null ? String.valueOf(item.getStudentNum()) : "0";

            String[] values = {receivable, pay, amount, billNum, studentNum};
            for (int c = 0; c < values.length; c++) {
                summaryGrid.add(new Label(values[c]), c + 1, r + 1);
            }
        }
    }

    private void setupSubjectTable() {
        TableColumn<ChargeSubjectReport, String> nameCol = new TableColumn<>("项目名称");
        nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSubjectName()));
        nameCol.setPrefWidth(150);

        TableColumn<ChargeSubjectReport, String> receivableCol = new TableColumn<>("应收金额");
        receivableCol.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getReceivableAmount() != null ? moneyFormat.format(data.getValue().getReceivableAmount()) : "0.00"));
        receivableCol.setPrefWidth(100);

        TableColumn<ChargeSubjectReport, String> balanceCol = new TableColumn<>("余额抵扣");
        balanceCol.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getBalancesAmount() != null ? moneyFormat.format(data.getValue().getBalancesAmount()) : "0.00"));
        balanceCol.setPrefWidth(100);

        TableColumn<ChargeSubjectReport, String> payCol = new TableColumn<>("实收金额");
        payCol.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getPayAmount() != null ? moneyFormat.format(data.getValue().getPayAmount()) : "0.00"));
        payCol.setPrefWidth(100);

        TableColumn<ChargeSubjectReport, String> ratioCol = new TableColumn<>("占比");
        ratioCol.setCellValueFactory(data -> {
            ChargeSubjectReport item = data.getValue();
            if (item.getReceivableAmount() == null || item.getReceivableAmount().doubleValue() == 0) {
                return new SimpleStringProperty("0%");
            }
            double ratio = item.getPayAmount() != null
                ? item.getPayAmount().doubleValue() / item.getReceivableAmount().doubleValue() * 100
                : 0;
            return new SimpleStringProperty(String.format("%.1f%%", ratio));
        });
        ratioCol.setPrefWidth(80);

        subjectTable.getColumns().addAll(nameCol, receivableCol, balanceCol, payCol, ratioCol);
    }

    private void setupCarryOverTable() {
        TableColumn<CarryOverSubjectReport, String> nameCol = new TableColumn<>("项目名称");
        nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSubjectName()));
        nameCol.setPrefWidth(150);

        TableColumn<CarryOverSubjectReport, String> amountCol = new TableColumn<>("金额");
        amountCol.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getReceivableAmount() != null ? moneyFormat.format(data.getValue().getReceivableAmount()) : "0.00"));
        amountCol.setPrefWidth(100);

        TableColumn<CarryOverSubjectReport, String> ratioCol = new TableColumn<>("占比");
        ratioCol.setCellValueFactory(data -> {
            double total = viewModel.getCarryOverReports().stream()
                .mapToDouble(r -> r.getReceivableAmount() != null ? r.getReceivableAmount().doubleValue() : 0)
                .sum();
            if (total == 0) return new SimpleStringProperty("0%");
            double val = data.getValue().getReceivableAmount() != null ? data.getValue().getReceivableAmount().doubleValue() : 0;
            return new SimpleStringProperty(String.format("%.1f%%", val / total * 100));
        });
        ratioCol.setPrefWidth(80);

        carryOverTable.getColumns().addAll(nameCol, amountCol, ratioCol);
    }

    private void setupPayChancelTable() {
        TableColumn<PayChancelReport, String> nameCol = new TableColumn<>("收款方式");
        nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        nameCol.setPrefWidth(150);

        TableColumn<PayChancelReport, String> amountCol = new TableColumn<>("金额");
        amountCol.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getAmount() != null ? moneyFormat.format(data.getValue().getAmount()) : "0.00"));
        amountCol.setPrefWidth(100);

        payChancelTable.getColumns().addAll(nameCol, amountCol);
    }

    private void setupDimensionCombo() {
        dimensionCombo.setItems(FXCollections.observableArrayList("实收金额", "应收金额", "余额抵扣"));
        dimensionCombo.setValue("实收金额");
        dimensionCombo.setOnAction(e -> updateSubjectBarChart());
    }

    private void updateSubjectBarChart() {
        subjectBarChart.getData().clear();
        String dimension = dimensionCombo.getValue();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(dimension);

        for (ChargeSubjectReport item : viewModel.getSubjectReports()) {
            Number value = null;
            switch (dimension) {
                case "实收金额": value = item.getPayAmount(); break;
                case "应收金额": value = item.getReceivableAmount(); break;
                case "余额抵扣": value = item.getBalancesAmount(); break;
            }
            series.getData().add(new XYChart.Data<>(
                item.getSubjectName() != null ? item.getSubjectName() : "",
                value != null ? value : 0
            ));
        }

        subjectBarChart.getData().add(series);
        subjectBarChart.setLegendVisible(false);
    }

    private void updateCarryOverPieChart() {
        carryOverPieChart.getData().clear();
        for (CarryOverSubjectReport item : viewModel.getCarryOverReports()) {
            String name = item.getSubjectName() != null ? item.getSubjectName() : "未知";
            double value = item.getReceivableAmount() != null ? item.getReceivableAmount().doubleValue() : 0;
            carryOverPieChart.getData().add(new PieChart.Data(name, value));
        }
    }

    private void updatePayChancelPieChart() {
        payChancelPieChart.getData().clear();
        for (PayChancelReport item : viewModel.getPayChancelReports()) {
            String name = item.getName() != null ? item.getName() : "未知";
            double value = item.getAmount() != null ? item.getAmount().doubleValue() : 0;
            payChancelPieChart.getData().add(new PieChart.Data(name, value));
        }
    }

    private void setupBindings() {
        subjectTable.setItems(viewModel.getSubjectReports());
        carryOverTable.setItems(viewModel.getCarryOverReports());
        payChancelTable.setItems(viewModel.getPayChancelReports());
        loadingIndicator.visibleProperty().bind(viewModel.loadingProperty());
        errorLabel.textProperty().bind(viewModel.errorMessageProperty());
        errorLabel.visibleProperty().bind(Bindings.isNotEmpty(viewModel.errorMessageProperty()));

        viewModel.chargeReportProperty().addListener((obs, oldVal, newVal) -> updateSummaryGrid());
        viewModel.getSubjectReports().addListener((javafx.collections.ListChangeListener<ChargeSubjectReport>) c -> updateSubjectBarChart());
        viewModel.getCarryOverReports().addListener((javafx.collections.ListChangeListener<CarryOverSubjectReport>) c -> updateCarryOverPieChart());
        viewModel.getPayChancelReports().addListener((javafx.collections.ListChangeListener<PayChancelReport>) c -> updatePayChancelPieChart());
    }

    private void setupEventHandlers() {
        queryBtn.setOnAction(e -> {
            viewModel.selectedMonthProperty().set(monthField.getText());
            viewModel.loadReports(monthField.getText());
        });
        monthField.setOnAction(e -> {
            viewModel.selectedMonthProperty().set(monthField.getText());
            viewModel.loadReports(monthField.getText());
        });
    }

    private void subscribeEvents() {
        EventBus.getInstance().subscribe(LocaleChangeEvent.class, localeHandler);
    }

    private void refreshI18n() {
        if (i18n == null) return;
        titleLabel.setText(i18n.getString("finance.reports.title"));
    }

    public void cleanup() {
        EventBus.getInstance().unsubscribe(LocaleChangeEvent.class, localeHandler);
    }
}
