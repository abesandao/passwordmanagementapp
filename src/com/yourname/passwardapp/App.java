package com.yourname.passwardapp;

import com.yourname.passwardapp.view.MainViewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

import java.util.Optional;

public class App extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // 1. マスターパスワードを尋ねるダイアログを表示
        TextInputDialog passwordDialog = new TextInputDialog();
        passwordDialog.setTitle("認証");
        passwordDialog.setHeaderText("マスターパスワードを入力してください。");
        passwordDialog.setContentText("パスワード:");

        Optional<String> result = passwordDialog.showAndWait();

        // 2. パスワードが入力された場合のみ、メイン画面に進む
        if (result.isPresent() && !result.get().isEmpty()) {
            String masterPassword = result.get();

            // メイン画面を読み込む
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("view/main-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);

            // MainViewControllerにマスターパスワードを渡す
            MainViewController controller = fxmlLoader.getController();
            controller.initData(masterPassword);

            // ウィンドウを閉じる時の処理を追加
            stage.setOnCloseRequest(event -> {
                System.out.println("ウィンドウを閉じています。データを保存します...");
                controller.saveData();
            });

            stage.setTitle("パスワード管理アプリ");
            stage.setScene(scene);
            stage.show();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}