package com.simsw.controller;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;

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
    private void openRiwayat(ActionEvent event) {
        try {
            long start = System.currentTimeMillis();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/simsw/view/Riwayat.fxml"));

            Parent root = loader.load();
            System.out.println("FXML Load = " + (System.currentTimeMillis() - start) + " ms");
            Stage stage = (Stage) btnRiwayat.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Riwayat");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // @FXML
    // private void openRiwayat(ActionEvent event) throws IOException {
    //     long start = System.currentTimeMillis();

    //     FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/simsw/view/Riwayat.fxml"));

    //     Parent root = loader.load();
    //     System.out.println("FXML Load = "+ (System.currentTimeMillis() - start) + " ms");
    //     Stage stage = (Stage) btnRiwayat.getScene().getWindow();
    //     stage.setScene(new Scene(root));
    //     stage.setTitle("Riwayat");
    //     stage.show();
    // }

    @FXML
    private void openPegawai(ActionEvent event) throws IOException {
        long start = System.currentTimeMillis();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/simsw/view/Pegawai.fxml"));

        Parent root = loader.load();
        System.out.println("FXML Load = " + (System.currentTimeMillis() - start) + " ms");
        Stage stage = (Stage) btnInventaris.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Pegawai");
        stage.show();
    }
}


