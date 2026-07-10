package com.simsw.controller;

import java.io.IOException;
import java.time.LocalDateTime;

import com.simsw.dao.xstream.PegawaiXStreamDAO;
import com.simsw.dao.xstream.RiwayatXStreamDAO;
import com.simsw.model.Pegawai;
import com.simsw.model.Riwayat;

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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class RiwayatController {

    @FXML
    private Button btnDashboard;

    @FXML
    private TableColumn<Riwayat, String> colNama;

    @FXML
    private TableColumn<Riwayat, String> colItem;

    @FXML
    private TableColumn<Riwayat, Integer> colNewValue;

    private ObservableList<Riwayat> masterData = FXCollections.observableArrayList();

    @FXML
    private TableColumn<Riwayat, String> colKeterangan;

    @FXML
    private TableColumn<Riwayat, Integer> colOldValue;

    @FXML
    private TableColumn<Riwayat, LocalDateTime> colTimestamp;

    @FXML
    private Button exportRiwayat;

    @FXML
    private TextField filterRiwayatTxt;

    @FXML
    private TableView<Riwayat> riwayatTable;

    @FXML
    private DatePicker pilihanTanggal;

    @FXML
    private Button resetFilterRiwayat;

    @FXML
    private void backDashboard(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/simsw/view/Dashboard.fxml"));

        Parent root = loader.load();
        Stage stage = (Stage) btnDashboard.getScene().getWindow();

        stage.setScene(new Scene(root));
        stage.setTitle("Dashboard");
        stage.show();
    }

    @FXML
    public void initialize() {
        // long start = System.currentTimeMillis();
        // kategoriFilter.getItems().addAll(
        //         "Admin",
        //         "Pegawai"
        // );

        loadTable();
        setupSearch();

    }

    private void loadTable() {
        colNama.setCellValueFactory(new PropertyValueFactory<>("namaPegawai"));
        colItem.setCellValueFactory(new PropertyValueFactory<>("namaBarang"));
        colKeterangan.setCellValueFactory(new PropertyValueFactory<>("keterangan"));
        colOldValue.setCellValueFactory(new PropertyValueFactory<>("jumlahLama"));
        colNewValue.setCellValueFactory(new PropertyValueFactory<>("jumlahBaru"));
        colTimestamp.setCellValueFactory(new PropertyValueFactory<>("tanggal"));

        RiwayatXStreamDAO dao = new RiwayatXStreamDAO();
        masterData.setAll(dao.getAllRiwayat());

        riwayatTable.setItems(masterData);
    }

    private void setupSearch() {
        FilteredList<Riwayat> filteredData = new FilteredList<>(masterData, r -> true);

        filterRiwayatTxt.textProperty().addListener((obs, oldValue, newValue) -> {
            filteredData.setPredicate(riwayat -> {
                if (newValue == null || newValue.isBlank()) {
                    return true;
                }

                String keyword = newValue.toLowerCase();

                return riwayat.getNamaPegawai().toLowerCase().contains(keyword)
                        || riwayat.getNamaBarang().toLowerCase().contains(keyword);
            });
        });

        SortedList<Riwayat> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(riwayatTable.comparatorProperty());

        riwayatTable.setItems(sortedData);
    }
}

