package com.simsw.model;

public class Riwayat {
    private int id;
    private String waktu;
    private String aktivitas;
    private String namaBarang;
    private int stokLama;
    private int stokBaru;
    private String user;

    // constructor kosong (wajib untuk XStream)
    public Riwayat() {
    }

    // constructor lengkap
    public Riwayat(int id, String waktu, String aktivitas, String namaBarang, int stokLama, int stokBaru, String user) {
        this.id = id;
        this.waktu = waktu;
        this.aktivitas = aktivitas;
        this.namaBarang = namaBarang;
        this.stokLama = stokLama;
        this.stokBaru = stokBaru;
        this.user = user;
    }

    // getter

    public int getId() {
        return id;
    }

    public String getWaktu() {
        return waktu;
    }

    public String getAktivitas() {
        return aktivitas;
    }

    public String getNamaBarang() {
        return namaBarang;
    }

    public int getStokLama() {
        return stokLama;
    }

    public int getStokBaru() {
        return stokBaru;
    }

    public String getUser() {
        return user;
    }

    // setter

    public void setId(int id) {
        this.id = id;
    }

    public void setWaktu(String waktu) {
        this.waktu = waktu;
    }

    public void setAktivitas(String aktivitas) {
        this.aktivitas = aktivitas;
    }

    public void setNamaBarang(String namaBarang) {
        this.namaBarang = namaBarang;
    }

    public void setStokLama(int stokLama) {
        this.stokLama = stokLama;
    }

    public void setStokBaru(int stokBaru) {
        this.stokBaru = stokBaru;
    }

    public void setUser(String user) {
        this.user = user;
    }

    // debug

    @Override
    public String toString() {
        return "Riwayat{" +
                "id=" + id +
                ", waktu='" + waktu + '\'' +
                ", aktivitas='" + aktivitas + '\'' +
                ", namaBarang='" + namaBarang + '\'' +
                ", stokLama=" + stokLama +
                ", stokBaru=" + stokBaru +
                ", user='" + user + '\'' +
                '}';
    }
}