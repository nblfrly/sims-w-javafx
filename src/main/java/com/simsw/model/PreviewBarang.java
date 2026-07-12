package com.simsw.model;

public class PreviewBarang {
    private String namaBarang;
    private int stokAwal;
    private int jumlahPakai;
    private int stokSisa;

    public PreviewBarang() {
    }

    public PreviewBarang(String namaBarang,
                         int stokAwal,
                         int jumlahPakai,
                         int stokSisa) {

        this.namaBarang = namaBarang;
        this.stokAwal = stokAwal;
        this.jumlahPakai = jumlahPakai;
        this.stokSisa = stokSisa;

    }

    public String getNamaBarang() {
        return namaBarang;
    }

    public void setNamaBarang(String namaBarang) {
        this.namaBarang = namaBarang;
    }

    public int getStokAwal() {
        return stokAwal;
    }

    public void setStokAwal(int stokAwal) {
        this.stokAwal = stokAwal;
    }

    public int getJumlahPakai() {
        return jumlahPakai;
    }

    public void setJumlahPakai(int jumlahPakai) {
        this.jumlahPakai = jumlahPakai;
    }

    public int getStokSisa() {
        return stokSisa;
    }

    public void setStokSisa(int stokSisa) {
        this.stokSisa = stokSisa;
    }
}