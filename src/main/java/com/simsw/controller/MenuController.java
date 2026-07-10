package com.simsw.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import com.simsw.dao.xstream.MenuXStreamDAO;
import com.simsw.model.Menu;

public class MenuController {

    // FORM

    @FXML
    private TextField txtNamaMenu;

    @FXML
    private TextField txtHarga;

    @FXML
    private TextField txtSearch;

    @FXML
    private ComboBox<String> cmbKategori;

    @FXML
    private ComboBox<String> cmbStatus;

    // BUTTON
    @FXML
    private Button btnDashboard;

    @FXML
    private Button btnTambah;

    @FXML
    private Button btnKelolaResep;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnReset;

    // TABLE

    @FXML
    private TableView<Menu> tableMenu;

    @FXML
    private TableColumn<Menu, Integer> colId;

    @FXML
    private TableColumn<Menu, String> colNama;

    @FXML
    private TableColumn<Menu, String> colKategori;

    @FXML
    private TableColumn<Menu, Integer> colHarga;

    @FXML
    private TableColumn<Menu, String> colStatus;

    // VARIABLE

    private Menu selectedMenu = null;

    private ObservableList<Menu> masterData = FXCollections.observableArrayList();

    // INITIALIZE
    @FXML
    public void initialize() {
        // isi kategori
        cmbKategori.getItems().addAll("Makanan", "Minuman", "Snack", "Paket");

        // isi status
        cmbStatus.getItems().addAll("Aktif", "Nonaktif");

        loadTable();
        clearForm();
        setupSearch();

        tableMenu.getSelectionModel().selectedItemProperty().addListener((obs, oldItem, newItem) -> {
            if (newItem == null) {
                selectedMenu = null;
                return;
            }

            selectedMenu = newItem;
            txtNamaMenu.setText(newItem.getNamaMenu());
            txtHarga.setText(String.valueOf(newItem.getHarga()));
            cmbKategori.setValue(newItem.getKategori());
            cmbStatus.setValue(newItem.getStatus());
            btnTambah.setText("Update Menu");
        });
    }

    // LOAD TABLE
    private void loadTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNama.setCellValueFactory(new PropertyValueFactory<>("namaMenu"));
        colKategori.setCellValueFactory(new PropertyValueFactory<>("kategori"));
        colHarga.setCellValueFactory(new PropertyValueFactory<>("harga"));
        colStatus.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getStatus()));

        MenuXStreamDAO dao = new MenuXStreamDAO();
        List<Menu> list = dao.getAllMenu();

        masterData.setAll(list);
        tableMenu.setItems(masterData);
    }

    // SEARCH
    private void setupSearch() {
        FilteredList<Menu> filteredData = new FilteredList<>(masterData, b -> true);

        txtSearch.textProperty().addListener((obs, oldValue, newValue) -> {
            filteredData.setPredicate(menu -> {
                if (newValue == null || newValue.isBlank()) {
                    return true;
                }

                String keyword = newValue.toLowerCase();
                return menu.getNamaMenu().toLowerCase().contains(keyword)
                        || menu.getKategori().toLowerCase().contains(keyword);
            });
        });

        SortedList<Menu> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableMenu.comparatorProperty());
        tableMenu.setItems(sortedData);
    }

    // CLEAR FORM
    private void clearForm() {
        txtNamaMenu.clear();
        txtHarga.clear();

        cmbKategori.getSelectionModel().clearSelection();
        cmbStatus.getSelectionModel().clearSelection();

        selectedMenu = null;

        tableMenu.getSelectionModel().clearSelection();
        tableMenu.refresh();

        btnTambah.setText("Tambah / Update Menu");
    }

    // SAVE MENU (INSERT / UPDATE)
    @FXML
    private void saveMenu(ActionEvent event) {
        try {
            String namaMenu = txtNamaMenu.getText().trim();
            String kategori = cmbKategori.getValue();
            String status = cmbStatus.getValue();

            if (namaMenu.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setHeaderText(null);
                alert.setContentText("Nama menu wajib diisi.");
                alert.showAndWait();
                return;
            }

            if (kategori == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setHeaderText(null);
                alert.setContentText("Kategori belum dipilih.");
                alert.showAndWait();
                return;
            }

            if (status == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setHeaderText(null);
                alert.setContentText("Status belum dipilih.");
                alert.showAndWait();
                return;
            }

            int harga;
            try {
                harga = Integer.parseInt(txtHarga.getText());
            } catch (NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setContentText("Harga harus berupa angka.");
                alert.showAndWait();
                return;
            }

            MenuXStreamDAO dao = new MenuXStreamDAO();
            boolean sukses;

            // INSERT / UPDATE LOGIC
            if (selectedMenu == null) {
                Menu menu = new Menu(0, namaMenu, kategori, harga, status);
                sukses = dao.insertMenu(menu);
            } else {
                selectedMenu.setNamaMenu(namaMenu);
                selectedMenu.setKategori(kategori);
                selectedMenu.setHarga(harga);
                selectedMenu.setStatus(status);
                sukses = dao.updateMenu(selectedMenu);
            }


            // ALERT NOTIFICATION
            if (sukses) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText(null);

                if (selectedMenu == null) {
                    alert.setContentText("Menu berhasil ditambahkan.");
                } else {
                    alert.setContentText("Menu berhasil diperbarui.");
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

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Terjadi kesalahan.");
            alert.showAndWait();
        }
    }

    @FXML
    private void openKelolaResep(ActionEvent event) {
        // Tulis logika untuk membuka menu resep di sini
        System.out.println("Tombol kelola resep diklik!");
    }

    // DELETE MENU
    @FXML
    private void deleteMenu(ActionEvent event) {
        if (selectedMenu == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText(null);
            alert.setContentText("Pilih menu yang ingin dihapus.");
            alert.showAndWait();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Konfirmasi");
        confirm.setHeaderText("Hapus Menu");
        confirm.setContentText("Yakin ingin menghapus \"" + selectedMenu.getNamaMenu() + "\" ?");

        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            MenuXStreamDAO dao = new MenuXStreamDAO();
            boolean sukses = dao.deleteMenu(selectedMenu.getId());

            if (sukses) {
                Alert info = new Alert(Alert.AlertType.INFORMATION);
                info.setHeaderText(null);
                info.setContentText("Menu berhasil dihapus.");
                info.showAndWait();

                loadTable();
                clearForm();
            } else {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setHeaderText(null);
                error.setContentText("Gagal menghapus menu.");
                error.showAndWait();
            }
        }
    }

    // RESET FORM
    @FXML
    private void resetForm(ActionEvent event) {
        clearForm();
    }

    // BACK DASHBOARD
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
