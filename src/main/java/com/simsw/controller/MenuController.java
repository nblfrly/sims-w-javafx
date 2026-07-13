package com.simsw.controller;

import com.simsw.dao.xstream.MenuXStreamDAO;
import com.simsw.model.Menu;
import com.simsw.util.AlertHelper;
import com.simsw.util.SceneNavigator;
import com.simsw.util.Session;
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
import javafx.util.StringConverter;

public class MenuController {

    private static final List<String> DEFAULT_CATEGORIES = List.of("Makanan", "Minuman", "Snack", "Paket");

    @FXML private ComboBox<Menu> cmbNamaMenu;
    @FXML private TextField txtHarga;
    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cmbKategori;
    @FXML private ComboBox<String> cmbStatus;
    @FXML private Button btnTambah;
    @FXML private Button btnKelolaResep;
    @FXML private TableView<Menu> tableMenu;
    @FXML private TableColumn<Menu, Integer> colId;
    @FXML private TableColumn<Menu, String> colNama;
    @FXML private TableColumn<Menu, String> colKategori;
    @FXML private TableColumn<Menu, Integer> colHarga;
    @FXML private TableColumn<Menu, String> colStatus;

    private final MenuXStreamDAO menuDAO = new MenuXStreamDAO();
    private final ObservableList<Menu> masterData = FXCollections.observableArrayList();
    private final ObservableList<String> categoryOptions = FXCollections.observableArrayList();
    private Menu selectedMenu;
    private boolean fillingForm;

    @FXML
    public void initialize() {
        setupTable();
        setupSearch();
        setupNameSuggestions();
        setupCategorySuggestions();
        cmbStatus.getItems().setAll("Aktif", "Nonaktif");
        loadTable();

        tableMenu.getSelectionModel().selectedItemProperty().addListener((obs, oldItem, newItem) -> {
            if (newItem != null) {
                fillForm(newItem);
            }
        });
        resetForm(null);
    }

    private void setupTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNama.setCellValueFactory(new PropertyValueFactory<>("namaMenu"));
        colKategori.setCellValueFactory(new PropertyValueFactory<>("kategori"));
        colHarga.setCellValueFactory(new PropertyValueFactory<>("harga"));
        colStatus.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getStatus()));
    }

    private void setupSearch() {
        FilteredList<Menu> filteredData = new FilteredList<>(masterData, menu -> true);
        txtSearch.textProperty().addListener((obs, oldValue, newValue) -> filteredData.setPredicate(menu -> {
            if (newValue == null || newValue.isBlank()) {
                return true;
            }
            String keyword = newValue.toLowerCase(Locale.ROOT);
            return menu.getNamaMenu().toLowerCase(Locale.ROOT).contains(keyword)
                    || menu.getKategori().toLowerCase(Locale.ROOT).contains(keyword);
        }));
        SortedList<Menu> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableMenu.comparatorProperty());
        tableMenu.setItems(sortedData);
    }

    private void setupNameSuggestions() {
        cmbNamaMenu.setEditable(true);
        cmbNamaMenu.setConverter(new StringConverter<>() {
            @Override
            public String toString(Menu menu) {
                return menu == null ? "" : menu.getNamaMenu();
            }

            @Override
            public Menu fromString(String nama) {
                if (nama == null || nama.isBlank()) {
                    return null;
                }
                return masterData.stream()
                        .filter(menu -> menu.getNamaMenu().equalsIgnoreCase(nama.trim()))
                        .findFirst()
                        .orElse(null);
            }
        });
    
        cmbNamaMenu.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (fillingForm) return;
            if (!(newValue instanceof Menu)) return;

            Menu menu = (Menu) newValue;
            fillForm(menu);
        });

        cmbNamaMenu.setOnAction(event -> {
            Menu pilihan = cmbNamaMenu.getSelectionModel().getSelectedItem();
            if (!fillingForm && pilihan != null) {
                fillForm(pilihan);
            }
        });

        cmbNamaMenu.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            if (!fillingForm && selectedMenu != null 
                    && !selectedMenu.getNamaMenu().equalsIgnoreCase(newValue.trim())) {
                selectedMenu = null;
                tableMenu.getSelectionModel().clearSelection();
                btnTambah.setText("Tambah Menu");
            }
            filterMenuSuggestions(newValue);
        });
    }

    private void setupCategorySuggestions() {
        cmbKategori.setEditable(true);
        cmbKategori.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            if (!fillingForm) {
                filterCategorySuggestions(newValue);
            }
        });
    }

    private void loadTable() {
        masterData.setAll(menuDAO.getAllMenu());
        categoryOptions.setAll(DEFAULT_CATEGORIES);
        masterData.stream().map(Menu::getKategori).filter(kategori -> !categoryOptions.contains(kategori))
                .forEach(categoryOptions::add);
        cmbNamaMenu.getItems().setAll(masterData);
        cmbKategori.getItems().setAll(categoryOptions);
    }

    private void filterMenuSuggestions(String query) {
        String keyword = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        List<Menu> matches = masterData.stream()
                .filter(menu -> menu.getNamaMenu().toLowerCase(Locale.ROOT).contains(keyword))
                .toList();
        cmbNamaMenu.getItems().setAll(matches);
        if (!keyword.isBlank() && !matches.isEmpty() && cmbNamaMenu.isFocused()) {
            cmbNamaMenu.show();
        }
    }

    private void filterCategorySuggestions(String query) {
        String keyword = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        List<String> matches = categoryOptions.stream()
                .filter(kategori -> kategori.toLowerCase(Locale.ROOT).contains(keyword))
                .toList();
        cmbKategori.getItems().setAll(matches);
        if (!keyword.isBlank() && !matches.isEmpty() && cmbKategori.isFocused()) {
            cmbKategori.show();
        }
    }

    private void fillForm(Menu menu) {
        fillingForm = true;
        selectedMenu = menu;
        cmbNamaMenu.setValue(menu);
        cmbNamaMenu.getEditor().setText(menu.getNamaMenu());
        txtHarga.setText(String.valueOf(menu.getHarga()));
        cmbKategori.setValue(menu.getKategori());
        cmbStatus.setValue(menu.getStatus());
        btnTambah.setText("Update Menu");
        fillingForm = false;
    }

    @FXML
    private void resetForm(ActionEvent event) {
        fillingForm = true;

        cmbNamaMenu.setValue(null);
        cmbNamaMenu.getSelectionModel().clearSelection();
        cmbNamaMenu.getEditor().clear();
        cmbNamaMenu.getItems().setAll(masterData);

        txtHarga.clear();
        cmbKategori.getSelectionModel().clearSelection();
        cmbKategori.getEditor().clear();
        cmbKategori.getItems().setAll(categoryOptions);
        cmbStatus.getSelectionModel().clearSelection();
        
        selectedMenu = null;
        tableMenu.getSelectionModel().clearSelection();
        btnTambah.setText("Tambah Menu");
        fillingForm = false;
    }

    @FXML
    private void saveMenu(ActionEvent event) {
        if (!isAdmin()) {
            AlertHelper.showWarning("Anda tidak memiliki hak akses untuk menambah atau memperbarui menu warmindo.");
            return;
        }
        try {
            String namaMenu = cmbNamaMenu.getEditor().getText().trim();
            String kategori = cmbKategori.getEditor().getText().trim();
            String status = cmbStatus.getValue();
            int harga = Integer.parseInt(txtHarga.getText().trim());

            if (namaMenu.isEmpty() || kategori.isEmpty() || status == null) {
                AlertHelper.showWarning("Nama menu, kategori, dan status wajib diisi.");
                return;
            }
            if (harga < 0) {
                AlertHelper.showWarning("Harga tidak boleh negatif.");
                return;
            }

            Optional<Menu> duplicate = masterData.stream()
                    .filter(menu -> menu.getNamaMenu().equalsIgnoreCase(namaMenu))
                    .filter(menu -> selectedMenu == null || menu.getId() != selectedMenu.getId())
                    .findFirst();
            if (duplicate.isPresent()) {
                AlertHelper.showWarning("Menu '" + duplicate.get().getNamaMenu()
                        + "' sudah ada. Pilih dari saran untuk memperbarui datanya.");
                return;
            }

            boolean isNew = selectedMenu == null;
            Menu menu = isNew ? new Menu(0, namaMenu, kategori, harga, status) : selectedMenu;
            menu.setNamaMenu(namaMenu);
            menu.setKategori(kategori);
            menu.setHarga(harga);
            menu.setStatus(status);

            boolean sukses = isNew ? menuDAO.insertMenu(menu) : menuDAO.updateMenu(menu);
            if (!sukses) {
                AlertHelper.showError("Data menu gagal disimpan.");
                return;
            }
            AlertHelper.showInformation(isNew ? "Menu berhasil ditambahkan." : "Menu berhasil diperbarui.");
            loadTable();
            resetForm(null);
        } catch (NumberFormatException exception) {
            AlertHelper.showWarning("Harga harus berupa angka.");
        }
    }

    @FXML
    private void deleteMenu(ActionEvent event) {
        if (!isAdmin()) {
            AlertHelper.showWarning("Anda tidak memiliki hak akses untuk menghapus menu warmindo.");
            return;
        }
        if (selectedMenu == null) {
            AlertHelper.showWarning("Pilih menu yang ingin dihapus.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Konfirmasi");
        confirm.setHeaderText("Hapus Menu");
        confirm.setContentText("Yakin ingin menghapus '" + selectedMenu.getNamaMenu() + "'?");
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }
        if (!menuDAO.deleteMenu(selectedMenu.getId())) {
            AlertHelper.showError("Menu gagal dihapus.");
            return;
        }
        AlertHelper.showInformation("Menu berhasil dihapus.");
        loadTable();
        resetForm(null);
    }

    @FXML
    private void openKelolaResep(ActionEvent event) {
        try {
            SidebarController.setActivePage("KelolaResep");
            Parent root = FXMLLoader.load(getClass().getResource("/com/simsw/view/KelolaResep.fxml"));
            Stage stage = (Stage) btnKelolaResep.getScene().getWindow();
            SceneNavigator.show(stage, root, "Kelola Resep");
        } catch (Exception exception) {
            AlertHelper.showError("Halaman kelola resep tidak dapat dibuka.");
        }
    }

    private boolean isAdmin() {
        return Session.isLogin() && "Admin".equalsIgnoreCase(Session.getCurrentUser().getRole());
    }
}
