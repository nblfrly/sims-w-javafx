package com.simsw.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.simsw.dao.xstream.BarangXStreamDAO;
import com.simsw.model.Barang;
import com.thoughtworks.xstream.XStream;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class DashboardController {

    @FXML
    private Button btnInventaris;

    @FXML
    private Button btnLogout;

    @FXML
    private Button btnRiwayat;
    
    @FXML
    private Button btnPegawai;

    @FXML
    private Label lblAktivitas;

    @FXML
    private Label lblBarang;

    @FXML
    private Label lblKategori;

    @FXML
    private Label lblStok;

    @FXML
    private void openInventaris(ActionEvent event) throws IOException {
        long start = System.currentTimeMillis();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/simsw/view/Inventaris.fxml"));

        Parent root = loader.load();
        System.out.println("FXML Load = "+ (System.currentTimeMillis() - start) + " ms");
        Stage stage = (Stage) btnInventaris.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Inventaris");
        stage.show();
    }

    @FXML
    private void logout(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/simsw/view/Login.fxml"));

        Parent root = loader.load();
        Stage stage = (Stage) btnLogout.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Login");
        stage.show();
    }

    @FXML
    private void openRiwayat(ActionEvent event) throws IOException {
        long start = System.currentTimeMillis();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/simsw/view/Riwayat.fxml"));

        Parent root = loader.load();
        System.out.println("FXML Load = "+ (System.currentTimeMillis() - start) + " ms");
        Stage stage = (Stage) btnRiwayat.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Riwayat");
        stage.show();
    }

    @FXML
    private void openPegawai(ActionEvent event) throws IOException {
        long start = System.currentTimeMillis();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/simsw/view/Pegawai.fxml"));

        Parent root = loader.load();
        System.out.println("FXML Load = "+ (System.currentTimeMillis() - start) + " ms");
        Stage stage = (Stage) btnPegawai.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Pegawai");
        stage.show();
    }

    private BarangXStreamDAO barangDAO = new BarangXStreamDAO();

    private void updateDashboard() {
        List<Barang> barangList = barangDAO.getAllBarang();
        lblBarang.setText(String.valueOf(barangList.size()));

        long jumlahKategori = barangList.stream()
            .map(Barang::getKategori)
            .distinct()
            .count();
        lblKategori.setText(String.valueOf(jumlahKategori));
        
        long jumlahStokTipis = barangList.stream()
        .filter(barang -> barang.getStok() <= barang.getStokMinimum())
        .count();

        lblStok.setText(String.valueOf(jumlahStokTipis));
    }

    @FXML
    public void initialize() {
        updateDashboard();
    }
}


