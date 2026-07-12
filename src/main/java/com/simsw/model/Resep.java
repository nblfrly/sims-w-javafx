package com.simsw.model;

public class Resep {
    private int id;
    private int idMenu;
    private int idBarang;
    private double jumlahPakai;
    private String satuan;

    public Resep() {
    }

    public Resep(int id,
                 int idMenu,
                 int idBarang,
                 double jumlahPakai,
                 String satuan) {

        this.id = id;
        this.idMenu = idMenu;
        this.idBarang = idBarang;
        this.jumlahPakai = jumlahPakai;
        this.satuan = satuan;
    }

 
    // Getter
    public int getId() {
        return id;
    }

    public int getIdMenu() {
        return idMenu;
    }

    public int getIdBarang() {
        return idBarang;
    }

    public double getJumlahPakai() {
        return jumlahPakai;
    }

    public String getSatuan() {
        return satuan;
    }

    // Setter
    public void setId(int id) {
        this.id = id;
    }

    public void setIdMenu(int idMenu) {
        this.idMenu = idMenu;
    }

    public void setIdBarang(int idBarang) {
        this.idBarang = idBarang;
    }

    public void setJumlahPakai(double jumlahPakai) {
        this.jumlahPakai = jumlahPakai;
    }

    public void setSatuan(String satuan) {
        this.satuan = satuan;
    }

    // toString
    @Override
    public String toString() {
        return "Resep{" +
                "id=" + id +
                ", idMenu=" + idMenu +
                ", idBarang=" + idBarang +
                ", jumlahPakai=" + jumlahPakai +
                ", satuan='" + satuan + '\'' +
                '}';
    }

}