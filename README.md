# パスワード管理アプリ

## 概要
JavaとJavaFXで作成した、シンプルなデスクトップ向けパスワード管理アプリケーションです。
学習目的で作成しました。

## 主な機能
- パスワードの追加、表示、コピー、削除
- マスターパスワードによるデータの暗号化 (AES/GCM)
- アプリ終了時のデータ自動保存、起動時の自動読み込み

## 動作方法
このプロジェクトはMaven/Gradleを使用していないため、手動で環境設定を行う必要があります。

1.  **JDK**: `21` 以降をインストールしてください。
2.  **JavaFX SDK**: [公式サイト](https://gluonhq.com/products/javafx/)からSDKをダウンロードしてください。
3.  **IDEでの設定**:
    * プロジェクトをIntelliJ IDEAで開きます。
    * `File` > `Project Structure` > `Libraries` で、ダウンロードしたJavaFX SDKの `lib` フォルダをライブラリとして追加します。
    * `Run` > `Edit Configurations` で、以下のVMオプションを設定します。
      ```
      --module-path "C:\path\to\your\javafx-sdk\lib" --add-modules javafx.controls,javafx.fxml
      ```
      `C:\path\to\your\javafx-sdk\lib` の部分は、実際のパスに置き換えてください。

4.  **実行**: `App.java` を実行します。

## ライセンス
このプロジェクトはMITライセンスです。