package admin.controller;

import database.UserDAO;
import model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField tfUsername;
    @FXML private PasswordField tfPassword;
    @FXML private Label lblError;

    private final UserDAO userDAO = new UserDAO();

    private Stage getStage() {
        return (Stage) tfUsername.getScene().getWindow();
    }

    @FXML
    public void handleLogin() {
        String username = tfUsername.getText().trim();
        String password = tfPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            lblError.setText("Username dan password tidak boleh kosong.");
            return;
        }

        // login() di UserDAO sudah filter dikonfirmasi=1
        User user = userDAO.login(username, password);
        if (user != null) {
            // user ditemukan = sudah dikonfirmasi
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/admin/admin.fxml"));
                Parent root = loader.load();
                AdminController controller = loader.getController();
                controller.setAdminUser(user.getUsername());

                Stage stage = getStage();
                stage.setScene(new Scene(root));
                stage.setTitle("Admin Panel — Sistem Perpustakaan");
                stage.setMaximized(true);
            } catch (Exception e) {
                e.printStackTrace();
                lblError.setText("Gagal membuka halaman admin.");
            }
        } else {
            // Cek apakah akun ada tapi belum dikonfirmasi
            User cek = userDAO.getByUsername(username);
            if (cek != null && !cek.isDikonfirmasi()) {
                lblError.setText("Akun Anda belum dikonfirmasi oleh admin. Harap tunggu persetujuan.");
            } else {
                lblError.setText("Username atau password salah.");
            }
        }
    }

    /** Kembali ke halaman welcome */
    @FXML
    public void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/admin/welcome.fxml"));
            Parent root = loader.load();
            Stage stage = getStage();
            stage.setScene(new Scene(root, 520, 650));
            stage.setTitle("Sistem Perpustakaan Digital");
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    public void goToRegistrasi() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/admin/registrasi.fxml"));
            Parent root = loader.load();
            Stage stage = getStage();
            stage.setScene(new Scene(root, 520, 650));
            stage.setTitle("Registrasi Admin — Sistem Perpustakaan");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}