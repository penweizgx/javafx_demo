package com.example.app.viewmodel;

import com.example.app.AppContext;
import com.example.app.exception.ExceptionHandler;
import com.example.app.model.*;
import com.example.app.service.FinanceManageService;
import com.example.app.service.UserService;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class FinanceReportsViewModel extends ViewModelBase {

    private final FinanceManageService financeService;
    private Long schId;

    @Getter
    private final ObjectProperty<Map<String, ChargeReportItem>> chargeReport = new SimpleObjectProperty<>();

    @Getter
    private final ObservableList<ChargeSubjectReport> subjectReports = FXCollections.observableArrayList();

    @Getter
    private final ObservableList<CarryOverSubjectReport> carryOverReports = FXCollections.observableArrayList();

    @Getter
    private final ObservableList<PayChancelReport> payChancelReports = FXCollections.observableArrayList();

    @Getter
    private final BooleanProperty loading = new SimpleBooleanProperty(false);

    @Getter
    private final StringProperty errorMessage = new SimpleStringProperty("");

    @Getter
    private final StringProperty selectedMonth = new SimpleStringProperty("");

    public FinanceReportsViewModel(FinanceManageService financeService) {
        this.financeService = financeService;
        loadSchId();
    }

    public ObjectProperty<Map<String, ChargeReportItem>> chargeReportProperty() { return chargeReport; }
    public BooleanProperty loadingProperty() { return loading; }
    public StringProperty errorMessageProperty() { return errorMessage; }
    public StringProperty selectedMonthProperty() { return selectedMonth; }

    private void loadSchId() {
        try {
            UserService userService = AppContext.get().getService(UserService.class);
            User user = userService.getCachedUser();
            if (user != null && user.getId() != null) {
                this.schId = Long.parseLong(user.getId());
            }
        } catch (Exception e) {
            ExceptionHandler.handle(e, "Failed to load schId");
        }
    }

    public void loadReports(String month) {
        if (schId == null) {
            errorMessage.set("无法获取学校信息");
            return;
        }
        if (month == null || month.trim().isEmpty()) {
            errorMessage.set("请输入月份（格式：yyyy-MM）");
            return;
        }

        loading.set(true);
        errorMessage.set("");
        final String monthStr = month.trim();

        executeAsync(
            () -> financeService.countChargeReport(schId, monthStr).join(),
            result -> {
                chargeReport.set(result);
                loadSubjectReports(monthStr);
            },
            this::onLoadError
        );
    }

    private void loadSubjectReports(String month) {
        executeAsync(
            () -> financeService.sumChargeSubjectReport(schId, month).join(),
            result -> {
                subjectReports.setAll(result != null ? result : Collections.emptyList());
                loadCarryOverReports(month);
            },
            this::onLoadError
        );
    }

    private void loadCarryOverReports(String month) {
        executeAsync(
            () -> financeService.sumCarryOverSubjectReport(schId, month).join(),
            result -> {
                carryOverReports.setAll(result != null ? result : Collections.emptyList());
                loadPayChancelReports(month);
            },
            this::onLoadError
        );
    }

    private void loadPayChancelReports(String month) {
        executeAsync(
            () -> financeService.countPayChancelReport(schId, month).join(),
            result -> {
                payChancelReports.setAll(result != null ? result : Collections.emptyList());
                loading.set(false);
            },
            this::onLoadError
        );
    }

    private void onLoadError(Throwable error) {
        loading.set(false);
        errorMessage.set("加载报表失败: " + error.getMessage());
        ExceptionHandler.handle(error, "Failed to load reports");
    }
}
