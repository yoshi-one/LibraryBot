package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Start with registrasi/login screen
        // Change to "/view/admin/login.fxml" to start at login
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/view/admin/registrasi.fxml")
        );

        Parent root = loader.load();
        Scene scene = new Scene(root, 500, 600);

        stage.setTitle("Sistem Manajemen Perpustakaan");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
