package com.simsw.util;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/** Replaces page content while preserving the current window size and state. */
public final class SceneNavigator {

    private SceneNavigator() {
    }

    public static void show(Stage stage, Parent root, String title) {
        Scene scene = stage.getScene();
        if (scene == null) {
            stage.setScene(new Scene(root));
        } else {
            scene.setRoot(root);
        }
        stage.setTitle(title);
        stage.show();
    }
}
