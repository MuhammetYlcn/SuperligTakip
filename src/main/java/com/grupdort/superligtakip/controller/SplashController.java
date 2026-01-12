package com.grupdort.superligtakip.controller;

import com.grupdort.superligtakip.service.DataSyncService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.net.URL;

import java.io.IOException;

public class SplashController {

    private final DataSyncService dataSyncService = new DataSyncService();

    @FXML
    public void initialize() {
        // Ekran açılır açılmaz arka plan işlemini başlat
        new Thread(() -> {
            try {
                // 1. AĞIR İŞLEM: Veritabanı kontrolü ve güncelleme
                dataSyncService.performSmartSync();

                // (Opsiyonel) Çok hızlı biterse kullanıcı "göz kırpma" gibi görmesin diye
                // biraz bekletebilirsin.
                // Thread.sleep(1500);

            } catch (Exception e) {
                e.printStackTrace();
            }

            // 2. İŞLEM BİTTİ: Ana Ekrana Geçiş (UI Thread'inde yapılmalı)
            Platform.runLater(this::loadMainScreen);

        }).start();
    }

    private void loadMainScreen() {
        try {
            String fxmlPath = "/com/grupdort/superligtakip/view/FixtureView/Fixture.fxml";
            URL resource = getClass().getResource(fxmlPath);

            if (resource == null) {
                System.err.println("KRİTİK HATA: FXML dosyası bulunamadı! Yol: " + fxmlPath);
                return;
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            // --- HATA DÜZELTİLDİ ---
            // Stage::isShowing yerine Window::isShowing veya lambda kullanıldı.
            // Ayrıca güvenli olması için "instanceof Stage" kontrolü eklendi.
            Stage currentStage = Stage.getWindows().stream()
                    .filter(window -> window.isShowing())   // Lambda ile kontrol (En garantisi)
                    .filter(window -> window instanceof Stage) // Sadece Stage olanları al (Tooltip vs. karışmasın)
                    .map(window -> (Stage) window)             // Stage'e çevir
                    .findFirst()
                    .orElse(null);

            if (currentStage != null) {
                Scene scene = new Scene(root);
                currentStage.setScene(scene);
                currentStage.setTitle("Trendyol Süper lig - Fikstür");
                currentStage.centerOnScreen();
                currentStage.show();
            }

        } catch (IOException e) {
            System.err.println("Ana ekran yüklenirken hata oluştu:");
            e.printStackTrace();
        }
    }
}