package com.simsw.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import com.simsw.dao.xstream.RiwayatXStreamDAO;
import com.simsw.model.Riwayat;
import com.simsw.util.SceneNavigator;
import com.simsw.util.Session;
import java.time.LocalDate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;

public class RiwayatController implements Initializable {

    @FXML
    private TextField txtCari;

    @FXML
    private DatePicker tanggalFilter;

    @FXML
    private Button btnReset;

    @FXML
    private Button btnExport;

    @FXML
    private Button btnDashboard;

    @FXML
    private Label lblTotalLog;

    @FXML
    private Label lblTambah;

    @FXML
    private Label lblUpdate;

    @FXML
    private Label lblHapus;

    @FXML
    private ComboBox<String> aktivitasFilter;

    @FXML
    private TableView<Riwayat> tableRiwayat;

    @FXML
    private TableColumn<Riwayat, String> colWaktu;

    @FXML
    private TableColumn<Riwayat, String> colAktivitas;

    @FXML
    private TableColumn<Riwayat, String> colBarang;

    @FXML
    private TableColumn<Riwayat, Integer> colStokLama;

    @FXML
    private TableColumn<Riwayat, Integer> colStokBaru;

    @FXML
    private TableColumn<Riwayat, String> colUser;

    // removed unused 'data' field
    private ObservableList<Riwayat> masterData = FXCollections.observableArrayList();

    private FilteredList<Riwayat> filteredData;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        colWaktu.setCellValueFactory(new PropertyValueFactory<>("waktu"));
        colAktivitas.setCellValueFactory(new PropertyValueFactory<>("aktivitas"));
        colBarang.setCellValueFactory(new PropertyValueFactory<>("namaBarang"));
        colStokLama.setCellValueFactory(new PropertyValueFactory<>("stokLama"));
        colStokBaru.setCellValueFactory(new PropertyValueFactory<>("stokBaru"));
        colUser.setCellValueFactory(new PropertyValueFactory<>("user"));

        aktivitasFilter.getItems().addAll(
                "Semua",
                "Tambah Barang",
                "Update Barang",
                "Hapus Barang"
        );

        aktivitasFilter.setValue("Semua");

        loadTable();
        setupFilter();

        if (Session.isLogin() && "Pegawai".equalsIgnoreCase(Session.getCurrentUser().getRole())) {
            btnExport.setVisible(false);
            btnExport.setManaged(false);
        }
    }

    private void loadTable() {
        RiwayatXStreamDAO dao = new RiwayatXStreamDAO();
        masterData.clear();
        masterData.addAll(dao.getAllRiwayat());

        filteredData = new FilteredList<>(masterData, p -> true);

        SortedList<Riwayat> sorted = new SortedList<>(filteredData);
        sorted.comparatorProperty().bind(tableRiwayat.comparatorProperty());
        tableRiwayat.setItems(sorted);
        
        updateStatistik();
    }

    private void updateStatistik() {
        lblTotalLog.setText(String.valueOf(masterData.size()));

        long tambah = masterData.stream()
                .filter(r -> r.getAktivitas().equals("Tambah Barang"))
                .count();

        long update = masterData.stream()
                .filter(r -> r.getAktivitas().equals("Update Barang"))
                .count();

        long hapus = masterData.stream()
                .filter(r -> r.getAktivitas().equals("Hapus Barang"))
                .count();

        lblTambah.setText(String.valueOf(tambah));
        lblUpdate.setText(String.valueOf(update));
        lblHapus.setText(String.valueOf(hapus));
    }

    public void tampilkanAktivitasHariIni() {
        tanggalFilter.setValue(LocalDate.now());
        applyFilter();
    }
    
    @FXML
    private void resetFilter(ActionEvent event){
        txtCari.clear();
        aktivitasFilter.setValue("Semua");
        tanggalFilter.setValue(null);
        applyFilter();

    }

    private void setupFilter() {
        txtCari.textProperty().addListener((obs, oldVal, newVal) -> applyFilter());
        aktivitasFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilter());
        tanggalFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilter());
    }

    private void applyFilter() {
        filteredData.setPredicate(riwayat -> {
            // search
            String keyword = txtCari.getText();

            if (keyword != null && !keyword.isBlank()) {
                keyword = keyword.toLowerCase();
                boolean cocokSearch =
                        riwayat.getNamaBarang().toLowerCase().contains(keyword)
                        || riwayat.getUser().toLowerCase().contains(keyword)
                        || riwayat.getAktivitas().toLowerCase().contains(keyword);

                if (!cocokSearch)
                    return false;
            }

            // filter aktivitas
            String aktivitas = aktivitasFilter.getValue();

            if (aktivitas != null && !aktivitas.equals("Semua")
                    && !riwayat.getAktivitas().equals(aktivitas)) {
                return false;
            }

            // filter tanggal
            LocalDate tanggalDipilih = tanggalFilter.getValue();

            if (tanggalDipilih != null) {
                String tanggalXML = riwayat.getWaktu().substring(0, 10);
                String tanggalPicker =
                        String.format("%02d-%02d-%04d",
                                tanggalDipilih.getDayOfMonth(),
                                tanggalDipilih.getMonthValue(),
                                tanggalDipilih.getYear());

                if (!tanggalXML.equals(tanggalPicker))
                    return false;
            }
            return true;
        });
    }

    @FXML
    private void backDashboard(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/simsw/view/Dashboard.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) btnDashboard.getScene().getWindow();
        SceneNavigator.show(stage, root, "Dashboard");
    }

    @FXML
    private void exportCSV(ActionEvent event) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Riwayat");

        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );

        fileChooser.setInitialFileName("riwayat.csv");

        File file = fileChooser.showSaveDialog(btnExport.getScene().getWindow());

        if (file == null) {
            return;
        }

        try (FileWriter writer = new FileWriter(file)) {

            // Header
            writer.append("Tanggal,Nama Pegawai,Nama Barang,Jumlah Lama,Jumlah Baru,Keterangan\n");

            // Data
            for (Riwayat r : tableRiwayat.getItems()) {

                writer.append(r.getWaktu().toString()).append(",");
                writer.append(r.getUser()).append(",");
                writer.append(r.getNamaBarang()).append(",");
                writer.append(String.valueOf(r.getStokLama())).append(",");
                writer.append(String.valueOf(r.getStokBaru())).append(",");
                writer.append(r.getAktivitas()).append("\n");
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Export berhasil!");
            alert.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Export gagal.");
            alert.showAndWait();
        }
    }

}
