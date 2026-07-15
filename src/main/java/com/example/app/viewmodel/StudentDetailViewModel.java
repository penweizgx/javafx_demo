package com.example.app.viewmodel;

import com.example.app.exception.ExceptionHandler;
import com.example.app.model.*;
import com.example.app.service.StudentManageService;
import javafx.beans.property.*;
import lombok.Getter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class StudentDetailViewModel extends ViewModelBase {

    private final StudentManageService studentService;

    private StudentVO student;

    @Getter private final BooleanProperty loading = new SimpleBooleanProperty(false);
    @Getter private final StringProperty errorMessage = new SimpleStringProperty("");
    @Getter private final BooleanProperty editing = new SimpleBooleanProperty(false);

    @Getter private final StringProperty name = new SimpleStringProperty();
    @Getter private final StringProperty gender = new SimpleStringProperty();
    @Getter private final StringProperty birthday = new SimpleStringProperty();
    @Getter private final StringProperty phone = new SimpleStringProperty();
    @Getter private final StringProperty address = new SimpleStringProperty();
    @Getter private final StringProperty clazzName = new SimpleStringProperty();
    @Getter private final StringProperty status = new SimpleStringProperty();
    @Getter private final StringProperty parentName = new SimpleStringProperty();
    @Getter private final StringProperty parentPhone = new SimpleStringProperty();
    @Getter private final StringProperty parentRelation = new SimpleStringProperty();

    @Getter private final StringProperty attendDays = new SimpleStringProperty();
    @Getter private final StringProperty leaveDays = new SimpleStringProperty();
    @Getter private final StringProperty absentDays = new SimpleStringProperty();

    @Getter private final StringProperty balance = new SimpleStringProperty();
    @Getter private final StringProperty classHistory = new SimpleStringProperty();

    private StudentFO editBuffer;

    public StudentDetailViewModel(StudentManageService studentService) {
        this.studentService = studentService;
    }

    public BooleanProperty loadingProperty() { return loading; }
    public StringProperty errorMessageProperty() { return errorMessage; }
    public BooleanProperty editingProperty() { return editing; }

    public void loadDetail(Long studentId) {
        loading.set(true);
        errorMessage.set("");

        executeAsync(
            () -> {
                StudentVO detail = studentService.detail(studentId).join();
                String month = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
                ClazzStudentMonthAttendVO attendResult = null;
                FinAccountVO accountResult = null;
                try {
                    attendResult = studentService.listMonthAttend(detail.getClazzId(), month).join();
                } catch (Exception e) {
                    ExceptionHandler.handle(e, "Failed to load attendance");
                }
                try {
                    accountResult = studentService.financeAccount(studentId).join();
                } catch (Exception e) {
                    ExceptionHandler.handle(e, "Failed to load finance");
                }
                final ClazzStudentMonthAttendVO attend = attendResult;
                final FinAccountVO account = accountResult;
                return new Object() {
                    StudentVO s = detail;
                    ClazzStudentMonthAttendVO a = attend;
                    FinAccountVO f = account;
                };
            },
            result -> {
                this.student = result.s;
                populateViewData(result.s, result.a, result.f);
                loading.set(false);
            },
            this::onLoadError
        );
    }

    private void populateViewData(StudentVO s, ClazzStudentMonthAttendVO attend, FinAccountVO account) {
        name.set(s.getName() != null ? s.getName() : "");
        gender.set(s.getSex() != null ? (s.getSex() == 1 ? "男" : "女") : "");
        birthday.set(s.getBornDate() != null ? s.getBornDate() : "");
        phone.set(s.getPhone() != null ? s.getPhone() : "");
        address.set(s.getAddress() != null ? s.getAddress() : "");
        clazzName.set(s.getClazzname() != null ? s.getClazzname() : "");
        status.set(s.getStatus() != null ? getStatusText(s.getStatus()) : "");

        if (s.getParents() != null && !s.getParents().isEmpty()) {
            ParentVO p = s.getParents().get(0);
            parentName.set(p.getName() != null ? p.getName() : "");
            parentPhone.set(p.getPhone() != null ? p.getPhone() : "");
            parentRelation.set(p.getRelationship() != null ? getRelationText(p.getRelationship()) : "");
        }

        if (attend != null && attend.getStudents() != null) {
            for (StudentMonthAttend sma : attend.getStudents()) {
                if (sma.getStudentId().equals(s.getId())) {
                    attendDays.set(sma.getAttendDays() != null ? sma.getAttendDays() + "天" : "-");
                    leaveDays.set(sma.getLeaveDays() != null ? sma.getLeaveDays() + "天" : "-");
                    absentDays.set(sma.getAbsentDays() != null ? sma.getAbsentDays() + "天" : "-");
                    break;
                }
            }
        }

        if (account != null) {
            balance.set(account.getBalancesAmount() != null ? "¥" + account.getBalancesAmount() : "-");
        }

        if (s.getClazzs() != null && !s.getClazzs().isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (StudentClazzHistoryVO h : s.getClazzs()) {
                if (h.getPeriod() != null) {
                    sb.append(h.getPeriod().getStart()).append("  ")
                      .append(h.getClazzName() != null ? h.getClazzName() : "").append("\n");
                }
            }
            classHistory.set(sb.toString().trim());
        }
    }

    public void startEdit() {
        if (student == null) return;
        editBuffer = new StudentFO();
        editBuffer.setId(student.getId());
        editBuffer.setName(student.getName());
        editBuffer.setSex(student.getSex());
        editBuffer.setBornDate(student.getBornDate());
        editBuffer.setPhone(student.getPhone());
        editBuffer.setAddress(student.getAddress());
        if (student.getParents() != null && !student.getParents().isEmpty()) {
            ParentVO p = student.getParents().get(0);
            editBuffer.setPid(p.getId());
            editBuffer.setPname(p.getName());
            editBuffer.setPrelationship(p.getRelationship());
        }
        editing.set(true);
    }

    public void cancelEdit() {
        if (student != null) {
            populateViewData(student, null, null);
        }
        editBuffer = null;
        editing.set(false);
    }

    public void saveEdit() {
        if (editBuffer == null) return;
        loading.set(true);

        executeAsync(
            () -> {
                studentService.change(editBuffer).join();
                return null;
            },
            result -> {
                editBuffer = null;
                editing.set(false);
                loadDetail(student.getId());
            },
            this::onLoadError
        );
    }

    public StudentFO getEditBuffer() {
        return editBuffer;
    }

    private void onLoadError(Throwable error) {
        loading.set(false);
        errorMessage.set("操作失败: " + error.getMessage());
        ExceptionHandler.handle(error, "Student detail operation failed");
    }

    private String getStatusText(String status) {
        if (status == null) return "-";
        return switch (status) {
            case "REGISTER" -> "已登记";
            case "INSCHOOL" -> "在读";
            case "PAUSE" -> "休学";
            case "LEAVE" -> "离校";
            default -> status;
        };
    }

    private String getRelationText(int relationship) {
        return switch (relationship) {
            case 1 -> "父亲";
            case 2 -> "母亲";
            case 3 -> "祖父";
            case 4 -> "祖母";
            default -> "其他";
        };
    }
}
