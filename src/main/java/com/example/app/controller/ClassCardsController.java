package com.example.app.controller;

import atlantafx.base.controls.Card;
import atlantafx.base.theme.Styles;
import com.example.app.AppContext;
import com.example.app.i18n.I18nService;
import com.example.app.i18n.LocaleChangeEvent;
import com.example.app.model.ClazzWithCountVO;
import com.example.app.model.StudentVO;
import com.example.app.navigation.EventBus;
import com.example.app.navigation.NavigationClickEvent;
import com.example.app.navigation.RouteParams;
import com.example.app.service.StudentManageService;
import com.example.app.viewmodel.ClassCardsViewModel;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.function.Consumer;

public class ClassCardsController {

    @FXML
    private VBox root;

    @FXML
    private Label titleLabel;

    @FXML
    private TextField searchField;

    @FXML
    private Button searchBtn;

    @FXML
    private Button clearSearchBtn;

    @FXML
    private FlowPane cardContainer;

    @FXML
    private VBox searchResultContainer;

    @FXML
    private TableView<StudentVO> searchResultTable;

    @FXML
    private ProgressIndicator loadingIndicator;

    @FXML
    private Label errorLabel;

    @FXML
    private VBox emptyState;

    @FXML
    private Button prevBtn;

    @FXML
    private Button nextBtn;

    @FXML
    private Label pageInfoLabel;

    @FXML
    private Label totalLabel;

    private ClassCardsViewModel viewModel;
    private I18nService i18n;

    private final Consumer<LocaleChangeEvent> localeHandler = e -> refreshI18n();

    @FXML
    public void initialize() {
        StudentManageService studentService = AppContext.get().getService(StudentManageService.class);
        this.viewModel = new ClassCardsViewModel(studentService);
        try {
            this.i18n = AppContext.get().getService(I18nService.class);
        } catch (Exception ignored) {}

        setupSearchTable();
        setupBindings();
        setupEventHandlers();
        subscribeEvents();
        viewModel.loadData();
    }

    private void setupSearchTable() {
        TableColumn<StudentVO, String> nameCol = new TableColumn<>("姓名");
        nameCol.setUserData("student.col.name");
        nameCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));
        nameCol.setPrefWidth(100);

        TableColumn<StudentVO, String> clazzCol = new TableColumn<>("班级");
        clazzCol.setUserData("student.col.clazz");
        clazzCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getClazzname()));
        clazzCol.setPrefWidth(120);

        TableColumn<StudentVO, String> phoneCol = new TableColumn<>("手机号");
        phoneCol.setUserData("student.col.phone");
        phoneCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getPhone()));
        phoneCol.setPrefWidth(130);

        TableColumn<StudentVO, String> statusCol = new TableColumn<>("状态");
        statusCol.setUserData("student.col.status");
        statusCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getStatus() != null ? getStatusText(data.getValue().getStatus()) : ""));
        statusCol.setPrefWidth(80);

        searchResultTable.getColumns().addAll(nameCol, clazzCol, phoneCol, statusCol);

        searchResultTable.setRowFactory(tv -> {
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
        loadingIndicator.visibleProperty().bind(viewModel.loadingProperty());
        errorLabel.textProperty().bind(viewModel.errorMessageProperty());
        errorLabel.visibleProperty().bind(Bindings.isNotEmpty(viewModel.errorMessageProperty()));
        searchField.textProperty().bindBidirectional(viewModel.searchTextProperty());

        viewModel.searchingProperty().addListener((obs, old, searching) -> {
            cardContainer.setVisible(!searching);
            cardContainer.setManaged(!searching);
            searchResultContainer.setVisible(searching);
            searchResultContainer.setManaged(searching);
            clearSearchBtn.setVisible(searching);
            clearSearchBtn.setManaged(searching);
        });

        pageInfoLabel.textProperty().bind(Bindings.createStringBinding(
            () -> viewModel.getSearchCurrentPage().get() + "/" + viewModel.getSearchTotalPages().get(),
            viewModel.searchCurrentPageProperty(), viewModel.searchTotalPagesProperty()
        ));

        totalLabel.textProperty().bind(Bindings.createStringBinding(
            () -> "共 " + viewModel.getSearchTotalCount().get() + " 条",
            viewModel.searchTotalCountProperty()
        ));

        viewModel.loadingProperty().addListener((obs, old, loading) -> {
            if (!loading && !viewModel.getSearching().get()) {
                renderCards();
            }
        });
    }

    private void renderCards() {
        cardContainer.getChildren().clear();
        for (ClazzWithCountVO clazz : viewModel.getClassList()) {
            Card card = createClassCard(clazz);
            cardContainer.getChildren().add(card);
        }

        emptyState.setVisible(viewModel.getClassList().isEmpty());
        emptyState.setManaged(viewModel.getClassList().isEmpty());
    }

    private Card createClassCard(ClazzWithCountVO clazz) {
        Card card = new Card();
        card.setMaxWidth(280);
        card.setMinWidth(240);

        VBox content = new VBox(8);
        content.setPadding(new Insets(16));

        Label className = new Label(clazz.getName());
        className.getStyleClass().add(Styles.TITLE_3);

        String teacherName = (clazz.getTeachers() != null && !clazz.getTeachers().isEmpty())
                ? clazz.getTeachers().get(0).getName() : "";
        Label teacher = new Label(teacherName);
        teacher.getStyleClass().add(Styles.TEXT_MUTED);

        Separator sep = new Separator();

        VBox stats = new VBox(4);
        stats.getChildren().addAll(
            new Label("在读 " + (clazz.getActiveNum() != null ? clazz.getActiveNum() : 0) + "人"),
            new Label("登记 " + (clazz.getRegNum() != null ? clazz.getRegNum() : 0) + "人"),
            new Label("请假 " + (clazz.getLeaveNum() != null ? clazz.getLeaveNum() : 0) + "人"),
            new Label("空位 " + (clazz.getVacancy() != null ? clazz.getVacancy() : 0) + "/" + (clazz.getCapacity() != null ? clazz.getCapacity() : 0))
        );

        Separator sep2 = new Separator();

        Double rate = viewModel.getAttendanceRate().get(clazz.getId());
        Label attendRate = new Label(rate != null ? String.format("今日出勤 %.0f%%", rate) : "今日出勤 -");
        attendRate.getStyleClass().add(Styles.TEXT_BOLD);

        content.getChildren().addAll(className, teacher, sep, stats, sep2, attendRate);
        card.setBody(content);

        card.setOnMouseClicked(e -> {
            RouteParams params = RouteParams.of("clazzId", clazz.getId()).put("clazzName", clazz.getName());
            EventBus.getInstance().publish(new NavigationClickEvent(null, "/student/class/" + clazz.getId(), params));
        });

        card.getStyleClass().add(Styles.INTERACTIVE);
        return card;
    }

    private void setupEventHandlers() {
        if (searchBtn != null) {
            searchBtn.setOnAction(e -> viewModel.search());
        }
        searchField.setOnAction(e -> viewModel.search());

        if (clearSearchBtn != null) {
            clearSearchBtn.setOnAction(e -> viewModel.clearSearch());
        }

        if (prevBtn != null) {
            prevBtn.setOnAction(e -> {
                viewModel.previousSearchPage();
                refreshSearchTable();
            });
        }
        if (nextBtn != null) {
            nextBtn.setOnAction(e -> {
                viewModel.nextSearchPage();
                refreshSearchTable();
            });
        }
    }

    private void refreshSearchTable() {
        searchResultTable.getItems().setAll(viewModel.getCurrentSearchPage());
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
        titleLabel.setText(i18n.getString("student.title"));
        searchField.setPromptText(i18n.getString("student.search.placeholder"));
        searchBtn.setText(i18n.getString("student.search.btn"));
        clearSearchBtn.setText(i18n.getString("student.search.clear"));
        prevBtn.setText(i18n.getString("student.page.prev"));
        nextBtn.setText(i18n.getString("student.page.next"));

        for (TableColumn<StudentVO, ?> col : searchResultTable.getColumns()) {
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

    public void cleanup() {
        EventBus.getInstance().unsubscribe(LocaleChangeEvent.class, localeHandler);
    }
}
