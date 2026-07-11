package com.example.app.viewmodel;

import com.example.app.model.ApiRequestLog;
import com.example.app.service.ApiMonitorService;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import lombok.Getter;

public class ApiMonitorViewModel extends ViewModelBase {

    private final ApiMonitorService monitorService;

    @Getter
    private final ObservableList<ApiRequestLog> allLogs;

    @Getter
    private final FilteredList<ApiRequestLog> filteredLogs;

    @Getter
    private final StringProperty filterText = new SimpleStringProperty("");

    @Getter
    private final BooleanProperty monitoring = new SimpleBooleanProperty(true);

    @Getter
    private final ObjectProperty<ApiRequestLog> selectedLog = new SimpleObjectProperty<>();

    public ApiMonitorViewModel(ApiMonitorService monitorService) {
        this.monitorService = monitorService;
        this.allLogs = monitorService.getLogs();
        this.filteredLogs = new FilteredList<>(allLogs, p -> true);

        filterText.addListener((obs, oldVal, newVal) -> applyFilter(newVal));
        monitoring.addListener((obs, oldVal, newVal) -> monitorService.setMonitoring(newVal));
    }

    public StringProperty filterTextProperty() { return filterText; }
    public BooleanProperty monitoringProperty() { return monitoring; }
    public ObjectProperty<ApiRequestLog> selectedLogProperty() { return selectedLog; }

    private void applyFilter(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            filteredLogs.setPredicate(p -> true);
        } else {
            String lower = keyword.trim().toLowerCase();
            filteredLogs.setPredicate(log ->
                (log.getUrl() != null && log.getUrl().toLowerCase().contains(lower)) ||
                (log.getMethod() != null && log.getMethod().toLowerCase().contains(lower)) ||
                (log.getRequestParams() != null && log.getRequestParams().toLowerCase().contains(lower)) ||
                (log.getRequestBody() != null && log.getRequestBody().toLowerCase().contains(lower)) ||
                (log.getResponseSummary() != null && log.getResponseSummary().toLowerCase().contains(lower)) ||
                String.valueOf(log.getStatusCode()).contains(lower)
            );
        }
    }

    public void clear() {
        monitorService.clear();
    }

    public void toggleMonitoring() {
        monitoring.set(!monitoring.get());
    }
}
