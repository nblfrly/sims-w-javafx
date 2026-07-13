package com.simsw.controller;

import java.io.IOException;
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
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TableColumn;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

import com.simsw.dao.xstream.PegawaiXStreamDAO;
import com.simsw.dao.mysql.PegawaiDAO;
// import com.simsw.model.Barang;
import com.simsw.model.Pegawai;
import com.simsw.util.SceneNavigator;
import com.simsw.util.Session;

public class PegawaiController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ComboBox<String> roleCombo;

    @FXML
    private TextField searchField;

    @FXML
    private Button btnDashboard;

    @FXML
    private Button saveButton;

    @FXML
    private Button deleteButton;

    @FXML
    private TableView<Pegawai> pegawaiTable;

    @FXML
    private TableColumn<Pegawai, Integer> idColumn;

    @FXML
    private TableColumn<Pegawai, String> nameColumn;

    @FXML
    private TableColumn<Pegawai, String> usernameColumn;

    @FXML
    private TableColumn<Pegawai, String> roleColumn;

    private Pegawai selectedPegawai = null;

    private ObservableList<Pegawai> masterData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        long start = System.currentTimeMillis();
        roleCombo.getItems().addAll(
                "Admin",
                "Pegawai"
        );

        loadTable();
        clearForm();
        setupSearch();

        pegawaiTable.getSelectionModel().selectedItemProperty().addListener((obs, oldItem, newItem) -> {
            if (newItem == null) {
                selectedPegawai = null;
                return;
            }

            selectedPegawai = newItem;

            nameField.setText(newItem.getNama());
            usernameField.setText(newItem.getUsername());
            passwordField.setText(newItem.getPassword());
            roleCombo.setValue(newItem.getRole());

            saveButton.setText("Update Pegawai");

        });

        if (isPegawaiUser()) {
            configurePegawaiAccess();
        }

        System.out.println("Load Pegawai = " + (System.currentTimeMillis() - start) + " ms");
    }

    private void loadTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("nama"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        roleColumn.setCellValueFactory(cell -> {
            Pegawai pegawai = cell.getValue();
            return new SimpleStringProperty(pegawai.getRole());
        });

        PegawaiXStreamDAO dao = new PegawaiXStreamDAO();
        masterData.setAll(dao.getAllPegawai());
        pegawaiTable.setItems(masterData);
    }

    // part 2
    // setupSearch()
    // clearForm()

    private void setupSearch() {
        FilteredList<Pegawai> filteredData =
                new FilteredList<>(masterData, p -> true);

        searchField.textProperty().addListener((obs, oldValue, newValue) -> {
            filteredData.setPredicate(pegawai -> {
                if (newValue == null || newValue.isBlank()) {
                    return true;
                }

                String keyword = newValue.toLowerCase();

                return pegawai.getNama().toLowerCase().contains(keyword)
                        || pegawai.getUsername().toLowerCase().contains(keyword)
                        || pegawai.getRole().toLowerCase().contains(keyword);
            });
        });

        SortedList<Pegawai> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(pegawaiTable.comparatorProperty());
        pegawaiTable.setItems(sortedData);
    }

    private void clearForm() {
        nameField.clear();
        usernameField.clear();
        passwordField.clear();

        roleCombo.getSelectionModel().clearSelection();
        roleCombo.getEditor().clear();

        selectedPegawai = null;

        pegawaiTable.getSelectionModel().clearSelection();
        pegawaiTable.requestFocus();
        pegawaiTable.refresh();

        saveButton.setText("Tambah Pegawai");
    }

    // part 3
    // savePegawai()
    @FXML
    private void savePegawai(ActionEvent event) {
        if (isPegawaiUser()) {
            updateOwnPassword();
            return;
        }
        try {
            String nama = nameField.getText().trim();
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();
            String role = roleCombo.getValue();

        // Validasi
            if (nama.isEmpty()
                    || username.isEmpty()
                    || password.isEmpty()
                    || role == null
                    || role.isEmpty()) {

                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);

                alert.setHeaderText(null);
                alert.setContentText("Semua data wajib diisi.");
                alert.showAndWait();

                return;
            }

            PegawaiXStreamDAO dao = new PegawaiXStreamDAO();
            boolean sukses;

            if (selectedPegawai == null) {
                Pegawai pegawai = new Pegawai(0, nama, username, password, role);
                sukses = dao.insertPegawai(pegawai);

            } else {
                selectedPegawai.setNama(nama);
                selectedPegawai.setUsername(username);
                selectedPegawai.setPassword(password);
                selectedPegawai.setRole(role);

                sukses = dao.updatePegawai(selectedPegawai);
            }

            if (sukses) {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                alert.setHeaderText(null);

                if (selectedPegawai == null) {
                    alert.setContentText("Data pegawai berhasil ditambahkan.");
                } else {
                    alert.setContentText("Data pegawai berhasil diperbarui.");
                }

                alert.showAndWait();

                loadTable();
                clearForm();

            } else {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);

                alert.setHeaderText(null);
                alert.setContentText("Proses gagal.");
                alert.showAndWait();
            }

        } catch (Exception e) {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);

            alert.setHeaderText(null);
            alert.setContentText("Terjadi kesalahan.");
            alert.showAndWait();

            e.printStackTrace();
        }
    }

    // part 4
    // deletePegawai()

    @FXML
    private void deletePegawai(ActionEvent event) {
        if (isPegawaiUser()) {
            showWarning("Anda tidak memiliki hak akses untuk menghapus akun pegawai.");
            return;
        }
        if (selectedPegawai == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Peringatan");
            alert.setHeaderText(null);
            alert.setContentText("Pilih pegawai yang ingin dihapus.");
            alert.showAndWait();

            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Konfirmasi");
        confirm.setHeaderText("Hapus Pegawai");
        confirm.setContentText("Yakin ingin menghapus \"" + selectedPegawai.getNama() + "\" ?");

        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            PegawaiXStreamDAO dao = new PegawaiXStreamDAO();
            boolean sukses = dao.deletePegawai(selectedPegawai.getId());

            if (sukses) {
                Alert info = new Alert(Alert.AlertType.INFORMATION);
                info.setHeaderText(null);
                info.setContentText("Pegawai berhasil dihapus.");
                info.showAndWait();

                loadTable();
                clearForm();

            } else {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setHeaderText(null);
                error.setContentText("Gagal menghapus pegawai.");
                error.showAndWait();

                clearForm();
            }
        } else {
            clearForm();

        }
    }

    // part 5
    // backDashboard()

    @FXML
    private void backDashboard(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/simsw/view/Dashboard.fxml"));

        Parent root = loader.load();
        Stage stage = (Stage) btnDashboard.getScene().getWindow();

        SceneNavigator.show(stage, root, "Dashboard");
    }

    private boolean isPegawaiUser() {
        return Session.isLogin() && "Pegawai".equalsIgnoreCase(Session.getCurrentUser().getRole());
    }

    private void configurePegawaiAccess() {
        Pegawai userSession = Session.getCurrentUser();
        Pegawai akunSendiri = masterData.stream()
                .filter(pegawai -> pegawai.getId() == userSession.getId())
                .findFirst()
                .orElse(userSession);

        selectedPegawai = akunSendiri;
        nameField.setText(akunSendiri.getNama());
        usernameField.setText(akunSendiri.getUsername());
        passwordField.clear();
        roleCombo.setValue(akunSendiri.getRole());

        nameField.setDisable(true);
        usernameField.setDisable(true);
        roleCombo.setDisable(true);
        searchField.setDisable(true);
        pegawaiTable.setDisable(true);
        deleteButton.setDisable(true);
        passwordField.setDisable(false);
        saveButton.setText("Ubah Password Saya");
    }

    private void updateOwnPassword() {
        String passwordBaru = passwordField.getText().trim();
        if (passwordBaru.isEmpty()) {
            showWarning("Password baru wajib diisi.");
            return;
        }

        Pegawai akunSendiri = masterData.stream()
                .filter(pegawai -> pegawai.getId() == Session.getCurrentUser().getId())
                .findFirst()
                .orElse(Session.getCurrentUser());
        akunSendiri.setPassword(passwordBaru);

        if (new PegawaiXStreamDAO().updatePegawai(akunSendiri)) {
            Session.login(akunSendiri);
            showInformation("Password berhasil diperbarui.");
            loadTable();
            configurePegawaiAccess();
        } else {
            showError("Password gagal diperbarui.");
        }
    }

    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInformation(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

