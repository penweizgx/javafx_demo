package com.example.app.viewmodel;

import com.example.app.exception.ExceptionHandler;
import com.example.app.model.Employee;
import com.example.app.model.EmployeeListReq;
import com.example.app.model.PageResult;
import com.example.app.service.EmployeeManageService;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;

public class EmployeeListViewModel extends ViewModelBase {

    private final EmployeeManageService employeeService;

    @Getter
    private final ObservableList<Employee> employeeList = FXCollections.observableArrayList();

    @Getter
    private final StringProperty searchText = new SimpleStringProperty("");

    @Getter
    private final BooleanProperty loading = new SimpleBooleanProperty(false);

    @Getter
    private final StringProperty errorMessage = new SimpleStringProperty("");

    @Getter
    private final IntegerProperty currentPage = new SimpleIntegerProperty(1);

    @Getter
    private final IntegerProperty totalPages = new SimpleIntegerProperty(1);

    @Getter
    private final LongProperty totalCount = new SimpleLongProperty(0);

    private static final int DEFAULT_PAGE_SIZE = 10;

    public EmployeeListViewModel(EmployeeManageService employeeService) {
        this.employeeService = employeeService;
    }

    public BooleanProperty loadingProperty() { return loading; }
    public StringProperty errorMessageProperty() { return errorMessage; }
    public StringProperty searchTextProperty() { return searchText; }
    public IntegerProperty currentPageProperty() { return currentPage; }
    public IntegerProperty totalPagesProperty() { return totalPages; }
    public LongProperty totalCountProperty() { return totalCount; }

    public void loadEmployees() {
        loading.set(true);
        errorMessage.set("");

        EmployeeListReq req = new EmployeeListReq();
        req.setPageNum(currentPage.get());
        req.setPageSize(DEFAULT_PAGE_SIZE);
        String keyword = searchText.get().trim();
        if (!keyword.isEmpty()) {
            req.setName(keyword);
        }

        executeAsync(
            () -> employeeService.listEmployees(req).join(),
            this::onEmployeesLoaded,
            this::onLoadError
        );
    }

    private void onEmployeesLoaded(PageResult<Employee> result) {
        employeeList.setAll(result.getRecords());
        totalCount.set(result.getTotal() != null ? result.getTotal() : 0);
        int total = (int) ((result.getTotal() != null ? result.getTotal() : 0) / DEFAULT_PAGE_SIZE);
        if ((result.getTotal() != null ? result.getTotal() : 0) % DEFAULT_PAGE_SIZE > 0) total++;
        totalPages.set(Math.max(total, 1));
        loading.set(false);
    }

    private void onLoadError(Throwable error) {
        loading.set(false);
        errorMessage.set("加载员工列表失败: " + error.getMessage());
        ExceptionHandler.handle(error, "Failed to load employees");
    }

    public void nextPage() {
        if (currentPage.get() < totalPages.get()) {
            currentPage.set(currentPage.get() + 1);
            loadEmployees();
        }
    }

    public void previousPage() {
        if (currentPage.get() > 1) {
            currentPage.set(currentPage.get() - 1);
            loadEmployees();
        }
    }

    public void search() {
        currentPage.set(1);
        loadEmployees();
    }
}