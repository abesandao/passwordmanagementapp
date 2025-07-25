package com.yourname.passwardapp.view;

import com.yourname.passwardapp.model.PasswordEntry;
import com.yourname.passwardapp.util.DatabaseManager;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import java.util.List;
import java.util.Optional;

public class MainViewController {
    // --- ↓↓↓ すべての変数をここにまとめる ---
    @FXML
    private TableView<PasswordEntry> passwordTable;
    @FXML
    private Button addButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button copyButton; // コピーボタンの変数
    @FXML
    private Button viewButton; // 表示ボタンの変数

    private DatabaseManager dbManager;
    private String masterPassword;
    // --- ↑↑↑ ここまで ---

    @FXML
    public void initialize() {
        // 初期化処理は initData に移動
    }

    public void initData(String masterPassword) {
        this.masterPassword = masterPassword;
        this.dbManager = new DatabaseManager(masterPassword);

        // 列の定義
        TableColumn<PasswordEntry, String> serviceColumn = new TableColumn<>("サービス名");
        serviceColumn.setCellValueFactory(new PropertyValueFactory<>("serviceName"));
        serviceColumn.setPrefWidth(150);

        TableColumn<PasswordEntry, String> usernameColumn = new TableColumn<>("ユーザー名");
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        usernameColumn.setPrefWidth(300);

        passwordTable.getColumns().add(serviceColumn);
        passwordTable.getColumns().add(usernameColumn);

        loadAndDisplayPasswords();
    }

    @FXML
    private void handleAddButton() {
        Dialog<PasswordEntry> dialog = new Dialog<>();
        dialog.setTitle("新しいパスワードの追加");
        dialog.setHeaderText("新しいアカウント情報を入力してください。");

        ButtonType addButtonType = new ButtonType("追加", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField serviceField = new TextField();
        serviceField.setPromptText("サービス名");
        TextField usernameField = new TextField();
        usernameField.setPromptText("ユーザー名");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("パスワード");

        grid.add(new Label("サービス名:"), 0, 0);
        grid.add(serviceField, 1, 0);
        grid.add(new Label("ユーザー名:"), 0, 1);
        grid.add(usernameField, 1, 1);
        grid.add(new Label("パスワード:"), 0, 2);
        grid.add(passwordField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                return new PasswordEntry(serviceField.getText(), usernameField.getText(), passwordField.getText());
            }
            return null;
        });

        Optional<PasswordEntry> result = dialog.showAndWait();

        result.ifPresent(newEntry -> {
            dbManager.savePassword(newEntry);
            loadAndDisplayPasswords();
        });
    }

    @FXML
    private void handleDeleteButton() {
        PasswordEntry selectedEntry = passwordTable.getSelectionModel().getSelectedItem();
        if (selectedEntry == null) {
            showAlert(Alert.AlertType.WARNING, "注意", "削除する項目を選択してください。");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("削除の確認");
        confirmation.setHeaderText("本当にこの項目を削除しますか？");
        confirmation.setContentText(selectedEntry.getServiceName() + " (" + selectedEntry.getUsername() + ")");
        Optional<ButtonType> result = confirmation.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            dbManager.deletePassword(selectedEntry);
            loadAndDisplayPasswords();
        }
    }

    @FXML
    private void handleCopyButton() {
        PasswordEntry selectedEntry = passwordTable.getSelectionModel().getSelectedItem();
        if (selectedEntry == null) {
            showAlert(Alert.AlertType.WARNING, "注意", "コピーする項目を選択してください。");
            return;
        }
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(selectedEntry.getPassword());
        clipboard.setContent(content);
        showAlert(Alert.AlertType.INFORMATION, "成功", "パスワードをクリップボードにコピーしました。");
    }

    @FXML
    private void handleViewButton() {
        PasswordEntry selectedEntry = passwordTable.getSelectionModel().getSelectedItem();
        if (selectedEntry == null) {
            showAlert(Alert.AlertType.WARNING, "注意", "表示する項目を選択してください。");
            return;
        }
        showAlert(Alert.AlertType.INFORMATION, "パスワードの表示", "パスワード: " + selectedEntry.getPassword());
    }

    private void loadAndDisplayPasswords() {
        List<PasswordEntry> entries = dbManager.getAllPasswords();
        passwordTable.getItems().setAll(entries);
    }

    public void saveData() {
        if (dbManager != null) {
            dbManager.saveDataToFile(masterPassword);
        }
    }

    // アラート表示を共通化するヘルパーメソッド
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}