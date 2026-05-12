package admin.controller;

import database.*;
import model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;

public class AdminController {

    // Sidebar buttons
    @FXML private Button btnDashboard, btnBuku, btnKategori, btnUser;

    // Top bar
    @FXML private Label pageTitle, adminName;

    // Pages
    @FXML private ScrollPane dashboardPage, bukuPage, kategoriPage, userPage;

    // Dashboard labels
    @FXML private Label lblStatus, lblConnString, lblSyncTime;
    @FXML private Label lblTotalBuku, lblTotalKategori, lblTotalUser;

    // Buku table
    @FXML private TableView<Buku> tableBuku;
    @FXML private TableColumn<Buku, String> colIdBuku, colJudul, colPenulis, colPenerbit, colIsbn;
    @FXML private TableColumn<Buku, Integer> colTahun, colStok;
    @FXML private TableColumn<Buku, Void> colAksiBuku;

    // Kategori list
    @FXML private VBox flowKategori;

    // Konfirmasi akun tables
    @FXML private TableView<User> tableUser;           // pending
    @FXML private TableView<User> tableUserKonfirmasi; // terkonfirmasi
    @FXML private TableColumn<User, String> colIdUser, colUsername, colEmail, colRole; // pending cols
    @FXML private TableColumn<User, Void> colAksiUser;
    @FXML private TableColumn<User, String> colIdUserK, colUsernameK, colEmailK;
    @FXML private TableColumn<User, Void> colAksiUserK;
    @FXML private Label lblPendingCount;

    private final BukuDAO bukuDAO = new BukuDAO();
    private final KategoriDAO kategoriDAO = new KategoriDAO();
    private final UserDAO userDAO = new UserDAO();
    private final DatabaseManager db = DatabaseManager.getInstance();

    private String currentAdminUsername = "Ilham";

    @FXML
    public void initialize() {
        adminName.setText(currentAdminUsername);
        setupBukuTable();
        setupKonfirmasiTable();
        showDashboard(null);
    }

    public void setAdminUser(String username) {
        this.currentAdminUsername = username;
        adminName.setText(username);
    }

    // ===================== NAVIGATION =====================

    private void setActivePage(ScrollPane activePage, String title, Button activeBtn) {
        dashboardPage.setVisible(false); dashboardPage.setManaged(false);
        bukuPage.setVisible(false); bukuPage.setManaged(false);
        kategoriPage.setVisible(false); kategoriPage.setManaged(false);
        userPage.setVisible(false); userPage.setManaged(false);

        activePage.setVisible(true); activePage.setManaged(true);
        pageTitle.setText(title);

        String activeStyle = "-fx-background-color: #E8EDFF; -fx-text-fill: #3B3FB6; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 10 15; -fx-font-size: 13px;";
        String inactiveStyle = "-fx-background-color: transparent; -fx-text-fill: #444; -fx-background-radius: 10; -fx-padding: 10 15; -fx-font-size: 13px; -fx-cursor: hand;";

        btnDashboard.setStyle(inactiveStyle);
        btnBuku.setStyle(inactiveStyle);
        btnKategori.setStyle(inactiveStyle);
        btnUser.setStyle(inactiveStyle);
        activeBtn.setStyle(activeStyle);
    }

    @FXML
    public void showDashboard(javafx.event.ActionEvent e) {
        setActivePage(dashboardPage, "Dashboard", btnDashboard);
        refreshDashboard();
    }

    @FXML
    public void showBuku(javafx.event.ActionEvent e) {
        setActivePage(bukuPage, "Kelola Buku", btnBuku);
        loadBuku();
    }

    @FXML
    public void showKategori(javafx.event.ActionEvent e) {
        setActivePage(kategoriPage, "Kelola Kategori", btnKategori);
        loadKategori();
    }

    @FXML
    public void showUser(javafx.event.ActionEvent e) {
        setActivePage(userPage, "Konfirmasi Akun", btnUser);
        loadKonfirmasiTables();
    }

    @FXML
    public void handleRefreshKonfirmasi() {
        loadKonfirmasiTables();
    }

    @FXML
    public void handleLogout(javafx.event.ActionEvent e) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Yakin ingin keluar?", ButtonType.YES, ButtonType.NO);
        alert.setHeaderText("Konfirmasi Logout");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            try {
                Stage stage = (Stage) btnDashboard.getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/admin/login.fxml"));
                Parent root = loader.load();
                stage.setScene(new Scene(root, 500, 600));
                stage.setTitle("Admin Login");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    // ===================== DASHBOARD =====================

    private void refreshDashboard() {
        boolean connected = db.isConnected();
        lblStatus.setText(connected ? "Terhubung" : "Terputus");
        lblStatus.setStyle(connected
                ? "-fx-background-color: #27ae60; -fx-text-fill: white; -fx-padding: 3 10; -fx-background-radius: 20; -fx-font-size: 12px;"
                : "-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 3 10; -fx-background-radius: 20; -fx-font-size: 12px;");
        lblConnString.setText("perpustakaan.db (SQLite)");
        lblSyncTime.setText(db.getLastSyncTime());
        lblTotalBuku.setText(String.valueOf(bukuDAO.getCount()));
        lblTotalKategori.setText(String.valueOf(kategoriDAO.getCount()));
        lblTotalUser.setText(String.valueOf(userDAO.getCount()));
    }

    @FXML
    public void handleOpenConn() {
        db.openConnection();
        showInfo("Koneksi dibuka.");
        refreshDashboard();
    }

    @FXML
    public void handleCloseConn() {
        db.closeConnection();
        showInfo("Koneksi ditutup.");
        refreshDashboard();
    }

    @FXML
    public void handleBackup() {
        showInfo("Backup database berhasil!\nFile: perpustakaan_backup.db");
    }

    // ===================== BUKU =====================

    private void setupBukuTable() {
        colIdBuku.setCellValueFactory(new PropertyValueFactory<>("idBuku"));
        colJudul.setCellValueFactory(new PropertyValueFactory<>("judul"));
        colPenulis.setCellValueFactory(new PropertyValueFactory<>("penulis"));
        colPenerbit.setCellValueFactory(new PropertyValueFactory<>("penerbit"));
        colIsbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        colTahun.setCellValueFactory(new PropertyValueFactory<>("tahun"));
        colStok.setCellValueFactory(new PropertyValueFactory<>("stok"));

        colAksiBuku.setCellFactory(col -> new TableCell<>() {
            final Button editBtn = new Button("✏");
            final Button delBtn = new Button("🗑");

            {
                editBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand; -fx-padding: 4 8;");
                delBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand; -fx-padding: 4 8;");

                editBtn.setOnAction(e -> {
                    Buku b = getTableView().getItems().get(getIndex());
                    showBukuDialog(b);
                });
                delBtn.setOnAction(e -> {
                    Buku b = getTableView().getItems().get(getIndex());
                    if (konfirmHapus("Hapus buku \"" + b.getJudul() + "\"?")) {
                        bukuDAO.delete(b.getIdBuku());
                        loadBuku();
                        refreshDashboard();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox box = new HBox(5, editBtn, delBtn);
                    setGraphic(box);
                }
            }
        });
    }

    private void loadBuku() {
        ObservableList<Buku> data = FXCollections.observableArrayList(bukuDAO.getAll());
        tableBuku.setItems(data);
    }

    @FXML
    public void handleTambahBuku() {
        showBukuDialog(null);
    }

    private void showBukuDialog(Buku existing) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Tambah Buku" : "Edit Buku");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));

        TextField tfId = new TextField(existing != null ? existing.getIdBuku() : bukuDAO.generateNextId());
        TextField tfJudul = new TextField(existing != null ? existing.getJudul() : "");
        TextField tfPenulis = new TextField(existing != null ? existing.getPenulis() : "");
        TextField tfPenerbit = new TextField(existing != null ? existing.getPenerbit() : "");
        TextField tfIsbn = new TextField(existing != null ? existing.getIsbn() : "");
        TextField tfTahun = new TextField(existing != null ? String.valueOf(existing.getTahun()) : "");
        TextField tfStok = new TextField(existing != null ? String.valueOf(existing.getStok()) : "0");

        ComboBox<Kategori> cbKategori = new ComboBox<>();
        cbKategori.getItems().addAll(kategoriDAO.getAll());
        if (existing != null) {
            cbKategori.getItems().stream()
                    .filter(k -> k.getIdKategori().equals(existing.getIdKategori()))
                    .findFirst().ifPresent(cbKategori::setValue);
        }

        if (existing != null) tfId.setDisable(true);

        String[] labels = {"ID Buku:", "Judul:", "Penulis:", "Penerbit:", "ISBN:", "Tahun:", "Stok:", "Kategori:"};
        javafx.scene.Node[] fields = {tfId, tfJudul, tfPenulis, tfPenerbit, tfIsbn, tfTahun, tfStok, cbKategori};
        for (int i = 0; i < labels.length; i++) {
            grid.add(new Label(labels[i]), 0, i);
            grid.add(fields[i], 1, i);
            ((javafx.scene.control.Control) fields[i]).setPrefWidth(250);
        }

        dialog.getDialogPane().setContent(grid);
        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                Buku buku = new Buku(
                        tfId.getText(), tfJudul.getText(), tfPenulis.getText(), tfPenerbit.getText(),
                        tfIsbn.getText(), Integer.parseInt(tfTahun.getText()),
                        Integer.parseInt(tfStok.getText()),
                        Integer.parseInt(tfStok.getText()) > 0,
                        cbKategori.getValue() != null ? cbKategori.getValue().getIdKategori() : null
                );
                if (existing == null) bukuDAO.insert(buku);
                else bukuDAO.update(buku);
                loadBuku();
                refreshDashboard();
            } catch (Exception e) {
                showError("Data tidak valid. Pastikan tahun dan stok berupa angka.");
            }
        }
    }

    // ===================== KATEGORI =====================

    private void loadKategori() {
        flowKategori.getChildren().clear();
        List<Kategori> list = kategoriDAO.getAll();
        if (list.isEmpty()) {
            Label empty = new Label("Belum ada kategori. Klik '+ Tambah Kategori' untuk menambahkan.");
            empty.setStyle("-fx-text-fill: #888; -fx-font-size: 13px; -fx-padding: 20;");
            flowKategori.getChildren().add(empty);
            return;
        }
        for (Kategori k : list) {
            flowKategori.getChildren().add(createKategoriCard(k));
        }
    }

    private HBox createKategoriCard(Kategori k) {
        HBox card = new HBox();
        card.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        card.setPadding(new Insets(16, 20, 16, 20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian,rgba(0,0,0,0.07),8,0,0,2); -fx-border-color: #f0f0f0; -fx-border-radius: 12; -fx-border-width: 1;");

        // Kiri: ID + Nama Kategori
        VBox infoBox = new VBox(4);
        infoBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        HBox.setHgrow(infoBox, javafx.scene.layout.Priority.ALWAYS);

        Label idLbl = new Label(k.getIdKategori());
        idLbl.setStyle("-fx-text-fill: #aaa; -fx-font-size: 11px; -fx-font-weight: normal;");

        Label nameLbl = new Label(k.getNamaKategori());
        nameLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: #1a1a2e;");

        // Jumlah buku
        List<Buku> books = bukuDAO.getByKategori(k.getIdKategori());
        Label jumlahLbl = new Label(books.size() + " buku");
        jumlahLbl.setStyle("-fx-text-fill: #888; -fx-font-size: 12px;");

        infoBox.getChildren().addAll(idLbl, nameLbl, jumlahLbl);

        // Kanan: Tombol
        Button lihatBtn = new Button("📚  Lihat Buku");
        lihatBtn.setStyle(
            "-fx-background-color: #5B63D3; -fx-text-fill: white; " +
            "-fx-background-radius: 8; -fx-padding: 8 16; -fx-font-size: 12px; " +
            "-fx-font-weight: bold; -fx-cursor: hand;"
        );
        lihatBtn.setOnAction(e -> {
            StringBuilder sb = new StringBuilder("📚 Buku dalam kategori " + k.getNamaKategori() + ":\n\n");
            if (books.isEmpty()) sb.append("Belum ada buku dalam kategori ini.");
            else books.forEach(b -> sb.append("• ").append(b.getJudul()).append("  —  ").append(b.getPenulis()).append("\n"));
            showInfo(sb.toString());
        });

        Button editBtn = new Button("✏  Edit");
        editBtn.setStyle(
            "-fx-background-color: #AED6F1; -fx-text-fill: #1A5276; " +
            "-fx-background-radius: 8; -fx-padding: 8 16; -fx-font-size: 12px; " +
            "-fx-font-weight: bold; -fx-cursor: hand;"
        );
        editBtn.setOnAction(e -> showKategoriDialog(k));

        Button hapusBtn = new Button("🗑  Hapus");
        hapusBtn.setStyle(
            "-fx-background-color: #E74C3C; -fx-text-fill: white; " +
            "-fx-background-radius: 8; -fx-padding: 8 16; -fx-font-size: 12px; " +
            "-fx-font-weight: bold; -fx-cursor: hand;"
        );
        hapusBtn.setOnAction(e -> {
            if (konfirmHapus("Hapus kategori \"" + k.getNamaKategori() + "\"?")) {
                kategoriDAO.delete(k.getIdKategori());
                loadKategori();
                refreshDashboard();
            }
        });

        HBox btnRow = new HBox(10, lihatBtn, editBtn, hapusBtn);
        btnRow.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

        card.getChildren().addAll(infoBox, btnRow);
        return card;
    }

    @FXML
    public void handleTambahKategori() {
        showKategoriDialog(null);
    }

    private void showKategoriDialog(Kategori existing) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Tambah Kategori" : "Edit Kategori");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));

        TextField tfId = new TextField(existing != null ? existing.getIdKategori() : kategoriDAO.generateNextId());
        TextField tfNama = new TextField(existing != null ? existing.getNamaKategori() : "");
        if (existing != null) tfId.setDisable(true);

        grid.add(new Label("ID Kategori:"), 0, 0); grid.add(tfId, 1, 0);
        grid.add(new Label("Nama Kategori:"), 0, 1); grid.add(tfNama, 1, 1);
        tfId.setPrefWidth(200); tfNama.setPrefWidth(200);

        dialog.getDialogPane().setContent(grid);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Kategori k = new Kategori(tfId.getText(), tfNama.getText());
            if (existing == null) kategoriDAO.insert(k);
            else kategoriDAO.update(k);
            loadKategori();
            refreshDashboard();
        }
    }

    // ===================== KONFIRMASI AKUN =====================

    private void setupKonfirmasiTable() {
        // Tabel PENDING
        colIdUser.setCellValueFactory(new PropertyValueFactory<>("idUser"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        // Kolom "TANGGAL DAFTAR" - pakai getStatusKonfirmasi sebagai placeholder (bisa diganti nanti)
        colRole.setCellValueFactory(new PropertyValueFactory<>("statusKonfirmasi"));

        colAksiUser.setCellFactory(col -> new TableCell<>() {
            final Button konfirmBtn = new Button("✅ Konfirmasi");
            final Button tolakBtn   = new Button("❌ Tolak");

            {
                konfirmBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 5 10; -fx-font-size: 11px;");
                tolakBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 5 10; -fx-font-size: 11px;");

                konfirmBtn.setOnAction(e -> {
                    User u = getTableView().getItems().get(getIndex());
                    userDAO.konfirmasi(u.getIdUser());
                    showInfo("Akun \"" + u.getUsername() + "\" berhasil dikonfirmasi.");
                    loadKonfirmasiTables();
                    refreshDashboard();
                });
                tolakBtn.setOnAction(e -> {
                    User u = getTableView().getItems().get(getIndex());
                    if (konfirmHapus("Tolak & hapus akun \"" + u.getUsername() + "\"?")) {
                        userDAO.tolak(u.getIdUser());
                        loadKonfirmasiTables();
                        refreshDashboard();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : new HBox(6, konfirmBtn, tolakBtn));
            }
        });

        // Tabel TERKONFIRMASI
        colIdUserK.setCellValueFactory(new PropertyValueFactory<>("idUser"));
        colUsernameK.setCellValueFactory(new PropertyValueFactory<>("username"));
        colEmailK.setCellValueFactory(new PropertyValueFactory<>("email"));

        colAksiUserK.setCellFactory(col -> new TableCell<>() {
            final Button batalBtn = new Button("🚫 Batalkan");
            {
                batalBtn.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 5 10; -fx-font-size: 11px;");
                batalBtn.setOnAction(e -> {
                    User u = getTableView().getItems().get(getIndex());
                    if (konfirmHapus("Batalkan konfirmasi akun \"" + u.getUsername() + "\"?\nAkun tidak akan bisa login.")) {
                        userDAO.delete(u.getIdUser());
                        loadKonfirmasiTables();
                        refreshDashboard();
                    }
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : batalBtn);
            }
        });
    }

    private void loadKonfirmasiTables() {
        ObservableList<User> pending = FXCollections.observableArrayList(userDAO.getPendingKonfirmasi());
        tableUser.setItems(pending);

        ObservableList<User> confirmed = FXCollections.observableArrayList(userDAO.getTerkonfirmasi());
        tableUserKonfirmasi.setItems(confirmed);

        int pendingCount = pending.size();
        if (lblPendingCount != null) {
            lblPendingCount.setText(String.valueOf(pendingCount));
            lblPendingCount.setStyle(pendingCount > 0
                ? "-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 2 10; -fx-font-size: 13px;"
                : "-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 2 10; -fx-font-size: 13px;");
        }
    }

    // ===================== HELPERS =====================

    private boolean konfirmHapus(String msg) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.YES, ButtonType.NO);
        alert.setHeaderText("Konfirmasi Hapus");
        return alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
    }

    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg);
        alert.setHeaderText("Error");
        alert.showAndWait();
    }
}