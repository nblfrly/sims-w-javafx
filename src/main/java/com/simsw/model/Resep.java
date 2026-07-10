package com.simsw.model;

public class Resep {
    private int id;
    private int idMenu;
    private int idBarang;
    private int jumlahPakai;

    public Resep() {
    }

    public Resep(int id,
                 int idMenu,
                 int idBarang,
                 int jumlahPakai) {

        this.id = id;
        this.idMenu = idMenu;
        this.idBarang = idBarang;
        this.jumlahPakai = jumlahPakai;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdMenu() {
        return idMenu;
    }

    public void setIdMenu(int idMenu) {
        this.idMenu = idMenu;
    }

    public int getIdBarang() {
        return idBarang;
    }

    public void setIdBarang(int idBarang) {
        this.idBarang = idBarang;
    }

    public int getJumlahPakai() {
        return jumlahPakai;
    }

    public void setJumlahPakai(int jumlahPakai) {
        this.jumlahPakai = jumlahPakai;
    }
}