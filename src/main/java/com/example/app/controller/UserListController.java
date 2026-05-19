package com.example.app.controller;

import atlantafx.base.controls.Card;
import atlantafx.base.controls.ModalPane;
import atlantafx.base.theme.Styles;
import com.example.app.AppContext;
import com.example.app.component.EmployeeDetailModal;
import com.example.app.model.User;
import com.example.app.util.UserStatusHelper;
import com.example.app.navigation.EventBus;
import com.example.app.navigation.NavigationClickEvent;
import com.example.app.navigation.RouteParams;
import com.example.app.service.DialogService;
import com.example.app.service.UserManageService;
import com.example.app.viewmodel.UserListViewModel;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class UserListController {

    @FXML
    private VBox root;

    @FXML
    private TextField searchField;

    @FXML
    private Button searchBtn;

    @FXML
    private Button addBtn;

    @FXML
    private TableView<User> userTable;

    @FXML
    private ProgressIndicator loadingIndicator;

    @FXML
    private Label errorLabel;

    private UserListViewModel viewModel;
    private DialogService dialogService;

    @FXML
    public void initialize() {
        UserManageService userService = AppContext.get().getService(UserManageService.class);
        this.viewModel = new UserListViewModel(userService);
        setupTable();
        setupEventHandlers();
        bindViewModel();
        viewModel.loadUsers();
    }

    public void setViewModel(UserListViewModel viewModel) {
        this.viewModel = viewModel;
        bindViewModel();
        viewModel.loadUsers();
    }

    private void setupTable() {
        TableColumn<User, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getId()));
        idCol.setPrefWidth(60);

        TableColumn<User, String> nameCol = new TableColumn<>("姓名");
        nameCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));
        nameCol.setPrefWidth(100);

        TableColumn<User, String> emailCol = new TableColumn<>("邮箱");
        emailCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getEmail()));
        emailCol.setPrefWidth(180);

        TableColumn<User, String> phoneCol = new TableColumn<>("电话");
        phoneCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getPhone()));
        phoneCol.setPrefWidth(120);

        TableColumn<User, String> orgCol = new TableColumn<>("部门");
        orgCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getOrgName()));
        orgCol.setPrefWidth(100);

        TableColumn<User, String> roleCol = new TableColumn<>("角色");
        roleCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getRole()));
        roleCol.setPrefWidth(100);

        TableColumn<User, String> statusCol = new TableColumn<>("状态");
        statusCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getStatus()));
        statusCol.setPrefWidth(80);
        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label badge = new Label(UserStatusHelper.getStatusText(status));
                    badge.getStyleClass().addAll("badge", UserStatusHelper.getStatusStyle(status));
                    setGraphic(badge);
                }
            }
        });

        TableColumn<User, Void> actionCol = new TableColumn<>("操作");
        actionCol.setPrefWidth(120);
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button viewBtn = new Button("查看");
            private final Button deleteBtn = new Button("删除");
            private final HBox box = new HBox(8, viewBtn, deleteBtn);

            {
                viewBtn.getStyleClass().addAll(Styles.BUTTON_OUTLINED, Styles.SMALL);
                deleteBtn.getStyleClass().addAll(Styles.BUTTON_OUTLINED, Styles.DANGER, Styles.SMALL);
                box.setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    User user = getTableView().getItems().get(getIndex());
                    viewBtn.setOnAction(e -> viewUser(user));
                    deleteBtn.setOnAction(e -> deleteUser(user));
                    setGraphic(box);
                }
            }
        });

        userTable.getColumns().addAll(idCol, nameCol, emailCol, phoneCol, orgCol, roleCol, statusCol, actionCol);
        userTable.setRowFactory(tv -> {
            TableRow<User> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && !row.isEmpty()) {
                    navigateToDetail(row.getItem());
                }
            });
            return row;
        });
    }

    private void bindViewModel() {
        if (viewModel == null) return;
        
        userTable.setItems(viewModel.getUserList());
        searchField.textProperty().bindBidirectional(viewModel.searchTextProperty());
        loadingIndicator.visibleProperty().bind(viewModel.loadingProperty());
        errorLabel.textProperty().bind(viewModel.errorMessageProperty());
        errorLabel.visibleProperty().bind(Bindings.isNotEmpty(viewModel.errorMessageProperty()));

        viewModel.setOnUserSelected(this::navigateToDetail);
    }

    private void setupEventHandlers() {
        if (searchBtn != null) {
            searchBtn.setOnAction(e -> {
                if (viewModel != null) viewModel.search();
            });
        }
        if (addBtn != null) {
            addBtn.setOnAction(e -> navigateToCreate());
        }
        searchField.setOnAction(e -> {
            if (viewModel != null) viewModel.search();
        });
    }

    private void viewUser(User user) {
        showUserDetailModal(user);
    }

    private void showUserDetailModal(User user) {
        if (dialogService == null) {
            try {
                dialogService = AppContext.get().getService(DialogService.class);
            } catch (Exception e) {
                javafx.scene.Parent parent = root.getParent();
                while (parent != null && !(parent instanceof StackPane)) {
                    parent = parent.getParent();
                }
                if (parent instanceof StackPane stackPane) {
                    dialogService = new DialogService(stackPane);
                } else {
                    navigateToDetail(user);
                    return;
                }
            }
        }

        EmployeeDetailModal modal = new EmployeeDetailModal(user);
        dialogService.showModal(modal, true);
    }

    private void deleteUser(User user) {
        VBox content = new VBox(16);
        content.setPadding(new Insets(24));
        content.setPrefWidth(360);

        Label msgLabel = new Label("确定要删除用户 " + user.getName() + " 吗？");
        msgLabel.setWrapText(true);

        HBox btnBox = new HBox(8);
        btnBox.setAlignment(Pos.CENTER_RIGHT);

        Button cancelBtn = new Button("取消");
        cancelBtn.getStyleClass().add(Styles.BUTTON_OUTLINED);
        cancelBtn.setOnAction(e -> dialogService.closeAll());

        Button confirmBtn = new Button("删除");
        confirmBtn.getStyleClass().addAll(Styles.ACCENT, Styles.DANGER);
        confirmBtn.setOnAction(e -> {
            dialogService.closeAll();
            viewModel.deleteUser(user);
        });

        btnBox.getChildren().addAll(cancelBtn, confirmBtn);
        content.getChildren().addAll(msgLabel, btnBox);

        ensureDialogService();
        dialogService.showModal(content, true);
    }

    private void ensureDialogService() {
        if (dialogService == null) {
            try {
                dialogService = AppContext.get().getService(DialogService.class);
            } catch (Exception e) {
                javafx.scene.Parent parent = root.getParent();
                while (parent != null && !(parent instanceof StackPane)) {
                    parent = parent.getParent();
                }
                if (parent instanceof StackPane stackPane) {
                    dialogService = new DialogService(stackPane);
                }
            }
        }
    }

    private void navigateToDetail(User user) {
        RouteParams params = new RouteParams();
        params.getData().put("id", user.getId());
        EventBus.getInstance().publish(new NavigationClickEvent(null, "/system/user/detail/" + user.getId(), params));
    }

    private void navigateToCreate() {
        EventBus.getInstance().publish(new NavigationClickEvent(null, "/system/user/create", new RouteParams()));
    }
}
