package com.example.app.viewmodel;

import com.example.app.AppContext;
import com.example.app.exception.ExceptionHandler;
import com.example.app.model.BillNumberRule;
import com.example.app.model.FinanceConfig;
import com.example.app.model.PayChancel;
import com.example.app.service.FinanceManageService;
import com.example.app.service.UserService;
import com.example.app.model.User;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;

import java.util.Collections;
import java.util.Map;

public class FinanceConfigViewModel extends ViewModelBase {

    private final FinanceManageService financeService;
    private Long schId;

    @Getter
    private final ObjectProperty<FinanceConfig> config = new SimpleObjectProperty<>();

    @Getter
    private final ObjectProperty<BillNumberRule> billNumberRule = new SimpleObjectProperty<>();

    @Getter
    private final ObservableList<PayChancel> payChancels = FXCollections.observableArrayList();

    @Getter
    private final BooleanProperty loading = new SimpleBooleanProperty(false);

    @Getter
    private final StringProperty errorMessage = new SimpleStringProperty("");

    public FinanceConfigViewModel(FinanceManageService financeService) {
        this.financeService = financeService;
        loadSchId();
    }

    public ObjectProperty<FinanceConfig> configProperty() { return config; }
    public ObjectProperty<BillNumberRule> billNumberRuleProperty() { return billNumberRule; }
    public BooleanProperty loadingProperty() { return loading; }
    public StringProperty errorMessageProperty() { return errorMessage; }

    private void loadSchId() {
        try {
            UserService userService = AppContext.get().getService(UserService.class);
            User user = userService.getCachedUser();
            if (user != null && user.getId() != null) {
                this.schId = Long.parseLong(user.getId());
            }
        } catch (Exception e) {
            ExceptionHandler.handle(e, "Failed to load schId");
        }
    }

    public void loadConfig() {
        if (schId == null) return;
        loading.set(true);
        errorMessage.set("");
        executeAsync(
            () -> financeService.getConfig(schId).join(),
            result -> {
                config.set(result != null ? result : new FinanceConfig());
                loading.set(false);
            },
            this::onLoadError
        );
    }

    public void saveConfig() {
        if (schId == null) return;
        FinanceConfig cfg = config.get();
        if (cfg == null) return;
        loading.set(true);
        errorMessage.set("");
        executeAsync(
            () -> financeService.saveConfig(schId, cfg).join(),
            () -> {
                loading.set(false);
                loadConfig();
            },
            this::onSaveError
        );
    }

    public void loadBillNumberRule() {
        if (schId == null) return;
        loading.set(true);
        errorMessage.set("");
        executeAsync(
            () -> financeService.getBillNumberRule(schId).join(),
            result -> {
                billNumberRule.set(result != null ? result : new BillNumberRule());
                loading.set(false);
            },
            this::onLoadError
        );
    }

    public void saveBillNumberRule() {
        BillNumberRule rule = billNumberRule.get();
        if (rule == null) return;
        loading.set(true);
        errorMessage.set("");
        executeAsync(
            () -> financeService.saveBillNumberRule(rule).join(),
            () -> {
                loading.set(false);
                loadBillNumberRule();
            },
            this::onSaveError
        );
    }

    public void loadPayChancels() {
        if (schId == null) return;
        loading.set(true);
        errorMessage.set("");
        executeAsync(
            () -> financeService.listPayChancels(schId).join(),
            result -> {
                payChancels.setAll(result != null ? result : Collections.emptyList());
                loading.set(false);
            },
            this::onLoadError
        );
    }

    public void addPayChancel(String name) {
        if (schId == null || name == null || name.trim().isEmpty()) return;
        loading.set(true);
        errorMessage.set("");
        executeAsync(
            () -> financeService.addPayChancel(schId, name.trim()).join(),
            () -> loadPayChancels(),
            this::onSaveError
        );
    }

    public void removePayChancel(Long id) {
        if (schId == null || id == null) return;
        loading.set(true);
        errorMessage.set("");
        executeAsync(
            () -> financeService.removePayChancel(schId, id).join(),
            () -> loadPayChancels(),
            this::onSaveError
        );
    }

    public void validate(java.util.function.Consumer<Map<String, String>> onResult) {
        loading.set(true);
        errorMessage.set("");
        executeAsync(
            () -> financeService.validate().join(),
            result -> {
                loading.set(false);
                if (onResult != null) onResult.accept(result);
            },
            this::onSaveError
        );
    }

    private void onLoadError(Throwable error) {
        loading.set(false);
        errorMessage.set("加载配置失败: " + error.getMessage());
        ExceptionHandler.handle(error, "Failed to load config");
    }

    private void onSaveError(Throwable error) {
        loading.set(false);
        errorMessage.set("保存配置失败: " + error.getMessage());
        ExceptionHandler.handle(error, "Failed to save config");
    }
}
