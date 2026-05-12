package database;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:perpustakaan.db";
    private static DatabaseManager instance;
    private Connection connection;

    private DatabaseManager() {
        initializeDatabase();
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    public void openConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getLastSyncTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("d/M/yyyy, HH.mm.ss"));
    }

    private void initializeDatabase() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(DB_URL);
            createTables();
            seedDataIfEmpty();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createTables() throws SQLException {
        String createUserTable = """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                id_user TEXT UNIQUE NOT NULL,
                username TEXT NOT NULL,
                email TEXT UNIQUE NOT NULL,
                password TEXT NOT NULL,
                dikonfirmasi INTEGER DEFAULT 0,
                created_at DATETIME DEFAULT CURRENT_TIMESTAMP
            )
        """;
        // Migrasi: tambahkan kolom dikonfirmasi jika belum ada (untuk DB lama)
        String migrasiKolom = """
            ALTER TABLE users ADD COLUMN dikonfirmasi INTEGER DEFAULT 0
        """;

        String createKategoriTable = """
            CREATE TABLE IF NOT EXISTS kategori (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                id_kategori TEXT UNIQUE NOT NULL,
                nama_kategori TEXT NOT NULL,
                created_at DATETIME DEFAULT CURRENT_TIMESTAMP
            )
        """;

        String createBukuTable = """
            CREATE TABLE IF NOT EXISTS buku (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                id_buku TEXT UNIQUE NOT NULL,
                judul TEXT NOT NULL,
                penulis TEXT NOT NULL,
                penerbit TEXT NOT NULL,
                isbn TEXT NOT NULL,
                tahun INTEGER,
                stok INTEGER DEFAULT 0,
                tersedia INTEGER DEFAULT 1,
                id_kategori TEXT,
                created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (id_kategori) REFERENCES kategori(id_kategori)
            )
        """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createUserTable);
            stmt.execute(createKategoriTable);
            stmt.execute(createBukuTable);
        }
        // Migrasi kolom dikonfirmasi (abaikan error jika sudah ada)
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(migrasiKolom);
        } catch (SQLException ignored) {}
        // Migrasi: set dikonfirmasi=1 untuk akun lama yang tadinya admin
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("UPDATE users SET dikonfirmasi = 1 WHERE dikonfirmasi IS NULL");
        } catch (SQLException ignored) {}
    }

    private void seedDataIfEmpty() throws SQLException {
        // Seed users
        String checkUsers = "SELECT COUNT(*) FROM users";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(checkUsers)) {
            if (rs.getInt(1) == 0) {
                String insertUsers = """
                    INSERT INTO users (id_user, username, email, password, dikonfirmasi) VALUES
                    ('USR001', 'Ilham', 'ilham@perpustakaan.com', 'admin123', 1)
                """;
                stmt.execute(insertUsers);
            }
        }

        // Seed kategori
        String checkKategori = "SELECT COUNT(*) FROM kategori";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(checkKategori)) {
            if (rs.getInt(1) == 0) {
                String insertKategori = """
                    INSERT INTO kategori (id_kategori, nama_kategori) VALUES
                    ('KT001', 'Teknologi'),
                    ('KT002', 'Fiksi'),
                    ('KT003', 'Sejarah'),
                    ('KT004', 'Psikologi'),
                    ('KT005', 'Sains')
                """;
                stmt.execute(insertKategori);
            }
        }

        // Seed buku
        String checkBuku = "SELECT COUNT(*) FROM buku";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(checkBuku)) {
            if (rs.getInt(1) == 0) {
                String insertBuku = """
                    INSERT INTO buku (id_buku, judul, penulis, penerbit, isbn, tahun, stok, tersedia, id_kategori) VALUES
                    ('BK001', 'Pemrograman Web Modern', 'John Doe', 'Tech Publisher', '978-1234567890', 2023, 10, 1, 'KT001'),
                    ('BK002', 'Pemrograman Robot', 'John Cena', 'Tech Publisher', '978-1234567323', 2023, 5, 1, 'KT001'),
                    ('BK003', 'Laskar Pelangi', 'Andrea Hirata', 'Bentang Pustaka', '978-979-3062-79-2', 2005, 8, 1, 'KT002'),
                    ('BK004', 'Bumi Manusia', 'Pramoedya Ananta Toer', 'Hasta Mitra', '978-979-8919-88-1', 1980, 3, 0, 'KT002'),
                    ('BK005', 'Sapiens', 'Yuval Noah Harari', 'Penguin', '978-006-231-6097', 2015, 6, 1, 'KT003')
                """;
                stmt.execute(insertBuku);
            }
        }
    }
}
