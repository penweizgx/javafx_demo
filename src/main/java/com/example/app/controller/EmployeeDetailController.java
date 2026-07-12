package com.example.app.controller;

import atlantafx.base.theme.Styles;
import com.example.app.model.Employee;
import com.example.app.navigation.EventBus;
import com.example.app.navigation.NavigationClickEvent;
import com.example.app.navigation.ParamReceiver;
import com.example.app.navigation.RouteParams;
import com.example.app.viewmodel.EmployeeDetailViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class EmployeeDetailController implements ParamReceiver {

    @FXML
    private VBox root;

    @FXML
    private Label titleLabel;

    @FXML
    private Button backBtn;

    @FXML
    private GridPane infoGrid;

    @FXML
    private Label idValue;

    @FXML
    private Label nameValue;

    @FXML
    private Label genderValue;

    @FXML
    private Label phoneValue;

    @FXML
    private Label emailValue;

    @FXML
    private Label orgValue;

    @FXML
    private Label hireDateValue;

    @FXML
    private Label birthdayValue;

    @FXML
    private Label statusValue;

    private EmployeeDetailViewModel viewModel;

    @FXML
    public void initialize() {
        this.viewModel = new EmployeeDetailViewModel();
        bindViewModel();
        setupEventHandlers();
    }

    @Override
    public void receiveParams(RouteParams params) {
        Object empObj = params.getData().get("employee");
        if (empObj instanceof Employee emp) {
            viewModel.setEmployee(emp);
            if (titleLabel != null) {
                titleLabel.setText("员工详情 - " + (emp.getName() != null ? emp.getName() : ""));
            }
        }
    }

    private void bindViewModel() {
        if (viewModel == null) return;

        idValue.textProperty().bind(viewModel.employeeIdProperty());
        nameValue.textProperty().bind(viewModel.nameProperty());
        genderValue.textProperty().bind(viewModel.genderProperty());
        phoneValue.textProperty().bind(viewModel.phoneProperty());
        emailValue.textProperty().bind(viewModel.emailProperty());
        orgValue.textProperty().bind(viewModel.orgNameProperty());
        hireDateValue.textProperty().bind(viewModel.hireDateProperty());
        birthdayValue.textProperty().bind(viewModel.birthdayProperty());
        statusValue.textProperty().bind(viewModel.statusProperty());
    }

    private void setupEventHandlers() {
        if (backBtn != null) {
            backBtn.setOnAction(e -> navigateBack());
        }
    }

    private void navigateBack() {
        EventBus.getInstance().publish(new NavigationClickEvent(null, "/system/employee/list", RouteParams.empty()));
    }

    public void cleanup() {}
}
