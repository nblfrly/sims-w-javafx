package com.simsw.controller;

import com.simsw.dao.xstream.BarangXStreamDAO;
import com.simsw.dao.xstream.RiwayatXStreamDAO;
import com.simsw.model.Barang;
import com.simsw.model.Riwayat;
import com.simsw.util.AlertHelper;
import com.simsw.util.Session;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

public class InventarisController {

    private static final List<String> DEFAULT_CATEGORIES = List.of("Mie", "Minuman", "Topping", "Snack");

    @FXML private ComboBox<Barang> cmbNama;
    @FXML private TextField stockField;
    @FXML private TextField alertField;
    @FXML private TextField searchField;
    @FXML private Button addButton;
    @FXML private ComboBox<String> categoryCombo;
    @FXML private TableView<Barang> inventoryTable;
    @FXML private TableColumn<Barang, Integer> idColumn;
    @FXML private TableColumn<Barang, String> nameColumn;
    @FXML private TableColumn<Barang, String> categoryColumn;
    @FXML private TableColumn<Barang, Integer> stockColumn;
    @FXML private TableColumn<Barang, String> statusColumn;

    private final BarangXStreamDAO barangDAO = new BarangXStreamDAO();
    private final RiwayatXStreamDAO riwayatDAO = new RiwayatXStreamDAO();
    private final ObservableList<Barang> masterData = FXCollections.observableArrayList();
    private final ObservableList<String> categoryOptions = FXCollections.observableArrayList();
    private FilteredList<Barang> filteredData;
    private Barang selectedBarang;
    private boolean fillingForm;
    private boolean stokMenipisOnly;

    @FXML
    public void initialize() {
        setupTable();
        setupSearch();
        setupNameSuggestions();
        setupCategorySuggestions();
        loadTable();

        inventoryTable.getSelectionModel().selectedItemProperty().addListener((obs, oldItem, newItem) -> {
            if (newItem != null) {
                fillForm(newItem);
            }
        });
        resetForm(null);
    }

    private void setupTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("namaBarang"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("kategori"));
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stok"));
        statusColumn.setCellValueFactory(cell -> {
            Barang barang = cell.getValue();
            String status = barang.getStok() == 0
                    ? "Habis"
                    : barang.getStok() <= barang.getStokMinimum() ? "Menipis" : "Aman";
            return new SimpleStringProperty(status);
        });
    }

    private void setupSearch() {
        filteredData = new FilteredList<>(masterData, barang -> true);
        searchField.textProperty().addListener((obs, oldValue, newValue) -> applyTableFilter());
        SortedList<Barang> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(inventoryTable.comparatorProperty());
        inventoryTable.setItems(sortedData);
    }

    private void setupNameSuggestions() {
        cmbNama.setEditable(true);
        cmbNama.setConverter(new StringConverter<>() {
            @Override
            public String toString(Barang barang) {
                return barang == null ? "" : barang.getNamaBarang();
            }

            @Override
            public Barang fromString(String nama) {
                if (nama == null || nama.isBlank()) {
                    return null;
                }
                return masterData.stream()
                        .filter(barang -> barang.getNamaBarang().equalsIgnoreCase(nama.trim()))
                        .findFirst()
                        .orElse(null);
            }
        });
        
        cmbNama.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (fillingForm) return;
            if (!(newValue instanceof Barang)) return;

            Barang barang = (Barang) newValue;
            fillForm(barang);
        });

        cmbNama.setOnAction(event -> {
            Barang pilihan = cmbNama.getSelectionModel().getSelectedItem();
            if (!fillingForm && pilihan != null) {
                fillForm(pilihan);
            }
        });

        cmbNama.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            if (!fillingForm && selectedBarang != null 
                    && !selectedBarang.getNamaBarang().equalsIgnoreCase(newValue.trim())) {
                selectedBarang = null;
                inventoryTable.getSelectionModel().clearSelection();
                addButton.setText("Tambah Item");
            }
            filterNameSuggestions(newValue);
        });
    }

    private void setupCategorySuggestions() {
        categoryCombo.setEditable(true);
        categoryCombo.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            if (!fillingForm) {
                filterCategorySuggestions(newValue);
            }
        });
    }

    private void loadTable() {
        masterData.setAll(barangDAO.getAllBarang());
        categoryOptions.setAll(DEFAULT_CATEGORIES);
        masterData.stream().map(Barang::getKategori).filter(kategori -> !categoryOptions.contains(kategori))
                .forEach(categoryOptions::add);
        cmbNama.getItems().setAll(masterData);
        categoryCombo.getItems().setAll(categoryOptions);
        applyTableFilter();
    }

    private void applyTableFilter() {
        if (filteredData == null) {
            return;
        }
        String keyword = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase(Locale.ROOT);
        filteredData.setPredicate(barang -> {
            boolean cocokPencarian = keyword.isBlank()
                    || barang.getNamaBarang().toLowerCase(Locale.ROOT).contains(keyword)
                    || barang.getKategori().toLowerCase(Locale.ROOT).contains(keyword)
                    || barang.getStatus().toLowerCase(Locale.ROOT).contains(keyword);
            boolean stokMenipis = !stokMenipisOnly || barang.getStok() <= barang.getStokMinimum();
            return cocokPencarian && stokMenipis;
        });
    }

    public void tampilkanStokMenipis() {
        stokMenipisOnly = true;
        searchField.clear();
        applyTableFilter();
    }

    private void filterNameSuggestions(String query) {
        String keyword = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        List<Barang> matches = masterData.stream()
                .filter(barang -> barang.getNamaBarang().toLowerCase(Locale.ROOT).contains(keyword))
                .toList();
        cmbNama.getItems().setAll(matches);
        if (!keyword.isBlank() && !matches.isEmpty() && cmbNama.isFocused()) {
            cmbNama.show();
        }
    }

    private void filterCategorySuggestions(String query) {
        String keyword = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        List<String> matches = categoryOptions.stream()
                .filter(kategori -> kategori.toLowerCase(Locale.ROOT).contains(keyword))
                .toList();
        categoryCombo.getItems().setAll(matches);
        if (!keyword.isBlank() && !matches.isEmpty() && categoryCombo.isFocused()) {
            categoryCombo.show();
        }
    }

    private void fillForm(Barang barang) {
        fillingForm = true;
        selectedBarang = barang;
        cmbNama.setValue(barang);
        cmbNama.getEditor().setText(barang.getNamaBarang());
        categoryCombo.setValue(barang.getKategori());
        stockField.setText(String.valueOf(barang.getStok()));
        alertField.setText(String.valueOf(barang.getStokMinimum()));
        addButton.setText("Update Item");
        fillingForm = false;
    }

    @FXML
    private void resetForm(ActionEvent event) {
        fillingForm = true;
        cmbNama.setValue(null);
        cmbNama.getSelectionModel().clearSelection();
        cmbNama.getEditor().clear();
        cmbNama.getItems().setAll(masterData);

        categoryCombo.getSelectionModel().clearSelection();
        categoryCombo.getEditor().clear();
        categoryCombo.getItems().setAll(categoryOptions);

        stockField.clear();
        alertField.clear();
        selectedBarang = null;
        
        inventoryTable.getSelectionModel().clearSelection();
        addButton.setText("Tambah Item");
        fillingForm = false;
    }

    @FXML
    private void saveItem(ActionEvent event) {
        if (!isAdmin()) {
            AlertHelper.showWarning("Anda tidak memiliki hak akses untuk menambah atau memperbarui inventaris.");
            return;
        }
        try {
            String nama = cmbNama.getEditor().getText().trim();
            String kategori = categoryCombo.getEditor().getText().trim();
            int stok = Integer.parseInt(stockField.getText().trim());
            int stokMinimum = Integer.parseInt(alertField.getText().trim());

            if (nama.isEmpty() || kategori.isEmpty()) {
                AlertHelper.showWarning("Nama item dan kategori wajib diisi.");
                return;
            }
            if (stok < 0 || stokMinimum < 0) {
                AlertHelper.showWarning("Stok dan batas minimum tidak boleh negatif.");
                return;
            }
            if (categoryOptions.stream().noneMatch(item -> item.equalsIgnoreCase(kategori))
                    && !confirmNewCategory(kategori)) {
                return;
            }

            Optional<Barang> duplicate = masterData.stream()
                    .filter(barang -> barang.getNamaBarang().equalsIgnoreCase(nama))
                    .filter(barang -> selectedBarang == null || barang.getId() != selectedBarang.getId())
                    .findFirst();
            if (duplicate.isPresent()) {
                AlertHelper.showWarning("Item '" + duplicate.get().getNamaBarang()
                        + "' sudah ada. Pilih dari saran untuk memperbarui datanya.");
                return;
            }

            boolean isNew = selectedBarang == null;
            int stokLama = isNew ? 0 : selectedBarang.getStok();
            Barang barang = isNew ? new Barang(0, nama, kategori, stok, stokMinimum) : selectedBarang;
            barang.setNamaBarang(nama);
            barang.setKategori(kategori);
            barang.setStok(stok);
            barang.setStokMinimum(stokMinimum);

            boolean sukses = isNew ? barangDAO.insertBarang(barang) : barangDAO.updateBarang(barang);
            if (!sukses) {
                AlertHelper.showError("Data item gagal disimpan.");
                return;
            }

            riwayatDAO.insertRiwayat(new Riwayat(0,
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")),
                    isNew ? "Tambah Barang" : "Update Barang",
                    nama, stokLama, stok, getUserLog()));
            AlertHelper.showInformation(isNew ? "Item berhasil ditambahkan." : "Item berhasil diperbarui.");
            loadTable();
            resetForm(null);
        } catch (NumberFormatException exception) {
            AlertHelper.showWarning("Stok dan batas minimum harus berupa angka.");
        }
    }

    @FXML
    private void deleteItem(ActionEvent event) {
        if (!isAdmin()) {
            AlertHelper.showWarning("Anda tidak memiliki hak akses untuk menghapus inventaris.");
            return;
        }
        if (selectedBarang == null) {
            AlertHelper.showWarning("Pilih item yang ingin dihapus.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Konfirmasi");
        confirm.setHeaderText("Hapus Item");
        confirm.setContentText("Yakin ingin menghapus '" + selectedBarang.getNamaBarang() + "'?");
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        Barang barang = selectedBarang;
        if (!barangDAO.deleteBarang(barang.getId())) {
            AlertHelper.showError("Item gagal dihapus.");
            return;
        }
        riwayatDAO.insertRiwayat(new Riwayat(0,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")),
                "Hapus Barang", barang.getNamaBarang(), barang.getStok(), 0, getUserLog()));
        AlertHelper.showInformation("Item berhasil dihapus.");
        loadTable();
        resetForm(null);
    }

    private String getUserLog() {
        return Session.isLogin()
                ? Session.getCurrentUser().getNama() + " (" + Session.getCurrentUser().getRole() + ")"
                : "Sistem";
    }

    private boolean isAdmin() {
        return Session.isLogin() && "Admin".equalsIgnoreCase(Session.getCurrentUser().getRole());
    }

    private boolean confirmNewCategory(String kategori) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Kategori Baru");
        confirmation.setHeaderText("Kategori '" + kategori + "' belum tersedia.");
        confirmation.setContentText("Tambahkan kategori baru ini ke inventaris?");
        return confirmation.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }
}
