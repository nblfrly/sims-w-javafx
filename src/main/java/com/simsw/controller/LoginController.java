package com.simsw.controller;

import java.io.IOException;

import com.simsw.dao.xstream.PegawaiXStreamDAO;
import com.simsw.model.Pegawai;
import com.simsw.session.Session;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private void handleLogin(ActionEvent event) throws IOException {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText();

        PegawaiXStreamDAO dao = new PegawaiXStreamDAO();
        Pegawai pegawai = dao.login(username, password);
        if (pegawai != null) {
            // simpan sesi login
            Session.login(pegawai);
            System.out.println("LOGIN BERHASIL");
            System.out.println("Nama  : " + pegawai.getNama());
            System.out.println("Role  : " + pegawai.getRole());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/simsw/view/Dashboard.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) txtUsername.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard");
            stage.show();

        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Gagal");
            alert.setHeaderText(null);
            alert.setContentText("Username atau Password salah!");
            alert.showAndWait();
        }
    }
}