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

public class FinanceAccountListViewModel extends ViewModelBase {

    private final FinanceManageService financeService;
    private final StudentManageService studentService;

    @Getter
    private final ObservableList<FinAccountVO> accountList = FXCollections.observableArrayList();

    @Getter
    private final BooleanProperty loading = new SimpleBooleanProperty(false);

    @Getter
    private final StringProperty errorMessage = new SimpleStringProperty("");

    @Getter
    private final ObjectProperty<FinanceBrief> brief = new SimpleObjectProperty<>();

    @Getter
    private final ObjectProperty<ClazzOptionVO> selectedClazz = new SimpleObjectProperty<>();

    @Getter
    private final StringProperty searchKeyword = new SimpleStringProperty("");

    @Getter
    private final ObservableList<ClazzOptionVO> clazzOptions = FXCollections.observableArrayList();

    private Long schId;

    public FinanceAccountListViewModel(FinanceManageService financeService, StudentManageService studentService) {
        this.financeService = financeService;
        this.studentService = studentService;
        loadSchId();
    }

    public BooleanProperty loadingProperty() { return loading; }
    public StringProperty errorMessageProperty() { return errorMessage; }
    public ObjectProperty<FinanceBrief> briefProperty() { return brief; }
    public ObjectProperty<ClazzOptionVO> selectedClazzProperty() { return selectedClazz; }
    public StringProperty searchKeywordProperty() { return searchKeyword; }

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

    public void loadBrief() {
        if (schId == null) return;
        executeAsync(
            () -> financeService.countBrief(schId).join(),
            result -> brief.set(result),
            error -> ExceptionHandler.handle(error, "Failed to load finance brief")
        );
    }

    public void loadAccounts() {
        loading.set(true);
        errorMessage.set("");

        StudentQO qo = new StudentQO();
        qo.setSchId(schId);
        ClazzOptionVO sel = selectedClazz.get();
        if (sel != null) {
            qo.setClazzId(sel.getId());
        }
        String keyword = searchKeyword.get();
        if (keyword != null && !keyword.trim().isEmpty()) {
            qo.setKeyword(keyword.trim());
        }

        executeAsync(
            () -> financeService.listByCondition(qo).join(),
            this::onAccountsLoaded,
            this::onLoadError
        );
    }

    public void searchAccounts() {
        loadAccounts();
    }

    public void loadClazzOptions() {
        executeAsync(
            () -> studentService.listActiveClazz(schId).join(),
            result -> clazzOptions.setAll(result),
            error -> ExceptionHandler.handle(error, "Failed to load clazz options")
        );
    }

    private void onAccountsLoaded(List<FinAccountVO> accounts) {
        accountList.setAll(accounts);
        loading.set(false);
    }

    private void onLoadError(Throwable error) {
        loading.set(false);
        errorMessage.set("加载账户列表失败: " + error.getMessage());
        ExceptionHandler.handle(error, "Failed to load finance accounts");
    }

    public void refresh() {
        loadBrief();
        loadAccounts();
    }
}
