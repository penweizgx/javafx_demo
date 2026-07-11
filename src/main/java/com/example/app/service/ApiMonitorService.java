package com.example.app.service;

import com.example.app.model.ApiRequestLog;
import javafx.collections.ObservableList;

public interface ApiMonitorService {
    ObservableList<ApiRequestLog> getLogs();
    void addLog(ApiRequestLog log);
    void clear();
    boolean isMonitoring();
    void setMonitoring(boolean enabled);
}
