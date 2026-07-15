package com.example.app.controller;

import atlantafx.base.theme.Styles;
import com.example.app.AppContext;
import com.example.app.i18n.I18nService;
import com.example.app.i18n.LocaleChangeEvent;
import com.example.app.model.StudentVO;
import com.example.app.navigation.EventBus;
import com.example.app.navigation.NavigationClickEvent;
import com.example.app.navigation.ParamReceiver;
import com.example.app.navigation.RouteParams;
import com.example.app.service.StudentManageService;
import com.example.app.viewmodel.StudentDetailViewModel;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public class StudentDetailController implements ParamReceiver {

    @FXML private VBox root;
    @FXML private Label titleLabel;
    @FXML private Button backBtn;
    @FXML private Button editBtn;
    @FXML private Button saveBtn;
    @FXML private Button cancelBtn;
    @FXML private ProgressIndicator loadingIndicator;
    @FXML private Label errorLabel;

    @FXML private Label nameValue;
    @FXML private Label genderValue;
    @FXML private Label birthdayValue;
    @FXML private Label phoneValue;
    @FXML private Label addressValue;
    @FXML private Label clazzValue;
    @FXML private Label statusValue;
    @FXML private Label parentValue;
    @FXML private Label attendDaysValue;
    @FXML private Label leaveDaysValue;
    @FXML private Label absentDaysValue;
    @FXML private Label balanceValue;
    @FXML private Label historyValue;

    @FXML private TextField nameInput;
    @FXML private ComboBox<String> genderInput;
    @FXML private DatePicker birthdayInput;
    @FXML private TextField phoneInput;
    @FXML private TextField addressInput;
    @FXML private TextField parentNameInput;
    @FXML private TextField parentPhoneInput;
    @FXML private ComboBox<String> parentRelationInput;

    private StudentDetailViewModel viewModel;
    private I18nService i18n;

    private final Consumer<LocaleChangeEvent> localeHandler = e -> refreshI18n();

    private Long studentId;

    @FXML
    public void initialize() {
        StudentManageService studentService = AppContext.get().getService(StudentManageService.class);
        this.viewModel = new StudentDetailViewModel(studentService);
        try {
            this.i18n = AppContext.get().getService(I18nService.class);
        } catch (Exception ignored) {}

        setupGenderCombo();
        setupRelationCombo();
        setupBindings();
        setupEventHandlers();
        subscribeEvents();
    }

    private void setupGenderCombo() {
        genderInput.getItems().addAll("男", "女");
    }

    private void setupRelationCombo() {
        parentRelationInput.getItems().addAll("父亲", "母亲", "祖父", "祖母", "其他");
    }

    @Override
    public void receiveParams(RouteParams params) {
        Object idObj = params.getData().get("id");
        if (idObj instanceof Long id) {
            studentId = id;
        } else if (idObj instanceof String idStr) {
            studentId = Long.parseLong(idStr);
        }
        if (studentId != null) {
            viewModel.loadDetail(studentId);
        }
    }

    private void setupBindings() {
        loadingIndicator.visibleProperty().bind(viewModel.loadingProperty());
        errorLabel.textProperty().bind(viewModel.errorMessageProperty());
        errorLabel.visibleProperty().bind(Bindings.isNotEmpty(viewModel.errorMessageProperty()));

        titleLabel.textProperty().bind(Bindings.createStringBinding(
            () -> {
                String name = viewModel.getName().get();
                return name != null && !name.isEmpty() ? "学生详情 - " + name : "学生详情";
            },
            viewModel.getName()
        ));

        nameValue.textProperty().bind(viewModel.getName());
        genderValue.textProperty().bind(viewModel.getGender());
        birthdayValue.textProperty().bind(viewModel.getBirthday());
        phoneValue.textProperty().bind(viewModel.getPhone());
        addressValue.textProperty().bind(viewModel.getAddress());
        clazzValue.textProperty().bind(viewModel.getClazzName());
        statusValue.textProperty().bind(viewModel.getStatus());
        parentValue.textProperty().bind(Bindings.createStringBinding(
            () -> {
                String n = viewModel.getParentName().get();
                String r = viewModel.getParentRelation().get();
                String p = viewModel.getParentPhone().get();
                if (n.isEmpty() && p.isEmpty()) return "";
                return n + " (" + r + ") " + p;
            },
            viewModel.getParentName(), viewModel.getParentRelation(), viewModel.getParentPhone()
        ));
        attendDaysValue.textProperty().bind(viewModel.getAttendDays());
        leaveDaysValue.textProperty().bind(viewModel.getLeaveDays());
        absentDaysValue.textProperty().bind(viewModel.getAbsentDays());
        balanceValue.textProperty().bind(viewModel.getBalance());
        historyValue.textProperty().bind(viewModel.getClassHistory());

        viewModel.editingProperty().addListener((obs, old, editing) -> {
            boolean showView = !editing;
            boolean showEdit = editing;

            toggleViewEdit(showView, showEdit);

            if (editing && viewModel.getEditBuffer() != null) {
                nameInput.setText(viewModel.getEditBuffer().getName());
                genderInput.setValue(viewModel.getGender().get());
                phoneInput.setText(viewModel.getEditBuffer().getPhone());
                addressInput.setText(viewModel.getEditBuffer().getAddress());
                parentNameInput.setText(viewModel.getEditBuffer().getPname());
                parentPhoneInput.setText("");
            }
        });
    }

    private void toggleViewEdit(boolean showView, boolean showEdit) {
        nameValue.setVisible(showView); nameValue.setManaged(showView);
        genderValue.setVisible(showView); genderValue.setManaged(showView);
        birthdayValue.setVisible(showView); birthdayValue.setManaged(showView);
        phoneValue.setVisible(showView); phoneValue.setManaged(showView);
        addressValue.setVisible(showView); addressValue.setManaged(showView);

        nameInput.setVisible(showEdit); nameInput.setManaged(showEdit);
        genderInput.setVisible(showEdit); genderInput.setManaged(showEdit);
        birthdayInput.setVisible(showEdit); birthdayInput.setManaged(showEdit);
        phoneInput.setVisible(showEdit); phoneInput.setManaged(showEdit);
        addressInput.setVisible(showEdit); addressInput.setManaged(showEdit);
        parentNameInput.setVisible(showEdit); parentNameInput.setManaged(showEdit);
        parentPhoneInput.setVisible(showEdit); parentPhoneInput.setManaged(showEdit);
        parentRelationInput.setVisible(showEdit); parentRelationInput.setManaged(showEdit);

        editBtn.setVisible(showView); editBtn.setManaged(showView);
        saveBtn.setVisible(showEdit); saveBtn.setManaged(showEdit);
        cancelBtn.setVisible(showEdit); cancelBtn.setManaged(showEdit);
    }

    private void setupEventHandlers() {
        if (backBtn != null) {
            backBtn.setOnAction(e -> navigateBack());
        }
        if (editBtn != null) {
            editBtn.setOnAction(e -> viewModel.startEdit());
        }
        if (saveBtn != null) {
            saveBtn.setOnAction(e -> {
                if (viewModel.getEditBuffer() != null) {
                    viewModel.getEditBuffer().setName(nameInput.getText());
                    viewModel.getEditBuffer().setSex(genderInput.getValue() != null
                            ? (genderInput.getValue().equals("男") ? 1 : 2) : null);
                    viewModel.getEditBuffer().setPhone(phoneInput.getText());
                    viewModel.getEditBuffer().setAddress(addressInput.getText());
                    viewModel.getEditBuffer().setPname(parentNameInput.getText());
                }
                viewModel.saveEdit();
            });
        }
        if (cancelBtn != null) {
            cancelBtn.setOnAction(e -> viewModel.cancelEdit());
        }
    }

    private void navigateBack() {
        EventBus.getInstance().publish(new NavigationClickEvent(null, "/student/classes", RouteParams.empty()));
    }

    private void subscribeEvents() {
        EventBus.getInstance().subscribe(LocaleChangeEvent.class, localeHandler);
    }

    private void refreshI18n() {
        if (i18n == null) return;
        backBtn.setText(i18n.getString("student.back"));
        editBtn.setText(i18n.getString("student.edit"));
        saveBtn.setText(i18n.getString("student.save"));
        cancelBtn.setText(i18n.getString("student.cancel"));
    }

    public void cleanup() {
        EventBus.getInstance().unsubscribe(LocaleChangeEvent.class, localeHandler);
    }
}
