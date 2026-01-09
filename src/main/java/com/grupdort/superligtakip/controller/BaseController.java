package com.grupdort.superligtakip.controller;

import com.grupdort.superligtakip.service.DataSyncService;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;

public abstract class BaseController {

    private final DataSyncService globalSyncService = new DataSyncService();
    protected boolean isMenuOpen = false;

    protected void runAsync(Node loadingOverlay, Runnable backgroundTask, Runnable onUISuccess) {
        if (loadingOverlay != null) loadingOverlay.setVisible(true);

        new Thread(() -> {
            try {
                globalSyncService.performSmartSync();
                backgroundTask.run();

                Platform.runLater(() -> {
                    if (loadingOverlay != null) loadingOverlay.setVisible(false);
                    onUISuccess.run();
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    if (loadingOverlay != null) loadingOverlay.setVisible(false);
                    e.printStackTrace();
                });
            }
        }).start();
    }

    protected void setupMenuOverlay(VBox sidebar, AnchorPane menuOverlay, VBox mainContent) {
        if (menuOverlay != null) {
            // Başlangıçta arkadaki listeye tıklanabilsin
            menuOverlay.setMouseTransparent(!isMenuOpen);
            menuOverlay.setOnMouseClicked(event -> {
                if (isMenuOpen && sidebar != null && !sidebar.isHover()) {
                    toggleMenu(sidebar, menuOverlay, mainContent);
                }
            });
        }
    }

    protected void toggleMenu(VBox sidebar, AnchorPane menuOverlay, VBox mainContent) {
        if (sidebar == null || menuOverlay == null) return;

        TranslateTransition transition = new TranslateTransition(Duration.millis(300), sidebar);

        if (!isMenuOpen) {
            menuOverlay.setVisible(true);
            menuOverlay.setMouseTransparent(false); // Tıklamaları yakala
            if (mainContent != null) mainContent.setEffect(new GaussianBlur(10));
            transition.setToX(0);
            transition.play();
            isMenuOpen = true;
        } else {
            if (mainContent != null) mainContent.setEffect(null);
            transition.setToX(-260);
            transition.setOnFinished(e -> {
                menuOverlay.setVisible(false);
                menuOverlay.setMouseTransparent(true); // Tıklamaları arkaya (listeye) geçir
            });
            transition.play();
            isMenuOpen = false;
        }
    }

    protected void setupNavigation(Button btnPuan, Button btnFikstur, Button btnGol, Button btnAsist) {
        if (btnPuan != null) btnPuan.setOnAction(e -> safePageTransition(btnPuan, "/com/grupdort/superligtakip/view/StandingView/Standing.fxml"));
        if (btnFikstur != null) btnFikstur.setOnAction(e -> safePageTransition(btnFikstur, "/com/grupdort/superligtakip/view/FixtureView/Fixture.fxml"));
        if (btnGol != null) btnGol.setOnAction(e -> safePageTransition(btnGol, "/com/grupdort/superligtakip/view/StatsView/GoalStats.fxml"));
        if (btnAsist != null) {
            btnAsist.setOnAction(e -> safePageTransition(btnAsist, "/com/grupdort/superligtakip/view/StatsView/AssistStats.fxml"));
        }
    }

    private void safePageTransition(Button sourceButton, String fxmlPath) {
        Scene currentScene = sourceButton.getScene();
        if (currentScene == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) currentScene.getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}