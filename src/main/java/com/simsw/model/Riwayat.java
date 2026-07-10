package com.simsw.model;

import java.time.LocalDateTime;

public class Riwayat {

    private String namaPegawai;
    private String namaBarang;
    private String keterangan;
    private LocalDateTime tanggal;
    private int jumlahLama;
    private int jumlahBaru;
    private int id;

    public Riwayat() {
    }
    public Riwayat(
        LocalDateTime tanggal, 
        int jumlahLama, 
        int jumlahBaru,
        String namaBarang,  
        String namaPegawai, 
        String keterangan) {

        this.tanggal = tanggal;
        this.namaBarang = namaBarang;
        this.jumlahLama = jumlahLama;
        this.jumlahBaru = jumlahBaru;
        this.namaPegawai = namaPegawai;
        this.keterangan = keterangan;
        
    }

    public String getNamaPegawai() {
        return namaPegawai;
    } 
    public void setNamaPegawai(String namaPegawai) {
        this.namaPegawai = namaPegawai;
    }
    public String getNamaBarang() {
        return namaBarang;
    } 
    public void setNamaBarang(String namaBarang) {
        this.namaBarang = namaBarang;
    }       
    public String getKeterangan() {
        return keterangan;
    } 
    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }
    public LocalDateTime getTanggal() {
        return tanggal;
    }
    public void setTanggal(LocalDateTime tanggal) {
        this.tanggal = tanggal;
    }
    public int getJumlahLama() {
        return jumlahLama;
    } 
    public void setJumlahLama(int jumlahLama) {
        this.jumlahLama = jumlahLama;
    }
    public int getJumlahBaru() {
        return jumlahBaru;
    } 
    public void setJumlahBaru(int jumlahBaru) {
        this.jumlahBaru = jumlahBaru;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }


}