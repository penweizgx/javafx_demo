package com.example.app.controller;

import atlantafx.base.theme.Styles;
import com.example.app.AppContext;
import com.example.app.i18n.I18nService;
import com.example.app.i18n.LocaleChangeEvent;
import com.example.app.model.StudentVO;
import com.example.app.navigation.EventBus;
import com.example.app.navigation.NavigationClickEvent;
import com.example.app.navigation.ParamReceiver;
import com.example.app.navigation.RouteParams;
import com.example.app.service.StudentManageService;
import com.example.app.viewmodel.StudentListViewModel;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public class StudentListController implements ParamReceiver {

    @FXML
    private VBox root;

    @FXML
    private Label titleLabel;

    @FXML
    private Button backBtn;

    @FXML
    private Button addBtn;

    @FXML
    private TableView<StudentVO> studentTable;

    @FXML
    private ProgressIndicator loadingIndicator;

    @FXML
    private Label errorLabel;

    private StudentListViewModel viewModel;
    private I18nService i18n;

    private final Consumer<LocaleChangeEvent> localeHandler = e -> refreshI18n();

    @FXML
    public void initialize() {
        StudentManageService studentService = AppContext.get().getService(StudentManageService.class);
        this.viewModel = new StudentListViewModel(studentService);
        try {
            this.i18n = AppContext.get().getService(I18nService.class);
        } catch (Exception ignored) {}

        setupTable();
        setupBindings();
        setupEventHandlers();
        subscribeEvents();
    }

    @Override
    public void receiveParams(RouteParams params) {
        Object clazzIdObj = params.getData().get("clazzId");
        if (clazzIdObj instanceof Long clazzId) {
            viewModel.setClazzId(clazzId);
        } else if (clazzIdObj instanceof String clazzIdStr) {
            viewModel.setClazzId(Long.parseLong(clazzIdStr));
        }
        Object clazzNameObj = params.getData().get("clazzName");
        if (clazzNameObj instanceof String clazzName) {
            viewModel.getClassName().set(clazzName);
        }
        viewModel.loadStudents();
    }

    private void setupTable() {
        TableColumn<StudentVO, String> nameCol = new TableColumn<>("姓名");
        nameCol.setUserData("student.col.name");
        nameCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));
        nameCol.setPrefWidth(100);

        TableColumn<StudentVO, String> genderCol = new TableColumn<>("性别");
        genderCol.setUserData("student.col.gender");
        genderCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                sexDisplay(data.getValue().getSex())));
        genderCol.setPrefWidth(60);

        TableColumn<StudentVO, String> phoneCol = new TableColumn<>("手机号");
        phoneCol.setUserData("student.col.phone");
        phoneCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getPhone()));
        phoneCol.setPrefWidth(130);

        TableColumn<StudentVO, String> statusCol = new TableColumn<>("状态");
        statusCol.setUserData("student.col.status");
        statusCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getStatus() != null ? getStatusText(data.getValue().getStatus()) : ""));
        statusCol.setPrefWidth(80);
        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label badge = new Label(status);
                    badge.getStyleClass().addAll("badge", getStatusStyle(status));
                    setGraphic(badge);
                }
            }
        });

        TableColumn<StudentVO, Void> actionCol = new TableColumn<>("操作");
        actionCol.setUserData("student.col.action");
        actionCol.setPrefWidth(80);
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button viewBtn = new Button("查看");
            {
                viewBtn.getStyleClass().addAll(Styles.BUTTON_OUTLINED, Styles.SMALL);
                viewBtn.setOnAction(e -> {
                    StudentVO student = getTableView().getItems().get(getIndex());
                    navigateToDetail(student);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : viewBtn);
            }
        });

        studentTable.getColumns().addAll(nameCol, genderCol, phoneCol, statusCol, actionCol);

        studentTable.setRowFactory(tv -> {
            TableRow<StudentVO> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    navigateToDetail(row.getItem());
                }
            });
            return row;
        });
    }

    private void setupBindings() {
        if (viewModel == null) return;

        titleLabel.textProperty().bind(Bindings.createStringBinding(
            () -> viewModel.getClassName().get() != null && !viewModel.getClassName().get().isEmpty()
                    ? viewModel.getClassName().get() + " - 学生列表" : "学生列表",
            viewModel.classNameProperty()
        ));

        studentTable.setItems(viewModel.getStudentList());
        loadingIndicator.visibleProperty().bind(viewModel.loadingProperty());
        errorLabel.textProperty().bind(viewModel.errorMessageProperty());
        errorLabel.visibleProperty().bind(Bindings.isNotEmpty(viewModel.errorMessageProperty()));
    }

    private void setupEventHandlers() {
        if (backBtn != null) {
            backBtn.setOnAction(e -> navigateBack());
        }
        if (addBtn != null) {
            addBtn.setOnAction(e -> openCreateModal());
        }
    }

    private void navigateBack() {
        EventBus.getInstance().publish(new NavigationClickEvent(null, "/student/classes", RouteParams.empty()));
    }

    private void openCreateModal() {
        new com.example.app.component.StudentCreateModal(viewModel.getClazzId(), this::onStudentCreated).show();
    }

    private void onStudentCreated() {
        viewModel.refresh();
    }

    private void navigateToDetail(StudentVO student) {
        RouteParams params = RouteParams.of("id", student.getId()).put("student", student);
        EventBus.getInstance().publish(new NavigationClickEvent(null, "/student/detail/" + student.getId(), params));
    }

    private void subscribeEvents() {
        EventBus.getInstance().subscribe(LocaleChangeEvent.class, localeHandler);
    }

    private void refreshI18n() {
        if (i18n == null) return;
        backBtn.setText(i18n.getString("student.back"));
        addBtn.setText(i18n.getString("student.add"));

        for (TableColumn<StudentVO, ?> col : studentTable.getColumns()) {
            String colId = (String) col.getUserData();
            if (colId != null) col.setText(i18n.getString(colId));
        }
    }

    private String getStatusText(String status) {
        if (status == null) return "-";
        return switch (status) {
            case "REGISTER" -> "已登记";
            case "INSCHOOL" -> "在读";
            case "PAUSE" -> "休学";
            case "LEAVE" -> "离校";
            default -> status;
        };
    }

    private String getStatusStyle(String status) {
        if (status == null) return "";
        return switch (status) {
            case "REGISTER" -> Styles.WARNING;
            case "INSCHOOL" -> Styles.SUCCESS;
            case "PAUSE" -> Styles.WARNING;
            case "LEAVE" -> Styles.DANGER;
            default -> "";
        };
    }

    private static String sexDisplay(Object sex) {
        if (sex instanceof Number) return ((Number) sex).intValue() == 1 ? "男" : "女";
        return "";
    }

    public void cleanup() {
        EventBus.getInstance().unsubscribe(LocaleChangeEvent.class, localeHandler);
    }
}
