package com.simsw.controller;

import com.simsw.dao.xstream.BarangXStreamDAO;
import com.simsw.dao.xstream.MenuXStreamDAO;
import com.simsw.dao.xstream.PegawaiXStreamDAO;
import com.simsw.dao.xstream.RiwayatXStreamDAO;
import com.simsw.model.Barang;
import com.simsw.model.Riwayat;
import com.simsw.util.Session;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

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
}
