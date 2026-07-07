package com.simsw.controller;

import java.io.IOException;
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
import javafx.stage.Stage;

public class RiwayatController {

    @FXML
    private Button btnDashboard;

    @FXML
    private TableColumn<?, String> colPegawai;

    @FXML
    private TableColumn<?, String> colItem;

    @FXML
    private TableColumn<?, Integer> colNewValue;

    @FXML
    private TableColumn<?, String> colKeterangan;

    @FXML
    private TableColumn<?, Integer> colOldValue;

    @FXML
    private TableColumn<?, ?> colTimestamp;

    @FXML
    private Button exportRiwayat;

    @FXML
    private TextField filterRiwayatTxt;

    @FXML
    private TableView<?> historyTable;

    @FXML
    private ComboBox<?> kategoriFilter;

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
}

