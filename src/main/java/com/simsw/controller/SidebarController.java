package com.simsw.controller;

import com.simsw.util.AlertHelper;
import com.simsw.util.SceneNavigator;
import com.simsw.util.Session;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

/** Navigation shared by all pages that are available after login. */
public class SidebarController {

    private static String activePage = "Dashboard";
    @FXML private VBox sidebar;
    @FXML private Label lblNav;
    @FXML private Button btnDashboard;
    @FXML private Button btnInventaris;
    @FXML private Button btnMenu;
    @FXML private Button btnPenggunaan;
    @FXML private Button btnRiwayat;
    @FXML private Button btnPegawai;
    @FXML private Button btnLogout;
    private final Map<Button, String> defaultStyles = new LinkedHashMap<>();
    private Button activeButton;
    private static final double COLLAPSED_WIDTH = 80;
    private static final double EXPANDED_WIDTH = 240;

    @FXML
    private void initialize() {

        enableHover(btnDashboard, btnInventaris, btnMenu,
                btnPenggunaan, btnRiwayat, btnPegawai, btnLogout);

        setActiveButton();

        sidebar.setPrefWidth(COLLAPSED_WIDTH);

        sidebar.setOnMouseEntered(e -> expandSidebar());
        sidebar.setOnMouseExited(e -> collapseSidebar());

        collapseSidebar();
    }

    @FXML private void openDashboard(ActionEvent event) { openPage(event, "Dashboard", "Dashboard"); }
    @FXML private void openInventaris(ActionEvent event) { openPage(event, "Inventaris", "Inventaris"); }
    @FXML private void openMenu(ActionEvent event) { openPage(event, "Menu", "Menu Warmindo"); }
    @FXML private void openPenggunaan(ActionEvent event) { openPage(event, "PenggunaanBarang", "Penggunaan Barang"); }
    @FXML private void openRiwayat(ActionEvent event) { openPage(event, "Riwayat", "Riwayat"); }
    @FXML private void openPegawai(ActionEvent event) { openPage(event, "Pegawai", "Manajemen Pegawai"); }

    @FXML
    private void logout(ActionEvent event) {
        Session.logout();
        activePage = "Dashboard";
        openPage(event, "Login", "Login");
    }

    private void openPage(ActionEvent event, String fxmlName, String title) {
        try {
            activePage = fxmlName;
            Parent root = FXMLLoader.load(getClass().getResource("/com/simsw/view/" + fxmlName + ".fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            SceneNavigator.show(stage, root, title);
        } catch (IOException exception) {
            AlertHelper.showError("Halaman tidak dapat dibuka.");
        }
    }

    public static void setActivePage(String pageName) {
        activePage = pageName;
    }

    private void setActiveButton() {
        activeButton = switch (activePage) {
            case "Inventaris" -> btnInventaris;
            case "Menu", "KelolaResep" -> btnMenu;
            case "PenggunaanBarang" -> btnPenggunaan;
            case "Riwayat" -> btnRiwayat;
            case "Pegawai" -> btnPegawai;
            default -> btnDashboard;
        };
        activeButton.setStyle(activeButton.getStyle()
                + " -fx-background-color: #C62828; -fx-text-fill: white;");
    }

    private void enableHover(Button... buttons) {
        for (Button button : buttons) {
            defaultStyles.put(button, button.getStyle());
            button.hoverProperty().addListener((obs, wasHovered, isHovered) -> {
                if (button == activeButton || button.isDisable()) {
                    return;
                }
                button.setStyle(defaultStyles.get(button)
                        + (isHovered ? " -fx-background-color: #FDECEC; -fx-text-fill: #9F1239;" : ""));
            });
        }
    }

    private void expandSidebar() {
        animateWidth(EXPANDED_WIDTH);

        lblNav.setVisible(true);
        lblNav.setManaged(true);

        btnDashboard.setText("🏠   Dashboard");
        btnInventaris.setText("📦   Inventaris");
        btnMenu.setText("🍜   Menu Warmindo");
        btnPenggunaan.setText("📋   Penggunaan Barang");
        btnRiwayat.setText("🕘   Riwayat");
        btnPegawai.setText("👥   Pegawai");
        btnLogout.setText("↪   Keluar");

        setButtonAlignment(true);
    }

    private void collapseSidebar() {

        animateWidth(COLLAPSED_WIDTH);

        lblNav.setVisible(false);
        lblNav.setManaged(false);

        btnDashboard.setText("🏠");
        btnInventaris.setText("📦");
        btnMenu.setText("🍜");
        btnPenggunaan.setText("📋");
        btnRiwayat.setText("🕘");
        btnPegawai.setText("👥");
        btnLogout.setText("↪");

        setButtonAlignment(false);
    }

    private void animateWidth(double width) {

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(100),
                        new KeyValue(sidebar.prefWidthProperty(), width))
        );

        timeline.play();
    }

    private void setButtonAlignment(boolean expanded) {

        String alignment = expanded ? "CENTER_LEFT" : "CENTER";

        Button[] buttons = {
            btnDashboard,
            btnInventaris,
            btnMenu,
            btnPenggunaan,
            btnRiwayat,
            btnPegawai,
            btnLogout
        };

        for (Button button : buttons) {

            String style = button.getStyle();

            style = style.replace("-fx-alignment: CENTER_LEFT;", "");
            style = style.replace("-fx-alignment: CENTER;", "");

            button.setStyle(style + "-fx-alignment: " + alignment + ";");
        }
    }
}
