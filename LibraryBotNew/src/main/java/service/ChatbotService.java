package service;

import database.BukuDAO;
import database.KategoriDAO;
import model.Buku;
import java.util.List;
import java.util.stream.Collectors;

public class ChatbotService {

    private final BukuDAO bukuDAO = new BukuDAO();
    private final KategoriDAO kategoriDAO = new KategoriDAO();

    public String processInput(String input) {
        input = input.toLowerCase().trim();

        if (input.contains("carikan buku") || input.contains("cari buku")) {
            String judul = extractBookTitle(input);
            return cariBuku(judul);
        } else if (input.contains("apakah buku") || input.contains("tersedia") || input.contains("stok")) {
            String judul = extractBookTitle(input);
            return cekKetersediaan(judul);
        } else if (input.contains("detail") || input.contains("informasi") || input.contains("info")) {
            String judul = extractBookTitle(input);
            return detailBuku(judul);
        } else if (input.contains("tampilkan buku") || input.contains("daftar buku") || input.contains("katalog")) {
            return tampilkanBukuTersedia();
        } else if (input.contains("semua katalog") || input.contains("semua buku")) {
            return tampilkanSemuaBuku();
        } else if (input.contains("kategori") || input.contains("jenis buku")) {
            return tampilkanKategori();
        } else if (input.matches(".*(hai|halo|hi|hello|pagi|siang|sore|malam).*")) {
            return "Halo! 👋 Saya siap membantu Anda menemukan buku. Apa yang bisa saya bantu hari ini?";
        } else if (input.matches(".*(terima kasih|makasih|thanks).*")) {
            return "Sama-sama! 😊 Senang bisa membantu. Ada yang bisa saya bantu lagi?";
        }

        return "Maaf, saya tidak memahami pertanyaan Anda. 🤔\n\nAnda bisa mencoba:\n• Carikan buku berjudul [judul]\n• Apakah buku [judul] tersedia?\n• Tampilkan detail buku [judul]\n• Tampilkan semua katalog buku";
    }

    private String extractBookTitle(String input) {
        return input
            .replaceAll(".*(carikan buku berjudul|cari buku|apakah buku berjudul|buku berjudul|detail.*buku berjudul|informasi.*buku berjudul|buku)", "")
            .replaceAll("(tersedia|\\?)", "")
            .trim();
    }

    private String cariBuku(String judul) {
        List<Buku> results = bukuDAO.search(judul);
        if (!results.isEmpty()) {
            Buku b = results.get(0);
            return String.format(
                "✅ Buku ditemukan!\n\n📚 %s\n✍️ Oleh: %s\n📖 Penerbit: %s\n📦 Stok: %d\n📊 Status: %s",
                b.getJudul(), b.getPenulis(), b.getPenerbit(), b.getStok(),
                b.isTersedia() ? "Tersedia ✓" : "Stok Habis ✗"
            );
        }
        return "❌ Maaf, buku tidak ditemukan dalam katalog kami.\n\nCoba periksa ejaan atau ketik 'tampilkan semua katalog'.";
    }

    private String cekKetersediaan(String judul) {
        List<Buku> results = bukuDAO.search(judul);
        if (!results.isEmpty()) {
            Buku b = results.get(0);
            if (b.isTersedia() && b.getStok() > 0) {
                return String.format("✅ Buku '%s' TERSEDIA!\n\nSaat ini ada %d eksemplar yang tersedia.", b.getJudul(), b.getStok());
            } else {
                return String.format("❌ Buku '%s' TIDAK TERSEDIA (Stok Habis).\n\nSilakan cek kembali nanti.", b.getJudul());
            }
        }
        return "❌ Buku tidak ditemukan.";
    }

    private String detailBuku(String judul) {
        List<Buku> results = bukuDAO.search(judul);
        if (!results.isEmpty()) {
            Buku b = results.get(0);
            return String.format(
                "📖 DETAIL BUKU\n\nJudul: %s\nPenulis: %s\nPenerbit: %s\nISBN: %s\nTahun: %d\nStok: %d\nStatus: %s",
                b.getJudul(), b.getPenulis(), b.getPenerbit(), b.getIsbn(), b.getTahun(), b.getStok(),
                b.isTersedia() ? "Tersedia ✓" : "Tidak Tersedia ✗"
            );
        }
        return "❌ Buku tidak ditemukan.";
    }

    private String tampilkanBukuTersedia() {
        List<Buku> all = bukuDAO.getAll();
        List<Buku> tersedia = all.stream().filter(b -> b.isTersedia() && b.getStok() > 0).collect(Collectors.toList());
        if (tersedia.isEmpty()) return "Maaf, saat ini tidak ada buku yang tersedia.";
        StringBuilder sb = new StringBuilder("📚 BUKU YANG TERSEDIA:\n\n");
        int no = 1;
        for (Buku b : tersedia) {
            sb.append(String.format("%d. %s\n   Oleh: %s\n   ✓ Tersedia (%d eksemplar)\n\n", no++, b.getJudul(), b.getPenulis(), b.getStok()));
        }
        return sb.toString();
    }

    private String tampilkanSemuaBuku() {
        List<Buku> all = bukuDAO.getAll();
        StringBuilder sb = new StringBuilder("📖 KATALOG LENGKAP PERPUSTAKAAN\n\n");
        sb.append(String.format("Koleksi kami: %d buku\n\n", all.size()));
        int no = 1;
        for (Buku b : all) {
            sb.append(String.format("%d. %s\n   Oleh: %s\n   %s\n\n", no++, b.getJudul(), b.getPenulis(), b.isTersedia() ? "✓ Tersedia (" + b.getStok() + ")" : "✗ Stok Habis"));
        }
        return sb.toString();
    }

    private String tampilkanKategori() {
        var kategoriList = kategoriDAO.getAll();
        StringBuilder sb = new StringBuilder("📑 KATEGORI BUKU\n\n");
        for (var k : kategoriList) {
            sb.append("📚 ").append(k.getNamaKategori()).append("\n");
        }
        sb.append("\nKetik nama kategori untuk melihat daftar bukunya.");
        return sb.toString();
    }

    public List<Buku> getDaftarBuku() {
        return bukuDAO.getAll();
    }
}
