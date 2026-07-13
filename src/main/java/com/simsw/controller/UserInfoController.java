package com.simsw.controller;

import com.simsw.util.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/** Displays the current session user in every page header. */
public class UserInfoController {

    @FXML private Label lblUserInfo;

    @FXML
    private void initialize() {
        if (Session.isLogin()) {
            lblUserInfo.setText("👤 " + Session.getCurrentUser().getNama()
                    + " | " + Session.getCurrentUser().getRole());
        } else {
            lblUserInfo.setText("👤 Guest");
        }
    }
}
