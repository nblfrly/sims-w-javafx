package com.simsw.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TableColumn;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

import com.simsw.dao.xstream.BarangXStreamDAO;
import com.simsw.dao.xstream.RiwayatXStreamDAO;
import com.simsw.dao.dom.BarangXMLDAO;
import com.simsw.dao.mysql.BarangDAO;
import com.simsw.model.Barang;
import com.simsw.model.Riwayat;

public class InventarisController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField stockField;

    @FXML
    private TextField alertField;

    @FXML
    private TextField searchField;

    @FXML
    private Button btnDashboard;

    @FXML
    private Button addButton;

    @FXML
    private ComboBox<String> categoryCombo;

    @FXML
    private TableView<Barang> inventoryTable;

    @FXML
    private TableColumn<Barang,Integer> idColumn;

    @FXML
    private TableColumn<Barang,String> nameColumn;

    @FXML
    private TableColumn<Barang,String> categoryColumn;

    @FXML
    private TableColumn<Barang,Integer> stockColumn;

    @FXML
    private TableColumn<Barang,String> statusColumn;

    private Barang selectedBarang = null;
    private ObservableList<Barang> masterData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        long start = System.currentTimeMillis();
        categoryCombo.getItems().addAll("Mie", "Minuman", "Topping", "Snack");
        categoryCombo.setEditable(true);

        loadTable();
        clearForm();
        setupSearch();

        inventoryTable.getSelectionModel().selectedItemProperty().addListener((obs, oldItem, newItem) -> {
            if (newItem == null) {
                selectedBarang = null;
                return;
            }

            selectedBarang = newItem;
            nameField.setText(newItem.getNamaBarang());
            categoryCombo.setValue(newItem.getKategori());
            stockField.setText(String.valueOf(newItem.getStok()));
            alertField.setText(String.valueOf(newItem.getStokMinimum()));

            addButton.setText("Update Item");
        });
        System.out.println("Load Inventaris = " + (System.currentTimeMillis() - start) + " ms");
        
        BarangXStreamDAO dao = new BarangXStreamDAO();
        List<Barang> list = dao.getAllBarang();

        System.out.println("Jumlah barang xml = " + list.size());
    }

    private void loadTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("namaBarang"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("kategori"));
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stok"));

        statusColumn.setCellValueFactory(cell -> {
            Barang barang = cell.getValue();
            if (barang.getStok() <= barang.getStokMinimum()) {
                return new SimpleStringProperty("Menipis");
            } else {
                return new SimpleStringProperty("Aman");
            }
        });

        BarangXStreamDAO dao = new BarangXStreamDAO();
        masterData.setAll(dao.getAllBarang());

        // // ===== DEBUG =====
        // System.out.println("Jumlah data = " + list.size());

        // for (Barang b : list) {
        //     System.out.println(b.getId() + " | " + b.getNamaBarang() + " | " + b.getKategori() + " | " + b.getStok());
        // }

        inventoryTable.setItems(masterData);
    }

    private void setupSearch() {
        FilteredList<Barang> filteredData = new FilteredList<>(masterData, b -> true);
        searchField.textProperty().addListener((obs, oldValue, newValue) -> {
            filteredData.setPredicate(barang -> {
                if (newValue == null || newValue.isBlank()) {
                    return true;
                }

                String keyword = newValue.toLowerCase();

                return barang.getNamaBarang().toLowerCase().contains(keyword)
                        || barang.getKategori().toLowerCase().contains(keyword);
            });
        });

        SortedList<Barang> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(inventoryTable.comparatorProperty());

        inventoryTable.setItems(sortedData);
    }

    private void clearForm() {
        nameField.clear();
        stockField.clear();
        alertField.clear();

        categoryCombo.getSelectionModel().clearSelection();
        categoryCombo.getEditor().clear();

        selectedBarang = null;

        inventoryTable.getSelectionModel().clearSelection();
        inventoryTable.requestFocus();

        inventoryTable.refresh();
        addButton.setText("Add Item");
    }

    @FXML
    private void saveItem(ActionEvent event) {
        try {
            System.out.println("SAVE ITEM DIPANGGIL");
            String nama = nameField.getText().trim();
            String kategori = categoryCombo.getEditor().getText().trim();

            int stok = Integer.parseInt(stockField.getText());
            int stokMinimum = Integer.parseInt(alertField.getText());

            if (nama.isEmpty() || kategori.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setHeaderText(null);
                alert.setContentText("Nama dan kategori wajib diisi.");
                alert.showAndWait();
                return;
            }
            
         
            BarangXStreamDAO dao = new BarangXStreamDAO();
            RiwayatXStreamDAO riwayatDAO = new RiwayatXStreamDAO();

            boolean sukses;
            String aktivitas = "";
            int stokLama = 0;
            int stokBaru = stok;

            String waktuSekarang = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));

            if (selectedBarang == null) {
                // logika 1: tambah barang
                System.out.println("1");
                Barang barang = new Barang(
                    0,
                    nama,
                    kategori,
                    stok,
                    stokMinimum
                );
                
                sukses = dao.insertBarang(barang);

                if (sukses) {
                    aktivitas = "Tambah Barang";
                    stokLama = 0;

                    Riwayat riwayat = new Riwayat(
                            0,
                            waktuSekarang,
                            aktivitas,
                            nama,
                            stokLama,
                            stokBaru,
                            "Admin"
                    );
                    riwayatDAO.insertRiwayat(riwayat);
                }

            } else {
                // logika 2: update barang lama
                System.out.println("Proses Update Barang");
                stokLama = selectedBarang.getStok(); // catat stok lama sebelum ditimpa

                selectedBarang.setNamaBarang(nama);
                selectedBarang.setKategori(kategori);
                selectedBarang.setStok(stok);
                selectedBarang.setStokMinimum(stokMinimum);

                sukses = dao.updateBarang(selectedBarang);

                if (sukses) {
                    aktivitas = "Update Barang";

                    Riwayat riwayat = new Riwayat(
                            0,
                            waktuSekarang,
                            aktivitas,
                            nama,
                            stokLama,
                            stokBaru,
                            "Admin"
                    );
                    riwayatDAO.insertRiwayat(riwayat);
                }
            }

            // logika 3: alert hasil
            if (sukses) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText(null);

                if (selectedBarang == null) {
                    alert.setContentText("Data berhasil ditambahkan.");
                } else {
                    alert.setContentText("Data berhasil diperbarui.");
                }

                alert.showAndWait();

                loadTable();
                clearForm();

            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setContentText("Proses gagal.");
                alert.showAndWait();
            }

        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Stok dan stok minimum harus berupa angka.");
            alert.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Terjadi Kesalahan");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
 


    @FXML
    private void deleteItem(ActionEvent event) {
        if (selectedBarang == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Peringatan");
            alert.setHeaderText(null);
            alert.setContentText("Pilih barang yang ingin dihapus.");

            alert.showAndWait();

            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Konfirmasi");
        confirm.setHeaderText("Hapus Barang");
        confirm.setContentText("Yakin ingin menghapus \"" + selectedBarang.getNamaBarang() + "\" ?");

        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            BarangXStreamDAO dao = new BarangXStreamDAO();
            String namaBarang = selectedBarang.getNamaBarang();
            int stokLama = selectedBarang.getStok();
            boolean sukses = dao.deleteBarang(selectedBarang.getId());
            if (sukses) {
                RiwayatXStreamDAO riwayatDAO = new RiwayatXStreamDAO();
                Riwayat riwayat = new Riwayat(
                        0,
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")),
                        "Hapus Barang",
                        namaBarang,
                        stokLama,
                        0,
                        "Admin"
                );
                riwayatDAO.insertRiwayat(riwayat);
            }

            if (sukses) {
                Alert info = new Alert(Alert.AlertType.INFORMATION);
                info.setHeaderText(null);
                info.setContentText("Barang berhasil dihapus.");
                info.showAndWait();

                loadTable();
                clearForm();

            } else {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setHeaderText(null);
                error.setContentText("Gagal menghapus barang.");
                error.showAndWait();

                clearForm();
            }
        } else {
            clearForm();
        }
    }

    @FXML
    private void backDashboard(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/simsw/view/Dashboard.fxml"));

        Parent root = loader.load();
        Stage stage = (Stage) btnDashboard.getScene().getWindow();

        stage.setScene(new Scene(root));
        stage.setTitle("Dashboard");
        stage.show();
    }
}