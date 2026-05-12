package admin.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class WelcomeController {

    private Stage getStage(javafx.scene.Node node) {
        return (Stage) node.getScene().getWindow();
    }

    @FXML
    public void goToAdmin(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/admin/login.fxml"));
            Parent root = loader.load();
            Stage stage = getStage((javafx.scene.Node) event.getSource());
            stage.setScene(new Scene(root, 520, 650));
            stage.setTitle("Admin — Sistem Perpustakaan");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void goToChatbot(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/chat.fxml"));
            Parent root = loader.load();
            Stage stage = getStage((javafx.scene.Node) event.getSource());
            Scene scene = new Scene(root, 450, 700);
            scene.getStylesheets().add(getClass().getResource("/style/style.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("LibraryBot — Perpustakaan Digital");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}