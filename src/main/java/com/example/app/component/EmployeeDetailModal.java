package com.example.app.component;

import atlantafx.base.theme.Styles;
import com.example.app.model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class EmployeeDetailModal extends VBox {

    private final User user;
    private Runnable onClose;

    public EmployeeDetailModal(User user) {
        this.user = user;
        initialize();
    }

    private void initialize() {
        setSpacing(20);
        setPadding(new Insets(24));
        setAlignment(Pos.TOP_CENTER);
        setMaxWidth(500);
        setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                 "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 20, 0, 0, 5);");

        Label title = new Label("员工详情");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        VBox infoCard = new VBox(16);
        infoCard.setPadding(new Insets(20));
        infoCard.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 8;");

        infoCard.getChildren().addAll(
            createInfoRow("姓名", user.getName()),
            createInfoRow("邮箱", user.getEmail()),
            createInfoRow("电话", user.getPhone()),
            createInfoRow("部门", user.getOrgName()),
            createInfoRow("角色", user.getRole()),
            createStatusRow("状态", user.getStatus()),
            createInfoRow("创建时间", user.getCreateTime())
        );

        HBox buttonBox = new HBox(12);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        Button closeBtn = new Button("关闭");
        closeBtn.getStyleClass().addAll(Styles.ACCENT);
        closeBtn.setOnAction(e -> close());

        buttonBox.getChildren().add(closeBtn);

        getChildren().addAll(title, infoCard, buttonBox);
    }

    private void close() {
        Runnable closer = (Runnable) getProperties().get("dialog-complete");
        if (closer != null) {
            closer.run();
        } else if (onClose != null) {
            onClose.run();
        }
    }

    private HBox createInfoRow(String label, String value) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);

        Label labelNode = new Label(label + ":");
        labelNode.setPrefWidth(80);
        labelNode.setStyle("-fx-text-fill: #666; -fx-font-size: 13px;");

        Label valueNode = new Label(value != null ? value : "-");
        valueNode.setStyle("-fx-font-size: 13px; -fx-font-weight: 500;");
        HBox.setHgrow(valueNode, javafx.scene.layout.Priority.ALWAYS);

        row.getChildren().addAll(labelNode, valueNode);
        return row;
    }

    private HBox createStatusRow(String label, String status) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);

        Label labelNode = new Label(label + ":");
        labelNode.setPrefWidth(80);
        labelNode.setStyle("-fx-text-fill: #666; -fx-font-size: 13px;");

        Label badge = new Label(getStatusText(status));
        badge.getStyleClass().addAll("badge", getStatusStyle(status));

        row.getChildren().addAll(labelNode, badge);
        return row;
    }

    private String getStatusText(String status) {
        if (status == null) return "-";
        return switch (status) {
            case "active" -> "正常";
            case "inactive" -> "停用";
            case "pending" -> "待审核";
            default -> status;
        };
    }

    private String getStatusStyle(String status) {
        if (status == null) return "";
        return switch (status) {
            case "active" -> Styles.SUCCESS;
            case "inactive" -> Styles.DANGER;
            case "pending" -> Styles.WARNING;
            default -> "";
        };
    }

    public void setOnClose(Runnable onClose) {
        this.onClose = onClose;
    }
}