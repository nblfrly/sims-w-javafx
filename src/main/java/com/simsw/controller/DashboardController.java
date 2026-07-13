package com.simsw.controller;

import com.simsw.dao.xstream.BarangXStreamDAO;
import com.simsw.dao.xstream.MenuXStreamDAO;
import com.simsw.dao.xstream.PegawaiXStreamDAO;
import com.simsw.dao.xstream.RiwayatXStreamDAO;
import com.simsw.model.Barang;
import com.simsw.model.Riwayat;
import com.simsw.util.SceneNavigator;
import com.simsw.util.Session;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class DashboardController implements Initializable {

    @FXML private Label lblUserInfo;
    @FXML private Label lblWelcome;
    @FXML private Label lblTanggal;
    @FXML private Label lblAktivitas;
    @FXML private Label lblBarang;
    @FXML private Label lblKategori;
    @FXML private Label lblStok;
    @FXML private Label lblMenu;
    @FXML private Label lblPegawai;

    @Override
    public void initialize(java.net.URL url, java.util.ResourceBundle resourceBundle) {
        updateUserInfo();
        updateTanggal();
        updateDashboard();
    }

    private void updateUserInfo() {
        if (Session.isLogin()) {
            String nama = Session.getCurrentUser().getNama();
            String role = Session.getCurrentUser().getRole();
            lblWelcome.setText("Selamat Datang, " + nama + "!");
            lblUserInfo.setText("👤 " + nama + " | " + role);
            return;
        }

        lblWelcome.setText("Selamat Datang!");
        lblUserInfo.setText("👤 Guest");
    }

    private void updateTanggal() {
        String tanggal = LocalDate.now().format(
                DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", Locale.forLanguageTag("id-ID")));
        lblTanggal.setText(tanggal.substring(0, 1).toUpperCase(Locale.forLanguageTag("id-ID")) + tanggal.substring(1));
    }

    private void updateDashboard() {
        List<Barang> barangList = new BarangXStreamDAO().getAllBarang();
        lblBarang.setText(String.valueOf(barangList.size()));
        lblMenu.setText(String.valueOf(new MenuXStreamDAO().getAllMenu().size()));
        lblPegawai.setText(String.valueOf(new PegawaiXStreamDAO().getAllPegawai().size()));

        long jumlahKategori = barangList.stream()
                .map(Barang::getKategori)
                .distinct()
                .count();
        lblKategori.setText(String.valueOf(jumlahKategori));

        long jumlahStokTipis = barangList.stream()
                .filter(barang -> barang.getStok() <= barang.getStokMinimum())
                .count();
        lblStok.setText(String.valueOf(jumlahStokTipis));

        List<Riwayat> riwayatList = new RiwayatXStreamDAO().getAllRiwayat();
        String hariIni = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        long aktivitasHariIni = riwayatList.stream()
                .filter(riwayat -> riwayat.getWaktu().startsWith(hariIni))
                .count();
        lblAktivitas.setText(String.valueOf(aktivitasHariIni));
    }

    @FXML
    private void openInventaris(MouseEvent event) {
        openPage(event, "Inventaris", "Inventaris");
    }

    @FXML
    private void openMenu(MouseEvent event) {
        openPage(event, "Menu", "Menu Warmindo");
    }

    @FXML
    private void openPegawai(MouseEvent event) {
        if (Session.isLogin() && "Pegawai".equalsIgnoreCase(Session.getCurrentUser().getRole())) {
            return;
        }
        openPage(event, "Pegawai", "Manajemen Pegawai");
    }

    @FXML
    private void openStokMenipis(MouseEvent event) {
        try {
            SidebarController.setActivePage("Inventaris");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/simsw/view/Inventaris.fxml"));
            Parent root = loader.load();
            loader.<InventarisController>getController().tampilkanStokMenipis();
            showPage(event, root, "Inventaris - Stok Menipis");
        } catch (Exception exception) {
            // The current page remains visible when a destination cannot be loaded.
        }
    }

    @FXML
    private void openAktivitasHariIni(MouseEvent event) {
        try {
            SidebarController.setActivePage("Riwayat");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/simsw/view/Riwayat.fxml"));
            Parent root = loader.load();
            loader.<RiwayatController>getController().tampilkanAktivitasHariIni();
            showPage(event, root, "Riwayat Hari Ini");
        } catch (Exception exception) {
            // The current page remains visible when a destination cannot be loaded.
        }
    }

    private void openPage(MouseEvent event, String fxmlName, String title) {
        try {
            SidebarController.setActivePage(fxmlName);
            Parent root = FXMLLoader.load(getClass().getResource("/com/simsw/view/" + fxmlName + ".fxml"));
            showPage(event, root, title);
        } catch (Exception exception) {
            // The current page remains visible when a destination cannot be loaded.
        }
    }

    private void showPage(MouseEvent event, Parent root, String title) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        SceneNavigator.show(stage, root, title);
    }
}
