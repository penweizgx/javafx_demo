package com.example.app.controller;

import atlantafx.base.theme.Styles;
import com.example.app.AppContext;
import com.example.app.model.User;
import com.example.app.navigation.EventBus;
import com.example.app.navigation.NavigationClickEvent;
import com.example.app.navigation.RouteParams;
import com.example.app.router.Router;
import com.example.app.service.UserManageService;
import com.example.app.viewmodel.UserDetailViewModel;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class UserDetailController implements Router.ParamReceiver {

    @FXML
    private VBox root;

    @FXML
    private HBox headerBox;

    @FXML
    private Label titleLabel;

    @FXML
    private Button backBtn;

    @FXML
    private Button editBtn;

    @FXML
    private Button saveBtn;

    @FXML
    private Button cancelBtn;

    @FXML
    private ProgressIndicator loadingIndicator;

    @FXML
    private Label errorLabel;

    @FXML
    private VBox infoCard;

    @FXML
    private GridPane infoGrid;

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField orgField;

    @FXML
    private TextField roleField;

    @FXML
    private Label statusBadge;

    @FXML
    private TextField createTimeField;

    private UserDetailViewModel viewModel;
    private String userId;

    @FXML
    public void initialize() {
        UserManageService userService = AppContext.get().getService(UserManageService.class);
        this.viewModel = new UserDetailViewModel(userService);
        setupStyles();
        bindViewModel();
        setupEventHandlers();
    }

    @Override
    public void receiveParams(RouteParams params) {
        String id = params.getString("id");
        if (id != null) {
            setUserId(id);
        }
    }

    private void setupStyles() {
        if (titleLabel != null) {
            titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        }
        if (backBtn != null) {
            backBtn.getStyleClass().addAll(Styles.BUTTON_OUTLINED);
        }
        if (editBtn != null) {
            editBtn.getStyleClass().addAll(Styles.BUTTON_OUTLINED);
        }
        if (saveBtn != null) {
            saveBtn.getStyleClass().addAll(Styles.ACCENT);
        }
        if (cancelBtn != null) {
            cancelBtn.getStyleClass().addAll(Styles.BUTTON_OUTLINED);
        }
    }

    private void setupBindings() {
    }

    private void setupEventHandlers() {
        if (backBtn != null) {
            backBtn.setOnAction(e -> navigateBack());
        }
        if (editBtn != null) {
            editBtn.setOnAction(e -> {
                if (viewModel != null) viewModel.enterEditMode();
                updateEditMode(true);
            });
        }
        if (saveBtn != null) {
            saveBtn.setOnAction(e -> {
                if (viewModel != null) viewModel.saveUser();
                updateEditMode(false);
            });
        }
        if (cancelBtn != null) {
            cancelBtn.setOnAction(e -> {
                if (viewModel != null) viewModel.cancelEdit();
                updateEditMode(false);
            });
        }
    }

    public void setViewModel(UserDetailViewModel viewModel) {
        this.viewModel = viewModel;
        bindViewModel();
    }

    private void bindViewModel() {
        if (viewModel == null) return;

        loadingIndicator.visibleProperty().bind(viewModel.loadingProperty());
        errorLabel.textProperty().bind(viewModel.errorMessageProperty());
        errorLabel.visibleProperty().bind(viewModel.errorMessageProperty().isNotEmpty());

        infoCard.visibleProperty().bind(viewModel.loadingProperty().not());

        nameField.textProperty().bindBidirectional(viewModel.nameProperty());
        emailField.textProperty().bindBidirectional(viewModel.emailProperty());
        phoneField.textProperty().bindBidirectional(viewModel.phoneProperty());
        orgField.textProperty().bindBidirectional(viewModel.orgNameProperty());
        roleField.textProperty().bindBidirectional(viewModel.roleProperty());

        viewModel.userProperty().addListener((obs, old, user) -> {
            if (user != null) {
                updateStatusBadge(user.getStatus());
                createTimeField.setText(user.getCreateTime());
            }
        });

        viewModel.editModeProperty().addListener((obs, old, editMode) -> {
            updateEditMode(editMode);
        });
    }

    private void updateStatusBadge(String status) {
        if (statusBadge == null) return;
        statusBadge.setText(getStatusText(status));
        statusBadge.getStyleClass().clear();
        statusBadge.getStyleClass().addAll("badge", getStatusStyle(status));
    }

    private String getStatusText(String status) {
        if (status == null) return "";
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

    private void updateEditMode(boolean editMode) {
        boolean editable = editMode;
        nameField.setEditable(editable);
        emailField.setEditable(editable);
        phoneField.setEditable(editable);
        orgField.setEditable(editable);
        roleField.setEditable(editable);

        editBtn.setVisible(!editable);
        editBtn.setManaged(!editable);
        saveBtn.setVisible(editable);
        saveBtn.setManaged(editable);
        cancelBtn.setVisible(editable);
        cancelBtn.setManaged(editable);

        if (editable) {
            nameField.getStyleClass().add(Styles.ROUNDED);
            emailField.getStyleClass().add(Styles.ROUNDED);
            phoneField.getStyleClass().add(Styles.ROUNDED);
            orgField.getStyleClass().add(Styles.ROUNDED);
            roleField.getStyleClass().add(Styles.ROUNDED);
        } else {
            nameField.getStyleClass().remove(Styles.ROUNDED);
            emailField.getStyleClass().remove(Styles.ROUNDED);
            phoneField.getStyleClass().remove(Styles.ROUNDED);
            orgField.getStyleClass().remove(Styles.ROUNDED);
            roleField.getStyleClass().remove(Styles.ROUNDED);
        }
    }

    public void setUserId(String id) {
        this.userId = id;
        if (viewModel != null && id != null) {
            viewModel.loadUser(id);
        }
    }

    public String getUserId() {
        return userId;
    }

    private void navigateBack() {
        EventBus.getInstance().publish(new NavigationClickEvent(null, "/system/user/list", new RouteParams()));
    }
}
