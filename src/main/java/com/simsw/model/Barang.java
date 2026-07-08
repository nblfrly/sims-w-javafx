package com.simsw.model;

public class Barang {
    private int id;
    private String namaBarang;
    private String kategori;
    private int stok;
    private int stokMinimum;

    // constructor kosong
    public Barang () {
    }
    
    public Barang(int id, String namaBarang, String kategori, int stok, int stokMinimum) {
        this.id = id;
        this.namaBarang = namaBarang;
        this.kategori = kategori;
        this.stok = stok;
        this.stokMinimum = stokMinimum;
    }

    public int getId() {
        return id;
    }

    public String getNamaBarang() {
        return namaBarang;
    }

    public String getKategori() {
        return kategori;
    }

    public int getStok() {
        return stok;
    }

    public int getStokMinimum() {
        return stokMinimum;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNamaBarang(String namaBarang) {
        this.namaBarang = namaBarang;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public void setStok(int stok) {
        this.stok = stok;
    }

    public void setStokMinimum(int stokMinimum) {
        this.stokMinimum = stokMinimum;
    }
}
