package model;

public class Buku {
    private String idBuku;
    private String judul;
    private String penulis;
    private String penerbit;
    private String isbn;
    private int tahun;
    private int stok;
    private boolean tersedia;
    private String idKategori;

    public Buku(String idBuku, String judul, String penulis, String penerbit,
                String isbn, int tahun, int stok, boolean tersedia, String idKategori) {
        this.idBuku = idBuku;
        this.judul = judul;
        this.penulis = penulis;
        this.penerbit = penerbit;
        this.isbn = isbn;
        this.tahun = tahun;
        this.stok = stok;
        this.tersedia = tersedia;
        this.idKategori = idKategori;
    }

    public Buku(String judul, String penulis, String penerbit, String isbn, boolean tersedia) {
        this.judul = judul;
        this.penulis = penulis;
        this.penerbit = penerbit;
        this.isbn = isbn;
        this.tersedia = tersedia;
        this.stok = tersedia ? 5 : 0;
    }

    public String getIdBuku() { return idBuku; }
    public void setIdBuku(String idBuku) { this.idBuku = idBuku; }
    public String getJudul() { return judul; }
    public void setJudul(String judul) { this.judul = judul; }
    public String getPenulis() { return penulis; }
    public void setPenulis(String penulis) { this.penulis = penulis; }
    public String getPenerbit() { return penerbit; }
    public void setPenerbit(String penerbit) { this.penerbit = penerbit; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public int getTahun() { return tahun; }
    public void setTahun(int tahun) { this.tahun = tahun; }
    public int getStok() { return stok; }
    public void setStok(int stok) { this.stok = stok; }
    public boolean isTersedia() { return tersedia; }
    public void setTersedia(boolean tersedia) { this.tersedia = tersedia; }
    public String getIdKategori() { return idKategori; }
    public void setIdKategori(String idKategori) { this.idKategori = idKategori; }
}
