package database;

import model.Buku;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BukuDAO {

    private final DatabaseManager db = DatabaseManager.getInstance();

    public List<Buku> getAll() {
        List<Buku> list = new ArrayList<>();
        String sql = "SELECT * FROM buku ORDER BY id_buku";
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

    public Buku getById(String idBuku) {
        String sql = "SELECT * FROM buku WHERE id_buku = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, idBuku);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Buku> getByKategori(String idKategori) {
        List<Buku> list = new ArrayList<>();
        String sql = "SELECT * FROM buku WHERE id_kategori = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, idKategori);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Buku> search(String keyword) {
        List<Buku> list = new ArrayList<>();
        String sql = "SELECT * FROM buku WHERE LOWER(judul) LIKE ? OR LOWER(penulis) LIKE ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            String kw = "%" + keyword.toLowerCase() + "%";
            ps.setString(1, kw);
            ps.setString(2, kw);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(Buku buku) {
        String sql = """
            INSERT INTO buku (id_buku, judul, penulis, penerbit, isbn, tahun, stok, tersedia, id_kategori)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, buku.getIdBuku());
            ps.setString(2, buku.getJudul());
            ps.setString(3, buku.getPenulis());
            ps.setString(4, buku.getPenerbit());
            ps.setString(5, buku.getIsbn());
            ps.setInt(6, buku.getTahun());
            ps.setInt(7, buku.getStok());
            ps.setInt(8, buku.isTersedia() ? 1 : 0);
            ps.setString(9, buku.getIdKategori());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(Buku buku) {
        String sql = """
            UPDATE buku SET judul=?, penulis=?, penerbit=?, isbn=?, tahun=?, stok=?, tersedia=?, id_kategori=?
            WHERE id_buku=?
        """;
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, buku.getJudul());
            ps.setString(2, buku.getPenulis());
            ps.setString(3, buku.getPenerbit());
            ps.setString(4, buku.getIsbn());
            ps.setInt(5, buku.getTahun());
            ps.setInt(6, buku.getStok());
            ps.setInt(7, buku.isTersedia() ? 1 : 0);
            ps.setString(8, buku.getIdKategori());
            ps.setString(9, buku.getIdBuku());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(String idBuku) {
        String sql = "DELETE FROM buku WHERE id_buku = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, idBuku);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getCount() {
        String sql = "SELECT COUNT(*) FROM buku";
        try (Statement stmt = db.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public String generateNextId() {
        int count = getCount();
        return String.format("BK%03d", count + 1);
    }

    private Buku mapRow(ResultSet rs) throws SQLException {
        return new Buku(
            rs.getString("id_buku"),
            rs.getString("judul"),
            rs.getString("penulis"),
            rs.getString("penerbit"),
            rs.getString("isbn"),
            rs.getInt("tahun"),
            rs.getInt("stok"),
            rs.getInt("tersedia") == 1,
            rs.getString("id_kategori")
        );
    }
}
