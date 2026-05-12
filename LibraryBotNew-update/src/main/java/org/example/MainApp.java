package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Start with welcome screen
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/view/admin/welcome.fxml")
        );

        Parent root = loader.load();
        Scene scene = new Scene(root, 520, 650);

        stage.setTitle("Welcome - LibraryBot");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
