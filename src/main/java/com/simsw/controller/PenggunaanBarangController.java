package com.simsw.controller;

import com.simsw.dao.xstream.BarangXStreamDAO;
import com.simsw.dao.xstream.MenuXStreamDAO;
import com.simsw.dao.xstream.ResepXStreamDAO;
import com.simsw.dao.xstream.RiwayatXStreamDAO;
import com.simsw.model.Barang;
import com.simsw.model.Menu;
import com.simsw.model.PreviewBarang;
import com.simsw.model.Resep;
import com.simsw.model.Riwayat;
import com.simsw.util.AlertHelper;
import com.simsw.util.SceneNavigator;
import com.simsw.util.Session;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.StringConverter;

/**
 * Mengelola pengurangan stok barang berdasarkan resep dan jumlah porsi menu.
 */
public class PenggunaanBarangController {

    @FXML private Button btnDashboard;
    @FXML private Button btnGunakan;
    @FXML private ComboBox<Menu> cmbMenu;
    @FXML private Spinner<Integer> spJumlah;
    @FXML private TableView<PreviewBarang> tablePreview;
    @FXML private TableColumn<PreviewBarang, String> colBarang;
    @FXML private TableColumn<PreviewBarang, Integer> colStok;
    @FXML private TableColumn<PreviewBarang, Integer> colPakai;
    @FXML private TableColumn<PreviewBarang, Integer> colSisa;
    @FXML private Label lblStatus;

    private final MenuXStreamDAO menuDAO = new MenuXStreamDAO();
    private final ResepXStreamDAO resepDAO = new ResepXStreamDAO();
    private final BarangXStreamDAO barangDAO = new BarangXStreamDAO();
    private final RiwayatXStreamDAO riwayatDAO = new RiwayatXStreamDAO();
    private final ObservableList<PreviewBarang> previewItems = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        colBarang.setCellValueFactory(new PropertyValueFactory<>("namaBarang"));
        colStok.setCellValueFactory(new PropertyValueFactory<>("stokAwal"));
        colPakai.setCellValueFactory(new PropertyValueFactory<>("jumlahPakai"));
        colSisa.setCellValueFactory(new PropertyValueFactory<>("stokSisa"));
        tablePreview.setItems(previewItems);

        spJumlah.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10_000, 1));
        cmbMenu.setConverter(new StringConverter<>() {
            @Override
            public String toString(Menu menu) {
                return menu == null ? "" : menu.getNamaMenu();
            }

            @Override
            public Menu fromString(String nama) {
                if (nama == null || nama.isBlank()) {
                    return null;
                }
                return cmbMenu.getItems().stream()
                        .filter(menu -> menu.getNamaMenu().equalsIgnoreCase(nama.trim()))
                        .findFirst()
                        .orElse(null);
            }
        });
        loadMenu();
        btnGunakan.setDisable(true);
    }

    private void loadMenu() {
        cmbMenu.getItems().setAll(menuDAO.getAllMenu().stream()
                .filter(menu -> "Aktif".equalsIgnoreCase(menu.getStatus()))
                .toList());
    }

    @FXML
    private void previewPenggunaan() {
        try {
            List<UsageItem> usageItems = createUsageItems();
            previewItems.setAll(toPreview(usageItems));

            List<UsageItem> stokKurang = usageItems.stream()
                    .filter(item -> item.barang().getStok() < item.jumlahPakai())
                    .toList();

            if (stokKurang.isEmpty()) {
                lblStatus.setText("Stok mencukupi. Penggunaan siap diproses.");
                btnGunakan.setDisable(false);
            } else {
                lblStatus.setText("Stok tidak mencukupi: " + namaBarang(stokKurang) + ".");
                btnGunakan.setDisable(true);
            }
        } catch (IllegalArgumentException exception) {
            clearPreview();
            lblStatus.setText(exception.getMessage());
            AlertHelper.showWarning(exception.getMessage());
        }
    }

    @FXML
    private void gunakanBarang() {
        try {
            // Hitung ulang dari data terbaru agar stok tidak memakai hasil preview lama.
            List<UsageItem> usageItems = createUsageItems();
            List<UsageItem> stokKurang = usageItems.stream()
                    .filter(item -> item.barang().getStok() < item.jumlahPakai())
                    .toList();

            if (!stokKurang.isEmpty()) {
                previewItems.setAll(toPreview(usageItems));
                String message = "Stok tidak mencukupi untuk: " + namaBarang(stokKurang) + ".";
                lblStatus.setText(message);
                btnGunakan.setDisable(true);
                AlertHelper.showWarning(message);
                return;
            }

            for (UsageItem item : usageItems) {
                Barang barang = item.barang();
                int stokLama = barang.getStok();
                int stokBaru = stokLama - item.jumlahPakai();
                barang.setStok(stokBaru);

                if (!barangDAO.updateBarang(barang)) {
                    throw new IllegalStateException("Gagal memperbarui stok " + barang.getNamaBarang() + ".");
                }

                riwayatDAO.insertRiwayat(new Riwayat(
                        0,
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")),
                        "Penggunaan barang - " + cmbMenu.getValue().getNamaMenu()
                                + " (" + spJumlah.getValue() + " porsi)",
                        barang.getNamaBarang(),
                        stokLama,
                        stokBaru,
                        getUserLog()));
            }

            AlertHelper.showInformation("Stok barang berhasil digunakan.");
            resetForm();
        } catch (IllegalArgumentException | IllegalStateException exception) {
            lblStatus.setText(exception.getMessage());
            AlertHelper.showError(exception.getMessage());
        }
    }

    @FXML
    private void resetForm() {
        cmbMenu.setValue(null);
        cmbMenu.getSelectionModel().clearSelection();
        spJumlah.getValueFactory().setValue(1);
        clearPreview();
        lblStatus.setText("Belum ada transaksi.");
        btnGunakan.setDisable(true);
    }

    @FXML
    private void backDashboard() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/simsw/view/Dashboard.fxml"));
        Stage stage = (Stage) btnDashboard.getScene().getWindow();
        SceneNavigator.show(stage, root, "Dashboard");
    }

    private List<UsageItem> createUsageItems() {
        Menu menu = cmbMenu.getValue();
        if (menu == null) {
            throw new IllegalArgumentException("Pilih menu terlebih dahulu.");
        }

        int jumlahPorsi = spJumlah.getValue();
        List<Resep> resepList = resepDAO.getResepByMenu(menu.getId());
        if (resepList.isEmpty()) {
            throw new IllegalArgumentException("Resep untuk menu " + menu.getNamaMenu() + " belum tersedia.");
        }

        Map<Integer, Barang> barangById = new HashMap<>();
        for (Barang barang : barangDAO.getAllBarang()) {
            barangById.put(barang.getId(), barang);
        }

        Map<Integer, Integer> jumlahPerBarang = new HashMap<>();
        for (Resep resep : resepList) {
            Barang barang = barangById.get(resep.getIdBarang());
            if (barang == null) {
                throw new IllegalArgumentException("Barang pada resep tidak ditemukan.");
            }

            int jumlahPakai;
            try {
                jumlahPakai = Math.multiplyExact(resep.getJumlahPakai(), jumlahPorsi);
            } catch (ArithmeticException exception) {
                throw new IllegalArgumentException("Jumlah pemakaian resep tidak valid.");
            }
            if (jumlahPakai <= 0) {
                throw new IllegalArgumentException("Jumlah pemakaian resep tidak valid.");
            }
            jumlahPerBarang.merge(barang.getId(), jumlahPakai, Integer::sum);
        }

        List<UsageItem> usageItems = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : jumlahPerBarang.entrySet()) {
            usageItems.add(new UsageItem(barangById.get(entry.getKey()), entry.getValue()));
        }
        return usageItems;
    }

    private List<PreviewBarang> toPreview(List<UsageItem> usageItems) {
        return usageItems.stream()
                .map(item -> new PreviewBarang(
                        item.barang().getNamaBarang(),
                        item.barang().getStok(),
                        item.jumlahPakai(),
                        item.barang().getStok() - item.jumlahPakai()))
                .toList();
    }

    private String namaBarang(List<UsageItem> usageItems) {
        return usageItems.stream()
                .map(item -> item.barang().getNamaBarang())
                .reduce((first, second) -> first + ", " + second)
                .orElse("-");
    }

    private void clearPreview() {
        previewItems.clear();
    }

    private String getUserLog() {
        if (!Session.isLogin()) {
            return "Sistem";
        }
        return Session.getCurrentUser().getNama() + " (" + Session.getCurrentUser().getRole() + ")";
    }

    private record UsageItem(Barang barang, int jumlahPakai) {
    }
}
