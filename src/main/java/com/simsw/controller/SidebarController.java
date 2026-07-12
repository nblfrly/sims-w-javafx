package com.simsw.controller;

import com.simsw.util.AlertHelper;
import com.simsw.util.Session;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/** Navigation shared by all pages that are available after login. */
public class SidebarController {

    @FXML private Button btnPegawai;

    @FXML
    private void initialize() {
        if (Session.isLogin() && "Pegawai".equalsIgnoreCase(Session.getCurrentUser().getRole())) {
            btnPegawai.setDisable(true);
        }
    }

    @FXML private void openDashboard(ActionEvent event) { openPage(event, "Dashboard", "Dashboard"); }
    @FXML private void openInventaris(ActionEvent event) { openPage(event, "Inventaris", "Inventaris"); }
    @FXML private void openMenu(ActionEvent event) { openPage(event, "Menu", "Menu Warmindo"); }
    @FXML private void openPenggunaan(ActionEvent event) { openPage(event, "PenggunaanBarang", "Penggunaan Barang"); }
    @FXML private void openRiwayat(ActionEvent event) { openPage(event, "Riwayat", "Riwayat"); }
    @FXML private void openPegawai(ActionEvent event) { openPage(event, "Pegawai", "Manajemen Pegawai"); }

    @FXML
    private void logout(ActionEvent event) {
        Session.logout();
        openPage(event, "Login", "Login");
    }

    private void openPage(ActionEvent event, String fxmlName, String title) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/simsw/view/" + fxmlName + ".fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (IOException exception) {
            AlertHelper.showError("Halaman tidak dapat dibuka.");
        }
    }
}
