package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/view/chat.fxml")
        );

        var root = loader.load(); // ini penting

        Scene scene = new Scene((Parent) root, 400, 600);

        // Tambahkan CSS di sini
        scene.getStylesheets().add(
                getClass().getResource("/style/style.css").toExternalForm()
        );

        stage.setTitle("LibraryBot");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}