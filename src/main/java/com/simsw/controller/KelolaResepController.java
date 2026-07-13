package com.simsw.controller;

import com.simsw.dao.xstream.BarangXStreamDAO;
import com.simsw.dao.xstream.MenuXStreamDAO;
import com.simsw.dao.xstream.ResepXStreamDAO;
import com.simsw.model.Barang;
import com.simsw.model.Menu;
import com.simsw.model.Resep;

import java.net.URL;
import com.simsw.util.AlertHelper;
import com.simsw.util.SceneNavigator;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class KelolaResepController implements Initializable {

    // FORM COMPONENTS
    @FXML private ComboBox<Menu> cmbMenu;
    
    @FXML private ComboBox<Barang> cmbBarang;
    
    @FXML private TextField txtJumlah;
    
    @FXML private TextField txtSearch;
    
    @FXML private Button btnTambah;
    
    @FXML private Button btnDelete;
    
    @FXML private Button btnReset;
    
    @FXML private Button btnMenu;

    // TABLE COMPONENTS
    @FXML 
    private TableView<Resep> tableResep;

    @FXML 
    private TableColumn<Resep, Integer> colId;

    @FXML 
    private TableColumn<Resep, String> colMenu;

    @FXML 
    private TableColumn<Resep, String> colBarang;

    @FXML 
    private TableColumn<Resep, Integer> colJumlah;


    // DAO & DATA PROPERTIES
    private final ResepXStreamDAO resepDAO = new ResepXStreamDAO();
    private final MenuXStreamDAO menuDAO = new MenuXStreamDAO();
    private final BarangXStreamDAO barangDAO = new BarangXStreamDAO();

    private ObservableList<Resep> resepList = FXCollections.observableArrayList();
    private Resep selectedResep;

    // INITIALIZE
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadComboBox();
        loadTable();
        refreshTable();


        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> {
            searchResep();
        });
        tableResep.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedResep = newVal;
                pilihResep();
            }
        });
    }

    private void loadComboBox() {
        cmbMenu.getItems().clear();
        cmbBarang.getItems().clear();
        cmbMenu.getItems().addAll(menuDAO.getAllMenu());
        cmbBarang.getItems().addAll(barangDAO.getAllBarang());
    }

    private void loadTable() {
        colId.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getId()).asObject());
        colMenu.setCellValueFactory(cell -> new SimpleStringProperty(getNamaMenu(cell.getValue().getIdMenu())));
        colBarang.setCellValueFactory(cell -> new SimpleStringProperty(getNamaBarang(cell.getValue().getIdBarang())));
        colJumlah.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getJumlahPakai()).asObject());
        
        refreshTable();
    }

    private void refreshTable() {
        resepList.setAll(resepDAO.getAllResep());
        tableResep.setItems(resepList);
    }

    // CLICK TABLE ACTION
    @FXML
    private void pilihResep() {
        selectedResep = tableResep.getSelectionModel().getSelectedItem();
        if (selectedResep == null) {
            return;
        }

        txtJumlah.setText(String.valueOf(selectedResep.getJumlahPakai()));
        btnTambah.setText("Update Resep");

        // Pilih menu otomatis di ComboBox
        for (Menu menu : cmbMenu.getItems()) {
            if (menu.getId() == selectedResep.getIdMenu()) {
                cmbMenu.getSelectionModel().select(menu);
                break;
            }
        }

        // Pilih barang otomatis di ComboBox
        for (Barang barang : cmbBarang.getItems()) {
            if (barang.getId() == selectedResep.getIdBarang()) {
                cmbBarang.getSelectionModel().select(barang);
                break;
            }
        }
    }

    // SAVE / INSERT / UPDATE
    @FXML
    private void saveResep() {
        try {
            if (!validasiInput()) {
                return;
            }

            Menu menu = cmbMenu.getSelectionModel().getSelectedItem();
            Barang barang = cmbBarang.getSelectionModel().getSelectedItem();
            int jumlah = Integer.parseInt(txtJumlah.getText());

            // PROSES INSERT
            if (selectedResep == null) {
                if (resepDAO.exists(menu.getId(), barang.getId())) {
                    showWarning("Resep sudah ada.");
                    return;
                }

                Resep resep = new Resep();
                resep.setIdMenu(menu.getId());
                resep.setIdBarang(barang.getId());
                resep.setJumlahPakai(jumlah);

                resepDAO.insertResep(resep);
                showInformation("Resep berhasil ditambahkan.");
            } 
            // PROSES UPDATE
            else {
                selectedResep.setIdMenu(menu.getId());
                selectedResep.setIdBarang(barang.getId());
                selectedResep.setJumlahPakai(jumlah);

                resepDAO.updateResep(selectedResep);
                showInformation("Resep berhasil diperbarui.");
            }

            refreshTable();
            resetForm();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // DELETE
    @FXML
    private void deleteResep() {
        if (selectedResep == null) {
            showWarning("Pilih resep terlebih dahulu.");
            return;
        }

        resepDAO.deleteResep(selectedResep.getId());
        showInformation("Resep berhasil dihapus.");
        
        refreshTable();
        resetForm();
    }

    // RESET FORM
    @FXML
    private void resetForm() {
        txtJumlah.clear();
        cmbMenu.getSelectionModel().clearSelection();
        cmbBarang.getSelectionModel().clearSelection();
        tableResep.getSelectionModel().clearSelection();
        selectedResep = null;
        btnTambah.setText("Tambah Resep");
    }

    // VALIDASI INPUT
    private boolean validasiInput() {
        if (cmbMenu.getSelectionModel().isEmpty()) {
            showWarning("Pilih menu.");
            return false;
        }

        if (cmbBarang.getSelectionModel().isEmpty()) {
            showWarning("Pilih barang.");
            return false;
        }

        if (txtJumlah.getText().trim().isEmpty()) {
            showWarning("Jumlah belum diisi.");
            return false;
        }

        try {
            int jumlah = Integer.parseInt(txtJumlah.getText());
            if (jumlah <= 0) {
                showWarning("Jumlah harus lebih dari nol.");
                return false;
            }
        } catch (NumberFormatException e) {
            showWarning("Jumlah harus berupa angka.");
            return false;
        }

        return true;
    }

    @FXML
    private void searchResep() {
        String keyword = txtSearch.getText().toLowerCase().trim();
        ObservableList<Resep> hasil = FXCollections.observableArrayList();
        for (Resep resep : resepDAO.getAllResep()) {
            String namaMenu = getNamaMenu(resep.getIdMenu()).toLowerCase();
            String namaBarang = getNamaBarang(resep.getIdBarang()).toLowerCase();
            if (namaMenu.contains(keyword)
                    || namaBarang.contains(keyword)) {
                hasil.add(resep);
            }
        }
        tableResep.setItems(hasil);
    }

    @FXML
    private void backMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/simsw/view/Menu.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnMenu.getScene().getWindow();
            SceneNavigator.show(stage, root, "Menu Warmindo");
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    @FXML
    private void refreshData(){
        loadComboBox();
        refreshTable();
    }

    // HELPER METHODS
    private String getNamaMenu(int idMenu) {
        List<Menu> menuList = menuDAO.getAllMenu();
        for (Menu menu : menuList) {
            if (menu.getId() == idMenu) {
                return menu.getNamaMenu();
            }
        }
        return "-";
    }

    private String getNamaBarang(int idBarang) {
        List<Barang> barangList = barangDAO.getAllBarang();
        for (Barang barang : barangList) {
            if (barang.getId() == idBarang) {
                return barang.getNamaBarang();
            }
        }
        return "-";
    }

    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Peringatan");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInformation(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informasi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
