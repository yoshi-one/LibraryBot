package model;

public class User {
    private String idUser;
    private String username;
    private String email;
    private String password;
    private String role;

    public User(String idUser, String username, String email, String password, String role) {
        this.idUser = idUser;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public String getIdUser() { return idUser; }
    public void setIdUser(String idUser) { this.idUser = idUser; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
