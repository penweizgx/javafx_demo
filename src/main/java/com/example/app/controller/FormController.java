package com.example.app.controller;

import com.example.app.ViewManager;
import com.example.app.model.BonusConfig;
import com.example.app.validator.FieldValidator;
import com.example.app.validator.FormValidator;
import com.example.app.component.FormItem;
import com.example.app.service.LoadingService;
import com.example.app.service.ToastService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;

public class FormController {

    @FXML private VBox root;
    @FXML private VBox formArea;
    @FXML private Button btnSave;

    private FormItem minItem;
    private FormItem maxItem;
    private FormItem countItem;

    // get services from AppContext or ViewManager (here we'll fetch from AppContext)
    private LoadingService loading;
    private ToastService toast;

    private FieldValidator minVal;
    private FieldValidator maxVal;
    private FieldValidator countVal;
    private FormValidator formValidator;

    public FormController() {
        // no-arg constructor (injected by FXMLLoader); services will be fetched in initialize
    }

    @FXML
    public void initialize() {
        // fetch services
        this.loading = ViewManager.class.getModule() == null ? null : com.example.app.AppContext.get().getService(LoadingService.class);
        this.toast = com.example.app.AppContext.get().getService(ToastService.class);

        // build form components
        minItem = new FormItem(); minItem.setLabel("最小金额");
        maxItem = new FormItem(); maxItem.setLabel("最大金额");
        countItem = new FormItem(); countItem.setLabel("数量");

        minItem.setValue("1");
        maxItem.setValue("5");
        countItem.setValue("100");

        formArea.getChildren().addAll(minItem, maxItem, countItem);

        // validators
        minVal = new FieldValidator(() -> minItem.getValue(), v -> {
            if (v == null || v.isBlank()) return "必填";
            try { Double.parseDouble(v); } catch (Exception e) { return "必须为数字"; }
            return null;
        });
        maxVal = new FieldValidator(() -> maxItem.getValue(), v -> {
            if (v == null || v.isBlank()) return "必填";
            try { Double.parseDouble(v); } catch (Exception e) { return "必须为数字"; }
            return null;
        });
        countVal = new FieldValidator(() -> countItem.getValue(), v -> {
            if (v == null || v.isBlank()) return "必填";
            try { Integer.parseInt(v); } catch (Exception e) { return "必须为整数"; }
            return null;
        });

        formValidator = new FormValidator();
        formValidator.addField(minVal, minItem);
        formValidator.addField(maxVal, maxItem);
        formValidator.addField(countVal, countItem);

        // global rule: min <= max
        formValidator.addGlobalRule(() -> {
            try {
                double a = Double.parseDouble(minItem.getValue());
                double b = Double.parseDouble(maxItem.getValue());
                return a <= b ? null : "最小金额不能大于最大金额";
            } catch (Exception ex) { return null; }
        });

        btnSave.setOnAction(e -> onSave());
    }

    private void onSave() {
        formArea.getChildren().forEach(n -> { /* clear errors */ });
        boolean ok = formValidator.validateAll();
        if (!ok) {
            toast.show("表单校验失败，请修正");
            return;
        }
        // gather model
        double a = Double.parseDouble(minItem.getValue());
        double b = Double.parseDouble(maxItem.getValue());
        int c = Integer.parseInt(countItem.getValue());
        BonusConfig cfg = new BonusConfig(a,b,c);

        // simulate save
        loading.show();
        new Thread(() -> {
            try { Thread.sleep(900); } catch (InterruptedException ignored) {}
            Platform.runLater(() -> {
                loading.hide();
                toast.show("保存成功: " + cfg.getMinAmount() + " ~ " + cfg.getMaxAmount() + " x" + cfg.getCount());
            });
        }).start();
    }
}
