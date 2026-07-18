package com.example.app.viewmodel;

import com.example.app.exception.ExceptionHandler;
import com.example.app.model.SubjectWithFeeScaleDTO;
import com.example.app.service.FinanceManageService;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

public class FinanceSubjectViewModel extends ViewModelBase {

    private final FinanceManageService financeService;

    @Getter
    private final ObservableList<SubjectWithFeeScaleDTO> subjectList = FXCollections.observableArrayList();

    @Getter
    private final BooleanProperty loading = new SimpleBooleanProperty(false);

    @Getter
    private final StringProperty errorMessage = new SimpleStringProperty("");

    @Getter
    private final StringProperty typeFilter = new SimpleStringProperty("ALL");

    private List<SubjectWithFeeScaleDTO> allSubjects;

    public FinanceSubjectViewModel(FinanceManageService financeService) {
        this.financeService = financeService;
    }

    public BooleanProperty loadingProperty() { return loading; }
    public StringProperty errorMessageProperty() { return errorMessage; }
    public StringProperty typeFilterProperty() { return typeFilter; }

    public void loadSubjects() {
        loading.set(true);
        errorMessage.set("");

        executeAsync(
            () -> financeService.listSubjectWithFeeScale().join(),
            this::onSubjectsLoaded,
            this::onLoadError
        );
    }

    public void filterByType(String type) {
        typeFilter.set(type);
        applyFilter();
    }

    private void applyFilter() {
        if (allSubjects == null) return;
        String filter = typeFilter.get();
        if ("ALL".equals(filter)) {
            subjectList.setAll(allSubjects);
        } else {
            subjectList.setAll(allSubjects.stream()
                .filter(s -> filter.equals(s.getType()))
                .collect(Collectors.toList()));
        }
    }

    private void onSubjectsLoaded(List<SubjectWithFeeScaleDTO> result) {
        allSubjects = result != null ? result : java.util.Collections.emptyList();
        applyFilter();
        loading.set(false);
    }

    private void onLoadError(Throwable error) {
        loading.set(false);
        errorMessage.set("加载项目列表失败: " + error.getMessage());
        ExceptionHandler.handle(error, "Failed to load subjects");
    }

    public void refresh() {
        loadSubjects();
    }
}
