package com.example.app.controller;

import atlantafx.base.theme.Styles;
import com.example.app.AppContext;
import com.example.app.component.ChargeModal;
import com.example.app.component.FeeScaleBindModal;
import com.example.app.component.FinanceAccountDetailModal;
import com.example.app.i18n.I18nService;
import com.example.app.i18n.LocaleChangeEvent;
import com.example.app.model.ClazzOptionVO;
import com.example.app.model.FinAccountVO;
import com.example.app.model.FinanceBrief;
import com.example.app.model.StudentVO;
import com.example.app.navigation.EventBus;
import com.example.app.service.FinanceManageService;
import com.example.app.service.StudentManageService;
import com.example.app.viewmodel.FinanceAccountListViewModel;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public class FinanceAccountListController {

    @FXML private VBox root;
    @FXML private Label titleLabel;
    @FXML private Label overdueLabel;
    @FXML private Label overdueValue;
    @FXML private Label remindLabel;
    @FXML private Label remindValue;
    @FXML private Label depositLabel;
    @FXML private Label depositValue;
    @FXML private Label freeLabel;
    @FXML private Label freeValue;
    @FXML private TextField searchField;
    @FXML private ComboBox<ClazzOptionVO> clazzCombo;
    @FXML private Button queryBtn;
    @FXML private Button resetBtn;
    @FXML private TableView<FinAccountVO> accountTable;
    @FXML private ProgressIndicator loadingIndicator;
    @FXML private Label errorLabel;
    @FXML private Button prevBtn;
    @FXML private Button nextBtn;
    @FXML private Label pageInfoLabel;
    @FXML private Label totalLabel;

    private FinanceAccountListViewModel viewModel;
    private I18nService i18n;

    private final Consumer<LocaleChangeEvent> localeHandler = e -> refreshI18n();

    @FXML
    public void initialize() {
        FinanceManageService financeService = AppContext.get().getService(FinanceManageService.class);
        StudentManageService studentService = AppContext.get().getService(StudentManageService.class);
        this.viewModel = new FinanceAccountListViewModel(financeService, studentService);
        try {
            this.i18n = AppContext.get().getService(I18nService.class);
        } catch (Exception ignored) {}

        setupTable();
        setupClazzCombo();
        setupBindings();
        setupEventHandlers();
        subscribeEvents();

        viewModel.loadBrief();
        viewModel.loadAccounts();
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
        TableColumn<FinAccountVO, String> nameCol = new TableColumn<>("姓名");
        nameCol.setUserData("finance.col.name");
        nameCol.setCellValueFactory(data -> {
            StudentVO s = data.getValue().getStudent();
            String name = s != null ? s.getName() : "";
            String gender = s != null && s.getSex() instanceof Number ? (((Number) s.getSex()).intValue() == 1 ? " (男)" : " (女)") : "";
            return new SimpleStringProperty(name + gender);
        });
        nameCol.setPrefWidth(120);

        TableColumn<FinAccountVO, String> clazzCol = new TableColumn<>("班级");
        clazzCol.setUserData("finance.col.clazz");
        clazzCol.setCellValueFactory(data -> {
            StudentVO s = data.getValue().getStudent();
            return new SimpleStringProperty(s != null && s.getClazzname() != null ? s.getClazzname() : "-");
        });
        clazzCol.setPrefWidth(120);

        TableColumn<FinAccountVO, String> enrollCol = new TableColumn<>("入学日期");
        enrollCol.setUserData("finance.col.enroll");
        enrollCol.setCellValueFactory(data -> {
            StudentVO s = data.getValue().getStudent();
            return new SimpleStringProperty(s != null && s.getIndate() != null ? s.getIndate() : "-");
        });
        enrollCol.setPrefWidth(100);

        TableColumn<FinAccountVO, String> balanceCol = new TableColumn<>("余额");
        balanceCol.setUserData("finance.col.balance");
        balanceCol.setCellValueFactory(data -> {
            Number balance = data.getValue().getBalancesAmount();
            return new SimpleStringProperty(balance != null ? "¥" + balance : "¥0");
        });
        balanceCol.setPrefWidth(100);

        TableColumn<FinAccountVO, String> feeCol = new TableColumn<>("收费项目");
        feeCol.setUserData("finance.col.feeItems");
        feeCol.setCellValueFactory(data -> {
            if (data.getValue().getFeeScales() == null || data.getValue().getFeeScales().isEmpty()) {
                return new SimpleStringProperty("未绑定");
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < data.getValue().getFeeScales().size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append(data.getValue().getFeeScales().get(i).getName());
            }
            return new SimpleStringProperty(sb.toString());
        });
        feeCol.setPrefWidth(200);

        TableColumn<FinAccountVO, Void> actionCol = new TableColumn<>("操作");
        actionCol.setUserData("finance.col.action");
        actionCol.setPrefWidth(220);
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button viewBtn = new Button("查看");
            private final Button chargeBtn = new Button("收费");
            private final Button feeScaleBtn = new Button("设标准");
            {
                viewBtn.getStyleClass().addAll(Styles.BUTTON_OUTLINED, Styles.SMALL);
                chargeBtn.getStyleClass().addAll(Styles.ACCENT, Styles.SMALL);
                feeScaleBtn.getStyleClass().addAll(Styles.BUTTON_OUTLINED, Styles.SMALL);
                viewBtn.setOnAction(e -> openDetailModal(getTableView().getItems().get(getIndex())));
                chargeBtn.setOnAction(e -> openChargeModal(getTableView().getItems().get(getIndex())));
                feeScaleBtn.setOnAction(e -> openFeeScaleBindModal(getTableView().getItems().get(getIndex())));
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    FinAccountVO account = getTableView().getItems().get(getIndex());
                    HBox box = new HBox(6);
                    box.setAlignment(Pos.CENTER_LEFT);
                    box.getChildren().addAll(viewBtn, chargeBtn, feeScaleBtn);
                    if (account.getClosed() != null && account.getClosed()) {
                        Label closedBadge = new Label("已封账");
                        closedBadge.getStyleClass().addAll("badge", Styles.DANGER);
                        box.getChildren().add(closedBadge);
                    }
                    setGraphic(box);
                }
            }
        });

        accountTable.getColumns().addAll(nameCol, clazzCol, enrollCol, balanceCol, feeCol, actionCol);

        accountTable.setRowFactory(tv -> {
            TableRow<FinAccountVO> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    openDetailModal(row.getItem());
                }
            });
            return row;
        });
    }

    private void setupBindings() {
        if (viewModel == null) return;

        accountTable.setItems(viewModel.getAccountList());
        loadingIndicator.visibleProperty().bind(viewModel.loadingProperty());
        errorLabel.textProperty().bind(viewModel.errorMessageProperty());
        errorLabel.visibleProperty().bind(Bindings.isNotEmpty(viewModel.errorMessageProperty()));

        viewModel.briefProperty().addListener((obs, oldVal, newVal) -> updateDashboard(newVal));

        viewModel.selectedClazzProperty().bind(clazzCombo.valueProperty());
        searchField.textProperty().bindBidirectional(viewModel.searchKeywordProperty());

        totalLabel.textProperty().bind(Bindings.createStringBinding(
            () -> "共 " + viewModel.getAccountList().size() + " 条",
            viewModel.getAccountList()
        ));
    }

    private void updateDashboard(FinanceBrief brief) {
        if (brief == null) return;
        overdueValue.setText(brief.getExpirePerson() + "人/" + brief.getExpireSubject() + "科");
        remindValue.setText(brief.getRemindPerson() + "人/" + brief.getRemindSubject() + "科");
        depositValue.setText(brief.getDepositNum() + "笔/¥" + (brief.getDepositAmount() != null ? brief.getDepositAmount() : "0"));
        freeValue.setText(brief.getFreePerson() + "人");
    }

    private void setupEventHandlers() {
        queryBtn.setOnAction(e -> viewModel.searchAccounts());
        searchField.setOnAction(e -> viewModel.searchAccounts());
        resetBtn.setOnAction(e -> {
            searchField.clear();
            clazzCombo.setValue(null);
            viewModel.searchAccounts();
        });
    }

    private void openDetailModal(FinAccountVO account) {
        new FinanceAccountDetailModal(account, () -> viewModel.refresh()).show();
    }

    private void openChargeModal(FinAccountVO account) {
        if (account.getClosed() != null && account.getClosed()) {
            showAlert("该账户已封账，无法收费");
            return;
        }
        new ChargeModal(account, () -> viewModel.refresh()).show();
    }

    private void openFeeScaleBindModal(FinAccountVO account) {
        new FeeScaleBindModal(account, () -> viewModel.refresh()).show();
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
        titleLabel.setText(i18n.getString("finance.title"));
        searchField.setPromptText(i18n.getString("finance.search.placeholder"));
        queryBtn.setText(i18n.getString("finance.query"));
        resetBtn.setText(i18n.getString("finance.reset"));
        overdueLabel.setText(i18n.getString("finance.overdue"));
        remindLabel.setText(i18n.getString("finance.remind"));
        depositLabel.setText(i18n.getString("finance.deposit"));
        freeLabel.setText(i18n.getString("finance.free"));

        for (TableColumn<FinAccountVO, ?> col : accountTable.getColumns()) {
            String colId = (String) col.getUserData();
            if (colId != null) col.setText(i18n.getString(colId));
        }
    }

    public void cleanup() {
        EventBus.getInstance().unsubscribe(LocaleChangeEvent.class, localeHandler);
    }
}
