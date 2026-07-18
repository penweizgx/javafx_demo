package com.example.app.viewmodel;

import com.example.app.exception.ExceptionHandler;
import com.example.app.model.*;
import com.example.app.service.FinanceManageService;
import com.example.app.service.StudentManageService;
import com.example.app.service.UserService;
import com.example.app.AppContext;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;

import java.util.List;
import java.util.Map;

public class FinanceAttendViewModel extends ViewModelBase {

    private final FinanceManageService financeService;
    private final StudentManageService studentService;

    @Getter
    private final ObservableList<MonthAttendCarryOverVO> refundList = FXCollections.observableArrayList();

    @Getter
    private final BooleanProperty loading = new SimpleBooleanProperty(false);

    @Getter
    private final StringProperty errorMessage = new SimpleStringProperty("");

    @Getter
    private final StringProperty selectedMonth = new SimpleStringProperty("");

    @Getter
    private final ObjectProperty<ClazzOptionVO> selectedClazz = new SimpleObjectProperty<>();

    @Getter
    private final BooleanProperty onlyRefund = new SimpleBooleanProperty(false);

    @Getter
    private final ObservableList<ClazzOptionVO> clazzOptions = FXCollections.observableArrayList();

    @Getter
    private final StringProperty confirmMessage = new SimpleStringProperty("");

    private Long schId;

    public FinanceAttendViewModel(FinanceManageService financeService, StudentManageService studentService) {
        this.financeService = financeService;
        this.studentService = studentService;
        loadSchId();
    }

    public BooleanProperty loadingProperty() { return loading; }
    public StringProperty errorMessageProperty() { return errorMessage; }
    public StringProperty selectedMonthProperty() { return selectedMonth; }
    public ObjectProperty<ClazzOptionVO> selectedClazzProperty() { return selectedClazz; }
    public BooleanProperty onlyRefundProperty() { return onlyRefund; }
    public StringProperty confirmMessageProperty() { return confirmMessage; }

    private void loadSchId() {
        try {
            UserService userService = AppContext.get().getService(UserService.class);
            User user = userService.getCachedUser();
            if (user != null && user.getOrgBound() != null) {
                this.schId = Long.parseLong(user.getId());
            }
        } catch (Exception e) {
            ExceptionHandler.handle(e, "Failed to load schId");
        }
    }

    public void loadRefundList() {
        String month = selectedMonth.get();
        if (month == null || month.trim().isEmpty()) {
            errorMessage.set("请输入月份（格式：yyyy-MM）");
            return;
        }
        if (schId == null) {
            errorMessage.set("无法获取学校信息");
            return;
        }

        loading.set(true);
        errorMessage.set("");
        confirmMessage.set("");

        final Long clazzId = selectedClazz.get() != null ? selectedClazz.get().getId() : null;
        final boolean onlyR = onlyRefund.get();
        final String monthStr = month.trim();

        executeAsync(
            () -> financeService.listMonthAttendCarryOver(schId, clazzId, monthStr, onlyR).join(),
            this::onRefundListLoaded,
            this::onLoadError
        );
    }

    public void loadClazzOptions() {
        executeAsync(
            () -> studentService.listActiveClazz(null).join(),
            result -> clazzOptions.setAll(result),
            error -> ExceptionHandler.handle(error, "Failed to load clazz options")
        );
    }

    public void confirmCarryOver(List<MonthAttendCarryOverVO> items) {
        String month = selectedMonth.get();
        if (month == null || month.trim().isEmpty() || items == null || items.isEmpty()) return;

        String[] parts = month.trim().split("-");
        int year = Integer.parseInt(parts[0]);
        int m = Integer.parseInt(parts[1]);

        loading.set(true);
        errorMessage.set("");

        executeAsync(
            () -> financeService.confirmMonthAttendCarryOver(year, m, items).join(),
            result -> {
                loading.set(false);
                StringBuilder sb = new StringBuilder("结转完成");
                if (result != null && !result.isEmpty()) {
                    sb.append(": ");
                    int i = 0;
                    for (Map.Entry<String, String> entry : result.entrySet()) {
                        if (i > 0) sb.append(", ");
                        sb.append(entry.getValue());
                        i++;
                    }
                }
                confirmMessage.set(sb.toString());
                loadRefundList();
            },
            error -> {
                loading.set(false);
                errorMessage.set("结转确认失败: " + error.getMessage());
                ExceptionHandler.handle(error, "Failed to confirm carry over");
            }
        );
    }

    private void onRefundListLoaded(List<MonthAttendCarryOverVO> result) {
        refundList.setAll(result != null ? result : java.util.Collections.emptyList());
        loading.set(false);
    }

    private void onLoadError(Throwable error) {
        loading.set(false);
        errorMessage.set("加载考勤退费列表失败: " + error.getMessage());
        ExceptionHandler.handle(error, "Failed to load attend refund list");
    }
}
