package com.example.app.controller;

import com.example.app.ViewManager;
import com.example.app.component.FormItem;
import com.example.app.model.Student;
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

    @FXML private VBox root;
    @FXML private VBox formArea;
    @FXML private Button btnSave;

    @FXML private FormItem nameItem;
    @FXML private FormItem studentIdItem;
    @FXML private FormItem ageItem;
    @FXML private FormItem emailItem;
    @FXML private FormItem phoneItem;

    // get services from AppContext or ViewManager (here we'll fetch from AppContext)
    private LoadingService loading;
    private ToastService toast;

    private FormValidator formValidator;

    public FormController() {
        // no-arg constructor (injected by FXMLLoader); services will be fetched in initialize
    }

    @FXML
    public void initialize() {
        // fetch services
        this.loading = ViewManager.class.getModule() == null ? null : com.example.app.AppContext.get().getService(LoadingService.class);
        this.toast = com.example.app.AppContext.get().getService(ToastService.class);

        // initialize labels & sample values (components declared in FXML)


        nameItem.setValue("张三");
        studentIdItem.setValue("20240001");
        ageItem.setValue("18");
        emailItem.setValue("zhangsan@example.com");
        phoneItem.setValue("13800138000");

        // validators
        FieldValidator nameVal = new FieldValidator(nameItem::getValue, v -> {
            if (v == null || v.isBlank()) return "必填";
            if (v.length() < 2 || v.length() > 30) return "姓名长度需在2-30字符";
            return null;
        });
        FieldValidator studentIdVal = new FieldValidator(studentIdItem::getValue, v -> {
            if (v == null || v.isBlank()) return "必填";
            if (!Pattern.compile("^[A-Za-z0-9_-]{4,20}$").matcher(v).matches()) return "学号需为4-20位字母/数字/下划线";
            return null;
        });
        FieldValidator ageVal = new FieldValidator(ageItem::getValue, v -> {
            if (v == null || v.isBlank()) return "必填";
            try {
                int age = Integer.parseInt(v);
                if (age < 1 || age > 120) return "年龄需在1-120之间";
            } catch (Exception e) {
                return "必须为整数";
            }
            return null;
        });
        FieldValidator emailVal = new FieldValidator(emailItem::getValue, v -> {
            if (v == null || v.isBlank()) return "必填";
            if (!Pattern.compile("^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,}$").matcher(v).matches()) return "邮箱格式不正确";
            return null;
        });
        FieldValidator phoneVal = new FieldValidator(phoneItem::getValue, v -> {
            if (v == null || v.isBlank()) return "必填";
            if (!Pattern.compile("^\\d{7,15}$").matcher(v).matches()) return "手机号需为7-15位数字";
            return null;
        });

        formValidator = new FormValidator();
        formValidator.addField(nameVal, nameItem);
        formValidator.addField(studentIdVal, studentIdItem);
        formValidator.addField(ageVal, ageItem);
        formValidator.addField(emailVal, emailItem);
        formValidator.addField(phoneVal, phoneItem);

        btnSave.setOnAction(e -> onSave());
    }

    private void onSave() {
        boolean ok = formValidator.validateAll();
        if (!ok) {
            toast.show("表单校验失败，请修正");
            return;
        }
        // gather model
        Student student = new Student(
                nameItem.getValue().trim(),
                studentIdItem.getValue().trim(),
                Integer.parseInt(ageItem.getValue().trim()),
                emailItem.getValue().trim(),
                phoneItem.getValue().trim()
        );

        // simulate save
        loading.show();
        new Thread(() -> {
            try { Thread.sleep(900); } catch (InterruptedException ignored) {}
            Platform.runLater(() -> {
                loading.hide();
                toast.show("保存成功: " + student.getName() + " / " + student.getStudentId());
            });
        }).start();
    }
}
