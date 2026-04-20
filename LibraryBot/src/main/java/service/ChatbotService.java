package service;

import model.Buku;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChatbotService {

    private List<Buku> daftarBuku = new ArrayList<>();

    public ChatbotService() {
        // Data buku dummy yang lebih lengkap
        daftarBuku.add(new Buku("Laskar Pelangi", "Andrea Hirata", "Bentang Pustaka", "978-979-3062-79-2", true));
        daftarBuku.add(new Buku("Bumi Manusia", "Pramoedya Ananta Toer", "Hasta Mitra", "978-979-8919-88-1", false));
        daftarBuku.add(new Buku("Harry Potter", "J.K. Rowling", "Gramedia", "978-602-03-0373-1", true));
        daftarBuku.add(new Buku("Sapiens", "Yuval Noah Harari", "Penguin", "978-006-231-6097", true));
        daftarBuku.add(new Buku("Educated", "Tara Westover", "Random House", "978-039-959-1139", true));
    }

    public String processInput(String input) {
        input = input.toLowerCase().trim();

        // Cari buku berdasarkan judul
        if (input.contains("carikan buku") || input.contains("cari buku")) {
            String judul = extractBookTitle(input);
            return cariBuku(judul);
        }

        // Cek ketersediaan
        else if (input.contains("apakah buku") || input.contains("tersedia") || input.contains("stok")) {
            String judul = extractBookTitle(input);
            return cekKetersediaan(judul);
        }

        // Detail buku
        else if (input.contains("detail") || input.contains("informasi") || input.contains("info")) {
            String judul = extractBookTitle(input);
            return detailBuku(judul);
        }

        // Tampilkan buku yang tersedia
        else if (input.contains("tampilkan buku") || input.contains("daftar buku") || input.contains("katalog")) {
            return tampilkanBukuTersedia();
        }

        // Tampilkan semua katalog
        else if (input.contains("semua katalog") || input.contains("semua buku")) {
            return tampilkanSemuaBuku();
        }

        // Kategori
        else if (input.contains("kategori") || input.contains("jenis buku")) {
            return tampilkanKategori();
        }

        // Salam
        else if (input.matches(".*(hai|halo|hi|hello|pagi|siang|sore|malam).*")) {
            return "Halo! 👋 Saya siap membantu Anda menemukan buku. Apa yang bisa saya bantu hari ini?";
        }

        // Terima kasih
        else if (input.matches(".*(terima kasih|makasih|thanks).*")) {
            return "Sama-sama! 😊 Senang bisa membantu. Ada yang bisa saya bantu lagi?";
        }

        // Default
        return "Maaf, saya tidak memahami pertanyaan Anda. 🤔\n\nAnda bisa mencoba:\n• Carikan buku berjudul [judul]\n• Apakah buku [judul] tersedia?\n• Tampilkan detail buku [judul]\n• Tampilkan semua katalog buku";
    }

    private String extractBookTitle(String input) {
        // Hapus kata-kata umum untuk ekstrak judul
        String judul = input
                .replaceAll(".*(carikan buku berjudul|cari buku|apakah buku berjudul|buku berjudul|detail.*buku berjudul|informasi.*buku berjudul|buku)", "")
                .replaceAll("(tersedia|\\?)", "")
                .trim();
        return judul;
    }

    private String cariBuku(String judul) {
        for (Buku b : daftarBuku) {
            if (b.getJudul().toLowerCase().contains(judul) || judul.contains(b.getJudul().toLowerCase())) {
                return String.format(
                        "✅ Buku ditemukan!\n\n" +
                                "📚 %s\n" +
                                "✍️ Oleh: %s\n" +
                                "📖 Penerbit: %s\n" +
                                "📊 Status: %s\n\n" +
                                "Ketik 'tampilkan detail buku %s' untuk informasi lengkap.",
                        b.getJudul(),
                        b.getPenulis(),
                        b.getPenerbit(),
                        b.isTersedia() ? "Tersedia ✓" : "Stok Habis ✗",
                        b.getJudul()
                );
            }
        }
        return "❌ Maaf, buku tidak ditemukan dalam katalog kami.\n\nCoba periksa ejaan atau lihat katalog lengkap dengan mengetik 'tampilkan semua katalog'.";
    }

    private String cekKetersediaan(String judul) {
        for (Buku b : daftarBuku) {
            if (b.getJudul().toLowerCase().contains(judul) || judul.contains(b.getJudul().toLowerCase())) {
                if (b.isTersedia()) {
                    return String.format(
                            "✅ Buku '%s' TERSEDIA untuk dipinjam!\n\n" +
                                    "Saat ini kami memiliki 5 eksemplar yang tersedia untuk dipinjam.",
                            b.getJudul()
                    );
                } else {
                    return String.format(
                            "❌ Maaf, buku '%s' sedang TIDAK TERSEDIA (Stok Habis).\n\n" +
                                    "Anda bisa melakukan reservasi untuk mendapat notifikasi saat buku tersedia kembali.",
                            b.getJudul()
                    );
                }
            }
        }
        return "❌ Buku tidak ditemukan dalam katalog kami.";
    }

    private String detailBuku(String judul) {
        for (Buku b : daftarBuku) {
            if (b.getJudul().toLowerCase().contains(judul) || judul.contains(b.getJudul().toLowerCase())) {
                return String.format(
                        "📖 DETAIL BUKU\n\n" +
                                "Judul: %s\n" +
                                "Penulis: %s\n" +
                                "Penerbit: %s\n" +
                                "ISBN: %s\n" +
                                "Kategori: Fiksi\n" +
                                "Status: %s\n" +
                                "Stok Tersedia: %s eksemplar",
                        b.getJudul(),
                        b.getPenulis(),
                        b.getPenerbit(),
                        b.getIsbn(),
                        b.isTersedia() ? "Tersedia ✓" : "Tidak Tersedia ✗",
                        b.isTersedia() ? "5" : "0"
                );
            }
        }
        return "❌ Buku tidak ditemukan.";
    }

    private String tampilkanBukuTersedia() {
        List<Buku> bukuTersedia = daftarBuku.stream()
                .filter(Buku::isTersedia)
                .collect(Collectors.toList());

        if (bukuTersedia.isEmpty()) {
            return "Maaf, saat ini tidak ada buku yang tersedia.";
        }

        StringBuilder result = new StringBuilder("📚 BUKU YANG TERSEDIA:\n\n");
        result.append(String.format("Saat ini kami memiliki %d buku yang tersedia untuk dipinjam:\n\n", bukuTersedia.size()));

        int no = 1;
        for (Buku b : bukuTersedia) {
            result.append(String.format(
                    "%d. %s\n   Oleh: %s\n   ✓ Tersedia (5 eksemplar)\n\n",
                    no++, b.getJudul(), b.getPenulis()
            ));
        }

        return result.toString();
    }

    private String tampilkanSemuaBuku() {
        StringBuilder result = new StringBuilder("📖 KATALOG LENGKAP PERPUSTAKAAN\n\n");
        result.append(String.format("Berikut adalah katalog lengkap perpustakaan kami (%d buku):\n\n", daftarBuku.size()));

        int no = 1;
        for (Buku b : daftarBuku) {
            result.append(String.format(
                    "%d. %s\n   Oleh: %s\n   %s\n\n",
                    no++,
                    b.getJudul(),
                    b.getPenulis(),
                    b.isTersedia() ? "✓ Tersedia (5)" : "✗ Stok Habis"
            ));
        }

        return result.toString();
    }

    private String tampilkanKategori() {
        return "📑 KATEGORI BUKU\n\n" +
                "Pilih kategori untuk melihat koleksi buku yang tersedia:\n\n" +
                "📚 Fiksi\n" +
                "👻 Horror\n" +
                "💡 Inspiratif\n" +
                "🧠 Psikologi\n" +
                "🔬 Sains\n" +
                "✨ Fantasi\n\n" +
                "Ketik nama kategori untuk melihat daftar bukunya.";
    }

    public List<Buku> getDaftarBuku() {
        return daftarBuku;
    }
}