package database;

import model.Kategori;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KategoriDAO {

    private final DatabaseManager db = DatabaseManager.getInstance();

    public List<Kategori> getAll() {
        List<Kategori> list = new ArrayList<>();
        String sql = "SELECT * FROM kategori ORDER BY id_kategori";
        try (Statement stmt = db.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Kategori(rs.getString("id_kategori"), rs.getString("nama_kategori")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Kategori getById(String id) {
        String sql = "SELECT * FROM kategori WHERE id_kategori = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return new Kategori(rs.getString("id_kategori"), rs.getString("nama_kategori"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insert(Kategori k) {
        String sql = "INSERT INTO kategori (id_kategori, nama_kategori) VALUES (?, ?)";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, k.getIdKategori());
            ps.setString(2, k.getNamaKategori());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(Kategori k) {
        String sql = "UPDATE kategori SET nama_kategori=? WHERE id_kategori=?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, k.getNamaKategori());
            ps.setString(2, k.getIdKategori());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(String id) {
        String sql = "DELETE FROM kategori WHERE id_kategori = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getCount() {
        String sql = "SELECT COUNT(*) FROM kategori";
        try (Statement stmt = db.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.getInt(1);
        } catch (SQLException e) {
            return 0;
        }
    }

    public String generateNextId() {
        int count = getCount();
        return String.format("KT%03d", count + 1);
    }
}
