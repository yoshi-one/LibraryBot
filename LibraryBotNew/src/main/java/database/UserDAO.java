package database;

import model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    private final DatabaseManager db = DatabaseManager.getInstance();

    public List<User> getAll() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY id_user";
        try (Statement stmt = db.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public User getByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insert(User u) {
        String sql = "INSERT INTO users (id_user, username, email, password, role) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, u.getIdUser());
            ps.setString(2, u.getUsername());
            ps.setString(3, u.getEmail());
            ps.setString(4, u.getPassword());
            ps.setString(5, u.getRole());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(User u) {
        String sql = "UPDATE users SET username=?, email=?, password=?, role=? WHERE id_user=?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getPassword());
            ps.setString(4, u.getRole());
            ps.setString(5, u.getIdUser());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(String idUser) {
        String sql = "DELETE FROM users WHERE id_user = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, idUser);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean register(String username, String email, String password) {
        int count = getCount();
        String newId = String.format("USR%03d", count + 1);
        User u = new User(newId, username, email, password, "user");
        return insert(u);
    }

    public int getCount() {
        String sql = "SELECT COUNT(*) FROM users";
        try (Statement stmt = db.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.getInt(1);
        } catch (SQLException e) {
            return 0;
        }
    }

    public String generateNextId() {
        int count = getCount();
        return String.format("USR%03d", count + 1);
    }

    private User mapRow(ResultSet rs) throws SQLException {
        return new User(
            rs.getString("id_user"),
            rs.getString("username"),
            rs.getString("email"),
            rs.getString("password"),
            rs.getString("role")
        );
    }
}
