package com.example.app.viewmodel;

import com.example.app.exception.ExceptionHandler;
import com.example.app.model.StudentVO;
import com.example.app.service.StudentManageService;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;

import java.util.List;

public class StudentListViewModel extends ViewModelBase {

    private final StudentManageService studentService;

    @Getter
    private final ObservableList<StudentVO> studentList = FXCollections.observableArrayList();

    @Getter
    private final BooleanProperty loading = new SimpleBooleanProperty(false);

    @Getter
    private final StringProperty errorMessage = new SimpleStringProperty("");

    @Getter
    private final StringProperty className = new SimpleStringProperty("");

    private Long clazzId;

    public StudentListViewModel(StudentManageService studentService) {
        this.studentService = studentService;
    }

    public BooleanProperty loadingProperty() { return loading; }
    public StringProperty errorMessageProperty() { return errorMessage; }
    public StringProperty classNameProperty() { return className; }

    public void setClazzId(Long clazzId) {
        this.clazzId = clazzId;
    }

    public Long getClazzId() {
        return clazzId;
    }

    public void loadStudents() {
        if (clazzId == null) return;
        loading.set(true);
        errorMessage.set("");

        executeAsync(
            () -> studentService.listWithClazz(clazzId).join(),
            this::onStudentsLoaded,
            this::onLoadError
        );
    }

    private void onStudentsLoaded(List<StudentVO> students) {
        studentList.setAll(students);
        loading.set(false);
    }

    private void onLoadError(Throwable error) {
        loading.set(false);
        errorMessage.set("加载学生列表失败: " + error.getMessage());
        ExceptionHandler.handle(error, "Failed to load student list");
    }

    public void refresh() {
        loadStudents();
    }
}
