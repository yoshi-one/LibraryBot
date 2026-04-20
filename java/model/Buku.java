package model;

public class Buku {
    private String judul;
    private String penulis;
    private String penerbit;
    private String isbn;
    private boolean tersedia;

    public Buku(String judul, String penulis, String penerbit, String isbn, boolean tersedia) {
        this.judul = judul;
        this.penulis = penulis;
        this.penerbit = penerbit;
        this.isbn = isbn;
        this.tersedia = tersedia;
    }

    public String getJudul() { return judul; }
    public String getPenulis() { return penulis; }
    public String getPenerbit() { return penerbit; }
    public String getIsbn() { return isbn; }
    public boolean isTersedia() { return tersedia; }
}