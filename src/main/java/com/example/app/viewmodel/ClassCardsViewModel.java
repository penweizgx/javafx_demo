package com.example.app.viewmodel;

import com.example.app.exception.ExceptionHandler;
import com.example.app.model.*;
import com.example.app.service.StudentManageService;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ClassCardsViewModel extends ViewModelBase {

    private final StudentManageService studentService;

    @Getter
    private final ObservableList<ClazzWithCountVO> classList = FXCollections.observableArrayList();

    @Getter
    private final ObservableList<StudentVO> searchResults = FXCollections.observableArrayList();

    @Getter
    private final StringProperty searchText = new SimpleStringProperty("");

    @Getter
    private final BooleanProperty loading = new SimpleBooleanProperty(false);

    @Getter
    private final BooleanProperty searching = new SimpleBooleanProperty(false);

    @Getter
    private final StringProperty errorMessage = new SimpleStringProperty("");

    @Getter
    private final MapProperty<Long, Double> attendanceRate = new SimpleMapProperty<>(null, "attendanceRate", FXCollections.observableHashMap());

    @Getter
    private final IntegerProperty searchCurrentPage = new SimpleIntegerProperty(1);

    @Getter
    private final IntegerProperty searchTotalPages = new SimpleIntegerProperty(1);

    @Getter
    private final LongProperty searchTotalCount = new SimpleLongProperty(0);

    private static final int PAGE_SIZE = 10;

    public ClassCardsViewModel(StudentManageService studentService) {
        this.studentService = studentService;
    }

    public StringProperty searchTextProperty() { return searchText; }
    public BooleanProperty loadingProperty() { return loading; }
    public BooleanProperty searchingProperty() { return searching; }
    public StringProperty errorMessageProperty() { return errorMessage; }
    public IntegerProperty searchCurrentPageProperty() { return searchCurrentPage; }
    public IntegerProperty searchTotalPagesProperty() { return searchTotalPages; }
    public LongProperty searchTotalCountProperty() { return searchTotalCount; }

    public void loadData() {
        loading.set(true);
        errorMessage.set("");

        executeAsync(
            () -> {
                List<ClazzWithCountVO> classes = studentService.listClazzWithCount(null).join();
                String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                List<ClazzDayAttendVO> attendData = studentService.countClazzDay(today).join();
                Map<Long, Double> rateMap = attendData.stream()
                    .collect(Collectors.toMap(
                        ClazzDayAttendVO::getClazzId,
                        a -> a.getTotal() > 0 ? (double) a.getAttend() / a.getTotal() * 100 : 0.0
                    ));
                List<ClazzWithCountVO> finalClasses = classes;
                Map<Long, Double> finalRates = rateMap;
                return new Object() {
                    List<ClazzWithCountVO> clazzData = finalClasses;
                    Map<Long, Double> rateData = finalRates;
                };
            },
            result -> {
                classList.setAll(result.clazzData);
                attendanceRate.putAll(result.rateData);
                loading.set(false);
            },
            this::onLoadError
        );
    }

    public void search() {
        String keyword = searchText.get().trim();
        if (keyword.isEmpty()) {
            searching.set(false);
            return;
        }

        searching.set(true);
        searchCurrentPage.set(1);
        doSearch();
    }

    public void clearSearch() {
        searchText.set("");
        searching.set(false);
        searchResults.clear();
    }

    private void doSearch() {
        loading.set(true);

        StudentQO qo = new StudentQO();
        qo.setKeyword(searchText.get().trim());

        executeAsync(
            () -> studentService.listByCondition(qo).join(),
            result -> {
                searchResults.setAll(result);
                searchTotalCount.set(result.size());
                int total = (result.size() + PAGE_SIZE - 1) / PAGE_SIZE;
                searchTotalPages.set(Math.max(total, 1));
                loading.set(false);
            },
            this::onLoadError
        );
    }

    public void nextSearchPage() {
        if (searchCurrentPage.get() < searchTotalPages.get()) {
            searchCurrentPage.set(searchCurrentPage.get() + 1);
        }
    }

    public void previousSearchPage() {
        if (searchCurrentPage.get() > 1) {
            searchCurrentPage.set(searchCurrentPage.get() - 1);
        }
    }

    private void onLoadError(Throwable error) {
        loading.set(false);
        errorMessage.set("加载失败: " + error.getMessage());
        ExceptionHandler.handle(error, "Failed to load class cards data");
    }

    public List<StudentVO> getCurrentSearchPage() {
        int from = (searchCurrentPage.get() - 1) * PAGE_SIZE;
        int to = Math.min(from + PAGE_SIZE, searchResults.size());
        if (from >= searchResults.size()) return List.of();
        return searchResults.subList(from, to);
    }
}
