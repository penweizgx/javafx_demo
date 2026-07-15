package com.example.app.component;

import atlantafx.base.theme.Styles;
import com.example.app.AppContext;
import com.example.app.service.DialogService;
import com.example.app.model.ClazzOptionVO;
import com.example.app.model.StudentFO;
import com.example.app.service.StudentManageService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class StudentCreateModal extends VBox {

    private final Long clazzId;
    private final Runnable onSuccess;
    private final StudentManageService studentService;

    private final TextField nameInput = new TextField();
    private final ComboBox<String> genderInput = new ComboBox<>();
    private final DatePicker birthdayInput = new DatePicker();
    private final TextField phoneInput = new TextField();
    private final TextField addressInput = new TextField();
    private final ComboBox<ClazzOptionVO> clazzInput = new ComboBox<>();
    private final TextField contactNameInput = new TextField();
    private final ComboBox<String> contactRelationInput = new ComboBox<>();

    public StudentCreateModal(Long clazzId, Runnable onSuccess) {
        this.clazzId = clazzId;
        this.onSuccess = onSuccess;
        this.studentService = AppContext.get().getService(StudentManageService.class);
        initialize();
    }

    private void initialize() {
        setSpacing(16);
        setPadding(new Insets(24));
        setAlignment(Pos.TOP_CENTER);
        setMaxWidth(480);
        setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                 "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 20, 0, 0, 5);");

        Label title = new Label("新增学生");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        VBox form = new VBox(12);
        form.setPadding(new Insets(16));
        form.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 8;");

        genderInput.getItems().addAll("男", "女");
        contactRelationInput.getItems().addAll("父亲", "母亲", "祖父", "祖母", "其他");

        clazzInput.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(ClazzOptionVO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName());
            }
        });
        clazzInput.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(ClazzOptionVO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName());
            }
        });

        loadClazzOptions();

        form.getChildren().addAll(
            createFormRow("姓名 *", nameInput),
            createFormRow("性别", genderInput),
            createFormRow("生日", birthdayInput),
            createFormRow("电话 *", phoneInput),
            createFormRow("地址", addressInput),
            createFormRow("班级", clazzInput),
            createFormRow("联系人", contactNameInput),
            createFormRow("联系人关系", contactRelationInput)
        );

        HBox buttonBox = new HBox(12);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        Button cancelBtn = new Button("取消");
        cancelBtn.getStyleClass().addAll(Styles.BUTTON_OUTLINED);
        cancelBtn.setOnAction(e -> close());

        Button saveBtn = new Button("保存");
        saveBtn.getStyleClass().addAll(Styles.ACCENT);
        saveBtn.setOnAction(e -> handleSave());

        buttonBox.getChildren().addAll(cancelBtn, saveBtn);

        getChildren().addAll(title, form, buttonBox);
    }

    private void loadClazzOptions() {
        Platform.runLater(() -> {
            try {
                List<ClazzOptionVO> options = studentService.listActiveClazz(null).join();
                clazzInput.setItems(FXCollections.observableArrayList(options));
                if (clazzId != null) {
                    for (ClazzOptionVO opt : options) {
                        if (opt.getId().equals(clazzId)) {
                            clazzInput.setValue(opt);
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                // silently fail - user can still type
            }
        });
    }

    private void handleSave() {
        String name = nameInput.getText().trim();
        String phone = phoneInput.getText().trim();

        if (name.isEmpty()) {
            showAlert("请输入学生姓名");
            return;
        }
        if (phone.isEmpty()) {
            showAlert("请输入手机号");
            return;
        }

        StudentFO fo = new StudentFO();
        fo.setName(name);
        fo.setSex(genderInput.getValue() != null ? (genderInput.getValue().equals("男") ? 1 : 2) : null);
        fo.setBornDate(birthdayInput.getValue() != null ? birthdayInput.getValue().toString() : null);
        fo.setPhone(phone);
        fo.setAddress(addressInput.getText().trim());
        if (clazzInput.getValue() != null) {
            fo.setClazzId(clazzInput.getValue().getId());
        }
        fo.setPname(contactNameInput.getText().trim());
        fo.setPrelationship(contactRelationInput.getValue() != null ? getRelationValue(contactRelationInput.getValue()) : null);

        studentService.create(fo).whenComplete((result, error) -> {
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

    private HBox createFormRow(String label, javafx.scene.Node input) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);

        Label labelNode = new Label(label);
        labelNode.setPrefWidth(100);
        labelNode.setStyle("-fx-text-fill: #333; -fx-font-size: 13px;");

        if (input instanceof TextField tf) {
            tf.setPrefWidth(300);
        } else if (input instanceof ComboBox cb) {
            cb.setPrefWidth(300);
        } else if (input instanceof DatePicker dp) {
            dp.setPrefWidth(300);
        }

        row.getChildren().addAll(labelNode, input);
        return row;
    }

    private Integer getRelationValue(String text) {
        return switch (text) {
            case "父亲" -> 1;
            case "母亲" -> 2;
            case "祖父" -> 3;
            case "祖母" -> 4;
            default -> 5;
        };
    }
}
