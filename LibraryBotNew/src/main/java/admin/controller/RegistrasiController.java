package admin.controller;

import database.UserDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class RegistrasiController {

    @FXML private TextField tfUsername;
    @FXML private TextField tfEmail;
    @FXML private PasswordField tfPassword;
    @FXML private Label lblError;

    private final UserDAO userDAO = new UserDAO();

    private Stage getStage() {
        return (Stage) tfUsername.getScene().getWindow();
    }

    @FXML
    public void handleDaftar() {
        String username = tfUsername.getText().trim();
        String email    = tfEmail.getText().trim();
        String password = tfPassword.getText();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            lblError.setText("Semua field harus diisi.");
            return;
        }
        if (!email.contains("@")) {
            lblError.setText("Format email tidak valid.");
            return;
        }

        boolean success = userDAO.register(username, email, password);
        if (success) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION,
                    "Registrasi berhasil! Silakan login dengan akun baru Anda.");
            alert.setHeaderText(null);
            alert.showAndWait();
            goToLogin();
        } else {
            lblError.setText("Registrasi gagal. Username atau email sudah digunakan.");
        }
    }

    @FXML
    public void goToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/admin/login.fxml"));
            Parent root = loader.load();
            Stage stage = getStage();
            stage.setScene(new Scene(root, 520, 650));
            stage.setTitle("Login Admin — Sistem Perpustakaan");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void goToWelcome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/admin/welcome.fxml"));
            Parent root = loader.load();
            Stage stage = getStage();
            stage.setScene(new Scene(root, 520, 650));
            stage.setTitle("Sistem Perpustakaan Digital");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
