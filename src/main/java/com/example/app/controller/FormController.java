package com.example.app.controller;

import com.example.app.AppContext;
import com.example.app.component.FormItem;
import com.example.app.executor.AsyncExecutor;
import com.example.app.i18n.I18nService;
import com.example.app.service.LoadingService;
import com.example.app.service.ToastService;
import com.example.app.validator.FieldValidator;
import com.example.app.validator.FormValidator;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.util.regex.Pattern;

public class FormController {

    @FXML
    private VBox root;
    @FXML
    private VBox formArea;
    @FXML
    private Button btnSave;

    @FXML
    private FormItem nameItem;
    @FXML
    private FormItem studentIdItem;
    @FXML
    private FormItem ageItem;
    @FXML
    private FormItem emailItem;
    @FXML
    private FormItem phoneItem;

    private LoadingService loading;
    private ToastService toast;
    private I18nService i18n;
    private FormValidator formValidator;

    public FormController() {
    }

    @FXML
    public void initialize() {
        this.loading = AppContext.get().getService(LoadingService.class);
        this.toast = AppContext.get().getService(ToastService.class);
        this.i18n = AppContext.get().getService(I18nService.class);

        nameItem.setValue("张三");
        studentIdItem.setValue("20240001");
        ageItem.setValue("18");
        emailItem.setValue("zhangsan@example.com");
        phoneItem.setValue("13800138000");

        setupValidators();
        btnSave.setOnAction(e -> onSave());
    }

    private void setupValidators() {
        FieldValidator nameVal = new FieldValidator(nameItem::getValue, v -> {
            if (v == null || v.isBlank()) return getI18nText("form.error.required");
            if (v.length() < 2 || v.length() > 30) return getI18nText("form.error.name.length");
            return null;
        });
        FieldValidator studentIdVal = new FieldValidator(studentIdItem::getValue, v -> {
            if (v == null || v.isBlank()) return getI18nText("form.error.required");
            if (!Pattern.compile("^[A-Za-z0-9_-]{4,20}$").matcher(v).matches()) return getI18nText("form.error.studentId.format");
            return null;
        });
        FieldValidator ageVal = new FieldValidator(ageItem::getValue, v -> {
            if (v == null || v.isBlank()) return getI18nText("form.error.required");
            try {
                int age = Integer.parseInt(v);
                if (age < 1 || age > 120) return getI18nText("form.error.age.range");
            } catch (Exception e) {
                return getI18nText("form.error.age.integer");
            }
            return null;
        });
        FieldValidator emailVal = new FieldValidator(emailItem::getValue, v -> {
            if (v == null || v.isBlank()) return getI18nText("form.error.required");
            if (!Pattern.compile("^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,}$").matcher(v).matches()) return getI18nText("form.error.email.format");
            return null;
        });
        FieldValidator phoneVal = new FieldValidator(phoneItem::getValue, v -> {
            if (v == null || v.isBlank()) return getI18nText("form.error.required");
            if (!Pattern.compile("^\\d{7,15}$").matcher(v).matches()) return getI18nText("form.error.phone.format");
            return null;
        });

        formValidator = new FormValidator();
        formValidator.addField(nameVal, nameItem);
        formValidator.addField(studentIdVal, studentIdItem);
        formValidator.addField(ageVal, ageItem);
        formValidator.addField(emailVal, emailItem);
        formValidator.addField(phoneVal, phoneItem);
    }

    private String getI18nText(String key) {
        return i18n != null ? i18n.getString(key) : key;
    }

    private void onSave() {
        boolean ok = formValidator.validateAll();
        if (!ok) {
            toast.show(getI18nText("form.validation.failed"));
            return;
        }

        loading.show();
        AsyncExecutor.runAsync(() -> {
            try {
                Thread.sleep(900);
            } catch (InterruptedException ignored) {
            }
        }).thenRun(() -> Platform.runLater(() -> {
            loading.hide();
            String name = nameItem.getValue().trim();
            String studentId = studentIdItem.getValue().trim();
            toast.show(getI18nText("form.save.success"));
        }));
    }
}