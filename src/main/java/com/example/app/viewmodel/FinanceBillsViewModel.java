package com.example.app.viewmodel;

import com.example.app.exception.ExceptionHandler;
import com.example.app.model.*;
import com.example.app.service.FinanceManageService;
import com.example.app.service.UserService;
import com.example.app.AppContext;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;

public class FinanceBillsViewModel extends ViewModelBase {

    private final FinanceManageService financeService;

    @Getter
    private final ObservableList<BillTimeDTO> billList = FXCollections.observableArrayList();

    @Getter
    private final BooleanProperty loading = new SimpleBooleanProperty(false);

    @Getter
    private final StringProperty errorMessage = new SimpleStringProperty("");

    @Getter
    private final IntegerProperty currentPage = new SimpleIntegerProperty(1);

    @Getter
    private final IntegerProperty totalPages = new SimpleIntegerProperty(1);

    @Getter
    private final LongProperty totalRecords = new SimpleLongProperty(0);

    @Getter
    private final ObservableList<ClazzOptionVO> clazzOptions = FXCollections.observableArrayList();

    @Getter
    private final ObjectProperty<ClazzOptionVO> selectedClazz = new SimpleObjectProperty<>();

    @Getter
    private final StringProperty searchWord = new SimpleStringProperty("");

    @Getter
    private final StringProperty billType = new SimpleStringProperty("");

    @Getter
    private final BooleanProperty filterInvalid = new SimpleBooleanProperty(false);

    @Getter
    private final StringProperty startDate = new SimpleStringProperty("");

    @Getter
    private final StringProperty endDate = new SimpleStringProperty("");

    private Long schId;
    private static final int PAGE_SIZE = 20;

    public FinanceBillsViewModel(FinanceManageService financeService) {
        this.financeService = financeService;
        loadSchId();
    }

    public BooleanProperty loadingProperty() { return loading; }
    public StringProperty errorMessageProperty() { return errorMessage; }
    public IntegerProperty currentPageProperty() { return currentPage; }
    public IntegerProperty totalPagesProperty() { return totalPages; }
    public LongProperty totalRecordsProperty() { return totalRecords; }
    public ObjectProperty<ClazzOptionVO> selectedClazzProperty() { return selectedClazz; }
    public StringProperty searchWordProperty() { return searchWord; }
    public StringProperty billTypeProperty() { return billType; }
    public BooleanProperty filterInvalidProperty() { return filterInvalid; }
    public StringProperty startDateProperty() { return startDate; }
    public StringProperty endDateProperty() { return endDate; }

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

    public void loadBills() {
        loadBills(currentPage.get());
    }

    public void loadBills(int page) {
        loading.set(true);
        errorMessage.set("");

        FilterBillsPage filter = new FilterBillsPage();
        filter.setCurrent(page);
        filter.setPageSize(PAGE_SIZE);
        filter.setSchId(schId);

        String keyword = searchWord.get();
        if (keyword != null && !keyword.trim().isEmpty()) {
            filter.setSearchWord(keyword.trim());
        }

        ClazzOptionVO sel = selectedClazz.get();
        if (sel != null) {
            filter.setClazzId(sel.getId());
        }

        String type = billType.get();
        if (type != null && !type.trim().isEmpty() && !"ALL".equals(type)) {
            filter.setBillType(type);
        }

        filter.setFilterInvalid(filterInvalid.get());

        String start = startDate.get();
        String end = endDate.get();
        if ((start != null && !start.isEmpty()) || (end != null && !end.isEmpty())) {
            DatePeriod period = new DatePeriod();
            period.setStart(start != null ? start : "");
            period.setEnd(end != null ? end : "");
            filter.setPeriod(period);
        }

        executeAsync(
            () -> financeService.listBills(filter).join(),
            this::onBillsLoaded,
            this::onLoadError
        );
    }

    public void nextPage() {
        if (currentPage.get() < totalPages.get()) {
            loadBills(currentPage.get() + 1);
        }
    }

    public void prevPage() {
        if (currentPage.get() > 1) {
            loadBills(currentPage.get() - 1);
        }
    }

    public void loadClazzOptions() {
        executeAsync(
            () -> AppContext.get().getService(com.example.app.service.StudentManageService.class).listActiveClazz(null).join(),
            result -> clazzOptions.setAll(result),
            error -> ExceptionHandler.handle(error, "Failed to load clazz options")
        );
    }

    private void onBillsLoaded(PaginationBillTimeDTO result) {
        if (result != null) {
            billList.setAll(result.getContent() != null ? result.getContent() : java.util.Collections.emptyList());
            currentPage.set(result.getCurrent() != null ? result.getCurrent() : 1);
            totalPages.set(result.getPages() != null ? result.getPages().intValue() : 1);
            totalRecords.set(result.getTotal() != null ? result.getTotal() : 0);
        }
        loading.set(false);
    }

    private void onLoadError(Throwable error) {
        loading.set(false);
        errorMessage.set("加载账单列表失败: " + error.getMessage());
        ExceptionHandler.handle(error, "Failed to load bills");
    }
}
