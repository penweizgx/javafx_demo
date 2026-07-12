package com.example.app.controller;

import atlantafx.base.theme.Styles;
import com.example.app.AppContext;
import com.example.app.i18n.I18nService;
import com.example.app.i18n.LocaleChangeEvent;
import com.example.app.model.Employee;
import com.example.app.navigation.EventBus;
import com.example.app.navigation.NavigationClickEvent;
import com.example.app.navigation.RouteParams;
import com.example.app.service.EmployeeManageService;
import com.example.app.viewmodel.EmployeeListViewModel;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public class EmployeeListController {

    @FXML
    private VBox root;

    @FXML
    private Label titleLabel;

    @FXML
    private TextField searchField;

    @FXML
    private Button searchBtn;

    @FXML
    private TableView<Employee> employeeTable;

    @FXML
    private ProgressIndicator loadingIndicator;

    @FXML
    private Label errorLabel;

    @FXML
    private Button prevBtn;

    @FXML
    private Button nextBtn;

    @FXML
    private Label pageInfoLabel;

    @FXML
    private Label totalLabel;

    private EmployeeListViewModel viewModel;
    private I18nService i18n;

    private final Consumer<LocaleChangeEvent> localeHandler = e -> refreshI18n();

    @FXML
    public void initialize() {
        EmployeeManageService employeeService = AppContext.get().getService(EmployeeManageService.class);
        this.viewModel = new EmployeeListViewModel(employeeService);
        try {
            this.i18n = AppContext.get().getService(I18nService.class);
        } catch (Exception ignored) {}

        setupTable();
        setupEventHandlers();
        bindViewModel();
        subscribeEvents();
        viewModel.loadEmployees();
    }

    public void setViewModel(EmployeeListViewModel viewModel) {
        this.viewModel = viewModel;
        bindViewModel();
        viewModel.loadEmployees();
    }

    private void setupTable() {
        TableColumn<Employee, String> nameCol = new TableColumn<>(i18n != null ? i18n.getString("employee.col.name") : "姓名");
        nameCol.setUserData("employee.col.name");
        nameCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));
        nameCol.setPrefWidth(100);

        TableColumn<Employee, String> genderCol = new TableColumn<>(i18n != null ? i18n.getString("employee.col.gender") : "性别");
        genderCol.setUserData("employee.col.gender");
        genderCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getGender()));
        genderCol.setPrefWidth(60);

        TableColumn<Employee, String> phoneCol = new TableColumn<>(i18n != null ? i18n.getString("employee.col.phone") : "手机号");
        phoneCol.setUserData("employee.col.phone");
        phoneCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getPhone()));
        phoneCol.setPrefWidth(120);

        TableColumn<Employee, String> emailCol = new TableColumn<>(i18n != null ? i18n.getString("employee.col.email") : "邮箱");
        emailCol.setUserData("employee.col.email");
        emailCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getEmail()));
        emailCol.setPrefWidth(180);

        TableColumn<Employee, String> orgCol = new TableColumn<>(i18n != null ? i18n.getString("employee.col.org") : "所属机构");
        orgCol.setUserData("employee.col.org");
        orgCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getOrgName()));
        orgCol.setPrefWidth(120);

        TableColumn<Employee, String> hireDateCol = new TableColumn<>(i18n != null ? i18n.getString("employee.col.hireDate") : "入职日期");
        hireDateCol.setUserData("employee.col.hireDate");
        hireDateCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getHireDate()));
        hireDateCol.setPrefWidth(100);

        TableColumn<Employee, String> statusCol = new TableColumn<>(i18n != null ? i18n.getString("employee.col.status") : "状态");
        statusCol.setUserData("employee.col.status");
        statusCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getStatus() != null ? String.valueOf(data.getValue().getStatus()) : ""));
        statusCol.setPrefWidth(80);
        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    int statusVal = Integer.parseInt(status);
                    Label badge = new Label();
                    if (statusVal == 1) {
                        badge.setText(i18n != null ? i18n.getString("employee.status.active") : "在职");
                        badge.getStyleClass().addAll("badge", Styles.SUCCESS);
                    } else {
                        badge.setText(i18n != null ? i18n.getString("employee.status.inactive") : "离职");
                        badge.getStyleClass().addAll("badge", Styles.DANGER);
                    }
                    setGraphic(badge);
                }
            }
        });

        employeeTable.getColumns().addAll(nameCol, genderCol, phoneCol, emailCol, orgCol, hireDateCol, statusCol);

        employeeTable.setRowFactory(tv -> {
            TableRow<Employee> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    Employee emp = row.getItem();
                    RouteParams params = RouteParams.of("id", emp.getId()).put("employee", emp);
                    EventBus.getInstance().publish(new NavigationClickEvent(null, "/system/employee/detail/" + emp.getId(), params));
                }
            });
            return row;
        });
    }

    private void bindViewModel() {
        if (viewModel == null) return;

        employeeTable.setItems(viewModel.getEmployeeList());
        searchField.textProperty().bindBidirectional(viewModel.searchTextProperty());
        loadingIndicator.visibleProperty().bind(viewModel.loadingProperty());
        errorLabel.textProperty().bind(viewModel.errorMessageProperty());
        errorLabel.visibleProperty().bind(Bindings.isNotEmpty(viewModel.errorMessageProperty()));

        pageInfoLabel.textProperty().bind(Bindings.createStringBinding(
            () -> viewModel.getCurrentPage().get() + "/" + viewModel.getTotalPages().get(),
            viewModel.currentPageProperty(), viewModel.totalPagesProperty()
        ));

        totalLabel.textProperty().bind(Bindings.createStringBinding(
            () -> (i18n != null ? i18n.getString("employee.total") : "共") + " " + viewModel.getTotalCount().get() + " " + (i18n != null ? i18n.getString("employee.totalUnit") : "条"),
            viewModel.totalCountProperty()
        ));

        prevBtn.disableProperty().bind(viewModel.currentPageProperty().lessThanOrEqualTo(1));
        nextBtn.disableProperty().bind(viewModel.currentPageProperty().greaterThanOrEqualTo(viewModel.totalPagesProperty()));
    }

    private void setupEventHandlers() {
        if (searchBtn != null) {
            searchBtn.setOnAction(e -> {
                if (viewModel != null) viewModel.search();
            });
        }
        searchField.setOnAction(e -> {
            if (viewModel != null) viewModel.search();
        });
        if (prevBtn != null) {
            prevBtn.setOnAction(e -> {
                if (viewModel != null) viewModel.previousPage();
            });
        }
        if (nextBtn != null) {
            nextBtn.setOnAction(e -> {
                if (viewModel != null) viewModel.nextPage();
            });
        }
    }

    private void subscribeEvents() {
        EventBus.getInstance().subscribe(LocaleChangeEvent.class, localeHandler);
    }

    private void refreshI18n() {
        if (i18n == null) return;
        titleLabel.setText(i18n.getString("employee.title"));
        searchField.setPromptText(i18n.getString("employee.search.placeholder"));
        searchBtn.setText(i18n.getString("employee.search.btn"));
        prevBtn.setText(i18n.getString("employee.page.prev"));
        nextBtn.setText(i18n.getString("employee.page.next"));

        for (TableColumn<Employee, ?> col : employeeTable.getColumns()) {
            String colId = (String) col.getUserData();
            if (colId != null) {
                col.setText(i18n.getString(colId));
            }
        }
    }

    public void cleanup() {
        EventBus.getInstance().unsubscribe(LocaleChangeEvent.class, localeHandler);
    }
}