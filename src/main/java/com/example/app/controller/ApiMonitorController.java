package com.example.app.controller;

import atlantafx.base.theme.Styles;
import com.example.app.AppContext;
import com.example.app.i18n.I18nService;
import com.example.app.i18n.LocaleChangeEvent;
import com.example.app.model.ApiRequestLog;
import com.example.app.navigation.EventBus;
import com.example.app.service.ApiMonitorService;
import com.example.app.viewmodel.ApiMonitorViewModel;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

public class ApiMonitorController {

    @FXML
    private VBox root;

    @FXML
    private Label titleLabel;

    @FXML
    private TextField filterField;

    @FXML
    private Button clearBtn;

    @FXML
    private ToggleButton monitorToggle;

    @FXML
    private TableView<ApiRequestLog> logTable;

    @FXML
    private SplitPane splitPane;

    private VBox detailPanel;
    private Button closeDetailBtn;
    private TextField detailMethodField;
    private TextField detailStatusField;
    private TextField detailDurationField;
    private TextField detailTimeField;
    private TextField detailUrlField;
    private TextArea detailRequestArea;
    private TextArea detailResponseArea;

    private ApiMonitorViewModel viewModel;
    private I18nService i18n;

    private final Consumer<LocaleChangeEvent> localeHandler = e -> refreshI18n();

    @FXML
    public void initialize() {
        ApiMonitorService monitorService = AppContext.get().getService(ApiMonitorService.class);
        this.viewModel = new ApiMonitorViewModel(monitorService);
        try {
            this.i18n = AppContext.get().getService(I18nService.class);
        } catch (Exception ignored) {}

        buildDetailPanel();
        setupTable();
        bindViewModel();
        setupEventHandlers();
        subscribeEvents();
    }

    private void buildDetailPanel() {
        detailPanel = new VBox(6);
        detailPanel.setMinWidth(280);
        detailPanel.setStyle("-fx-padding: 8 12 8 12;");

        HBox header = new HBox(8);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label headerLabel = new Label("请求详情");
        headerLabel.getStyleClass().add("h5");
        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        closeDetailBtn = new Button("✕");
        closeDetailBtn.getStyleClass().addAll("button-icon", "small");
        header.getChildren().addAll(headerLabel, spacer, closeDetailBtn);

        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(6);
        ColumnConstraints labelCol = new ColumnConstraints(70, 70, 70);
        ColumnConstraints valueCol = new ColumnConstraints();
        valueCol.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(labelCol, valueCol);

        String[] labels = {"Method", "Status", "Duration", "Time", "URL"};
        detailMethodField = createCopyableField();
        detailStatusField = createCopyableField();
        detailDurationField = createCopyableField();
        detailTimeField = createCopyableField();
        detailUrlField = createCopyableField();
        TextField[] fields = {detailMethodField, detailStatusField, detailDurationField, detailTimeField, detailUrlField};

        for (int i = 0; i < labels.length; i++) {
            Label lbl = new Label(labels[i]);
            lbl.getStyleClass().add("bold");
            grid.add(lbl, 0, i);
            grid.add(fields[i], 1, i);
        }

        Separator sep1 = new Separator();

        VBox reqBox = new VBox(4);
        Label reqLabel = new Label("请求参数");
        reqLabel.getStyleClass().add("bold");
        detailRequestArea = new TextArea();
        detailRequestArea.setWrapText(true);
        detailRequestArea.setEditable(false);
        detailRequestArea.setPrefRowCount(6);
        detailRequestArea.getStyleClass().add("rounded");
        VBox.setVgrow(detailRequestArea, Priority.ALWAYS);
        reqBox.getChildren().addAll(reqLabel, detailRequestArea);
        VBox.setVgrow(reqBox, Priority.ALWAYS);

        VBox resBox = new VBox(4);
        Label resLabel = new Label("返回参数");
        resLabel.getStyleClass().add("bold");
        detailResponseArea = new TextArea();
        detailResponseArea.setWrapText(true);
        detailResponseArea.setEditable(false);
        detailResponseArea.setPrefRowCount(6);
        detailResponseArea.getStyleClass().add("rounded");
        VBox.setVgrow(detailResponseArea, Priority.ALWAYS);
        resBox.getChildren().addAll(resLabel, detailResponseArea);
        VBox.setVgrow(resBox, Priority.ALWAYS);

        Separator sep2 = new Separator();

        detailPanel.getChildren().addAll(header, sep1, grid, sep2, reqBox, resBox);
    }

    private TextField createCopyableField() {
        TextField field = new TextField();
        field.setEditable(false);
        field.setFocusTraversable(false);
        field.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-border-color: transparent; -fx-border-width: 0; -fx-border-insets: 0; -fx-padding: 2 4;");
        return field;
    }

    private void setupTable() {
        TableColumn<ApiRequestLog, String> timeCol = new TableColumn<>(
                i18n != null ? i18n.getString("apimonitor.col.time") : "时间");
        timeCol.setUserData("apimonitor.col.time");
        timeCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                formatTimestamp(data.getValue().getTimestamp())));
        timeCol.setPrefWidth(160);

        TableColumn<ApiRequestLog, String> methodCol = new TableColumn<>(
                i18n != null ? i18n.getString("apimonitor.col.method") : "Method");
        methodCol.setUserData("apimonitor.col.method");
        methodCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getMethod()));
        methodCol.setPrefWidth(70);
        methodCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String method, boolean empty) {
                super.updateItem(method, empty);
                if (empty || method == null) {
                    setText(null);
                    setGraphic(null);
                    getStyleClass().removeAll("badge", Styles.ACCENT, Styles.SUCCESS, Styles.DANGER, Styles.WARNING);
                } else {
                    Label badge = new Label(method);
                    badge.getStyleClass().addAll("badge", Styles.SMALL);
                    switch (method) {
                        case "GET": badge.getStyleClass().add(Styles.SUCCESS); break;
                        case "POST": badge.getStyleClass().add(Styles.ACCENT); break;
                        case "PUT", "PATCH":
                            badge.getStyleClass().add(Styles.WARNING); break;
                        case "DELETE": badge.getStyleClass().add(Styles.DANGER); break;
                        default: break;
                    }
                    setGraphic(badge);
                    setText(null);
                }
            }
        });

        TableColumn<ApiRequestLog, String> urlCol = new TableColumn<>(
                i18n != null ? i18n.getString("apimonitor.col.url") : "URL");
        urlCol.setUserData("apimonitor.col.url");
        urlCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getUrl()));
        urlCol.setPrefWidth(400);

        TableColumn<ApiRequestLog, Number> statusCol = new TableColumn<>(
                i18n != null ? i18n.getString("apimonitor.col.status") : "状态码");
        statusCol.setUserData("apimonitor.col.status");
        statusCol.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(
                data.getValue().getStatusCode()));
        statusCol.setPrefWidth(80);
        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Number status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null || status.intValue() == 0) {
                    setText(null);
                    setGraphic(null);
                    getStyleClass().removeAll("badge", Styles.SUCCESS, Styles.DANGER, Styles.WARNING);
                } else {
                    int code = status.intValue();
                    Label badge = new Label(String.valueOf(code));
                    badge.getStyleClass().addAll("badge", Styles.SMALL);
                    if (code >= 200 && code < 300) {
                        badge.getStyleClass().add(Styles.SUCCESS);
                    } else if (code >= 400 && code < 500) {
                        badge.getStyleClass().add(Styles.WARNING);
                    } else if (code >= 500) {
                        badge.getStyleClass().add(Styles.DANGER);
                    }
                    setGraphic(badge);
                    setText(null);
                }
            }
        });

        TableColumn<ApiRequestLog, String> durationCol = new TableColumn<>(
                i18n != null ? i18n.getString("apimonitor.col.duration") : "耗时");
        durationCol.setUserData("apimonitor.col.duration");
        durationCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getDurationMs() + "ms"));
        durationCol.setPrefWidth(80);

        logTable.getColumns().addAll(timeCol, methodCol, urlCol, statusCol, durationCol);
    }

    private String formatTimestamp(long timestamp) {
        if (timestamp <= 0) return "";
        LocalDateTime ldt = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
        return ldt.format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
    }

    private void bindViewModel() {
        if (viewModel == null) return;

        logTable.setItems(viewModel.getFilteredLogs());
        filterField.textProperty().bindBidirectional(viewModel.filterTextProperty());
        monitorToggle.selectedProperty().bindBidirectional(viewModel.monitoringProperty());

        logTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            viewModel.selectedLogProperty().set(newVal);
            showDetail(newVal);
        });

        viewModel.selectedLogProperty().isNotNull().addListener((obs, wasNotNull, isNotNull) -> {
            if (isNotNull) {
                if (!splitPane.getItems().contains(detailPanel)) {
                    splitPane.getItems().add(detailPanel);
                    splitPane.setDividerPositions(0.6);
                }
            } else {
                splitPane.getItems().remove(detailPanel);
            }
        });

        closeDetailBtn.setOnAction(e -> {
            logTable.getSelectionModel().clearSelection();
            viewModel.selectedLogProperty().set(null);
        });
    }

    private void showDetail(ApiRequestLog log) {
        if (log == null) {
            detailUrlField.setText("");
            detailMethodField.setText("");
            detailStatusField.setText("");
            detailDurationField.setText("");
            detailTimeField.setText("");
            detailRequestArea.setText("");
            detailResponseArea.setText("");
            return;
        }
        detailUrlField.setText(log.getUrl());
        detailMethodField.setText(log.getMethod());
        detailStatusField.setText(String.valueOf(log.getStatusCode()));
        detailDurationField.setText(log.getDurationMs() + "ms");
        LocalDateTime ldt = LocalDateTime.ofInstant(Instant.ofEpochMilli(log.getTimestamp()), ZoneId.systemDefault());
        detailTimeField.setText(ldt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")));

        StringBuilder reqText = new StringBuilder();
        if (log.getRequestParams() != null && !log.getRequestParams().isEmpty()) {
            reqText.append("Query: ").append(log.getRequestParams()).append("\n\n");
        }
        if (log.getRequestBody() != null && !log.getRequestBody().isEmpty()) {
            reqText.append("Body: ").append(log.getRequestBody());
        }
        detailRequestArea.setText(reqText.toString());

        if (log.getError() != null && !log.getError().isEmpty()) {
            detailResponseArea.setText("Error: " + log.getError());
        } else if (log.getResponseSummary() != null) {
            detailResponseArea.setText(log.getResponseSummary());
        } else {
            detailResponseArea.setText("");
        }
    }

    private void setupEventHandlers() {
        if (clearBtn != null) {
            clearBtn.setOnAction(e -> viewModel.clear());
        }
    }

    private void subscribeEvents() {
        EventBus.getInstance().subscribe(LocaleChangeEvent.class, localeHandler);
    }

    private void refreshI18n() {
        if (i18n == null) return;
        titleLabel.setText(i18n.getString("apimonitor.title"));
        filterField.setPromptText(i18n.getString("apimonitor.filter.placeholder"));
        clearBtn.setText(i18n.getString("apimonitor.clear"));
        monitorToggle.setText(i18n.getString("apimonitor.pause"));

        for (TableColumn<ApiRequestLog, ?> col : logTable.getColumns()) {
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
