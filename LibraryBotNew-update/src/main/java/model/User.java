package model;

public class User {
    private String idUser;
    private String username;
    private String email;
    private String password;
    private boolean dikonfirmasi;

    public User(String idUser, String username, String email, String password, boolean dikonfirmasi) {
        this.idUser = idUser;
        this.username = username;
        this.email = email;
        this.password = password;
        this.dikonfirmasi = dikonfirmasi;
    }

    public String getIdUser() { return idUser; }
    public void setIdUser(String idUser) { this.idUser = idUser; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public boolean isDikonfirmasi() { return dikonfirmasi; }
    public void setDikonfirmasi(boolean dikonfirmasi) { this.dikonfirmasi = dikonfirmasi; }

    // Helper untuk tampilan di tabel
    public String getStatusKonfirmasi() {
        return dikonfirmasi ? "✅ Terkonfirmasi" : "⏳ Menunggu";
    }
}
