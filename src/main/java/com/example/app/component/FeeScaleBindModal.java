package com.example.app.component;

import atlantafx.base.theme.Styles;
import com.example.app.AppContext;
import com.example.app.model.*;
import com.example.app.service.DialogService;
import com.example.app.service.FinanceManageService;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.List;

public class FeeScaleBindModal extends VBox {

    private final FinAccountVO account;
    private final Runnable onSuccess;
    private final FinanceManageService financeService;

    private final TabPane tabPane = new TabPane();
    private final ProgressIndicator loadingIndicator = new ProgressIndicator();

    public FeeScaleBindModal(FinAccountVO account, Runnable onSuccess) {
        this.account = account;
        this.onSuccess = onSuccess;
        this.financeService = AppContext.get().getService(FinanceManageService.class);
        initialize();
    }

    private void initialize() {
        setSpacing(16);
        setPadding(new Insets(24));
        setAlignment(Pos.TOP_CENTER);
        setMaxWidth(600);
        setMaxHeight(500);
        setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                 "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 20, 0, 0, 5);");

        StudentVO student = account.getStudent();
        String studentName = student != null ? student.getName() : "未知";

        Label title = new Label("设置收费标准 - " + studentName);
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        loadingIndicator.setMaxSize(30, 30);
        loadingIndicator.setVisible(true);

        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        HBox buttonBox = new HBox(12);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        Button cancelBtn = new Button("取消");
        cancelBtn.getStyleClass().addAll(Styles.BUTTON_OUTLINED);
        cancelBtn.setOnAction(e -> close());

        Button saveBtn = new Button("保存");
        saveBtn.getStyleClass().addAll(Styles.ACCENT);
        saveBtn.setOnAction(e -> handleSave());

        buttonBox.getChildren().addAll(cancelBtn, saveBtn);

        getChildren().addAll(title, loadingIndicator, tabPane, buttonBox);

        loadSubjects();
    }

    private void loadSubjects() {
        financeService.listSubjectWithFeeScale().whenComplete((subjects, error) -> {
            Platform.runLater(() -> {
                loadingIndicator.setVisible(false);
                if (error != null) {
                    tabPane.getTabs().add(new Tab("错误", new Label("加载失败: " + error.getMessage())));
                    return;
                }
                populateTabs(subjects);
            });
        });
    }

    private void populateTabs(List<SubjectWithFeeScaleDTO> subjects) {
        tabPane.getTabs().clear();

        List<Long> boundIds = new ArrayList<>();
        if (account.getFeeScales() != null) {
            for (FeeScale fs : account.getFeeScales()) {
                boundIds.add(fs.getId());
            }
        }

        if (subjects == null || subjects.isEmpty()) {
            tabPane.getTabs().add(new Tab("无数据", new Label("暂无收费科目")));
            return;
        }

        for (SubjectWithFeeScaleDTO subject : subjects) {
            if (subject.getDisabled() != null && subject.getDisabled()) continue;

            VBox tabContent = new VBox(8);
            tabContent.setPadding(new Insets(12));

            if (subject.getFeeScales() != null && !subject.getFeeScales().isEmpty()) {
                for (FeeScaleDTO fs : subject.getFeeScales()) {
                    if (fs.getDisabled() != null && fs.getDisabled()) continue;

                    HBox row = new HBox(8);
                    row.setAlignment(Pos.CENTER_LEFT);

                    CheckBox cb = new CheckBox();
                    cb.setUserData(fs.getId());
                    if (boundIds.contains(fs.getId())) {
                        cb.setSelected(true);
                    }

                    String unitLabel = fs.getUnit() != null ? fs.getUnit() : "";
                    String amountStr = fs.getStandardAmount() != null ? "¥" + fs.getStandardAmount() + "/" + unitLabel : "-";
                    String nameStr = fs.getName() != null ? fs.getName() : amountStr;

                    Label nameLabel = new Label(nameStr);
                    nameLabel.setPrefWidth(150);

                    Label amountLabel = new Label(amountStr);
                    amountLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");

                    row.getChildren().addAll(cb, nameLabel, amountLabel);
                    tabContent.getChildren().add(row);
                }
            } else {
                tabContent.getChildren().add(new Label("暂无收费标准"));
            }

            Tab tab = new Tab(subject.getName());
            tab.setContent(tabContent);
            tabPane.getTabs().add(tab);
        }
    }

    private void handleSave() {
        List<Long> selectedIds = new ArrayList<>();

        for (Tab tab : tabPane.getTabs()) {
            if (tab.getContent() instanceof VBox vbox) {
                collectSelectedIds(vbox, selectedIds);
            }
        }

        financeService.feeScaleBind(account.getStudentId(), selectedIds).whenComplete((v, error) -> {
            Platform.runLater(() -> {
                if (error != null) {
                    showAlert("保存失败: " + error.getMessage());
                } else {
                    close();
                    if (onSuccess != null) onSuccess.run();
                }
            });
        });
    }

    private void collectSelectedIds(Pane pane, List<Long> ids) {
        for (javafx.scene.Node node : pane.getChildren()) {
            if (node instanceof HBox hbox) {
                for (javafx.scene.Node child : hbox.getChildren()) {
                    if (child instanceof CheckBox cb && cb.isSelected()) {
                        Object userData = cb.getUserData();
                        if (userData instanceof Long id) {
                            ids.add(id);
                        }
                    }
                }
            } else if (node instanceof Pane subPane) {
                collectSelectedIds(subPane, ids);
            }
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("提示");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void close() {
        Runnable closer = (Runnable) getProperties().get("dialog-complete");
        if (closer != null) closer.run();
    }

    public void show() {
        DialogService dialogService = AppContext.get().getService(DialogService.class);
        if (dialogService != null) {
            dialogService.showModal(this, true);
        }
    }
}
