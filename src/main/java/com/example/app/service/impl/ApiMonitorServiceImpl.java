package com.example.app.service.impl;

import com.example.app.model.ApiRequestLog;
import com.example.app.service.ApiMonitorService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ApiMonitorServiceImpl implements ApiMonitorService {

    private static final int MAX_LOG_SIZE = 1000;
    private final ObservableList<ApiRequestLog> logs = FXCollections.observableArrayList();
    private volatile boolean monitoring = true;
    private long idCounter = 0;

    @Override
    public ObservableList<ApiRequestLog> getLogs() {
        return logs;
    }

    @Override
    public void addLog(ApiRequestLog log) {
        if (!monitoring) return;
        log.setId(++idCounter);
        if (Platform.isFxApplicationThread()) {
            doAdd(log);
        } else {
            Platform.runLater(() -> doAdd(log));
        }
    }

    private void doAdd(ApiRequestLog log) {
        logs.add(0, log);
        while (logs.size() > MAX_LOG_SIZE) {
            logs.remove(logs.size() - 1);
        }
    }

    @Override
    public void clear() {
        logs.clear();
    }

    @Override
    public boolean isMonitoring() {
        return monitoring;
    }

    @Override
    public void setMonitoring(boolean enabled) {
        this.monitoring = enabled;
    }
}
