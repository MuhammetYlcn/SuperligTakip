package com.grupdort.superligtakip;

import com.grupdort.superligtakip.service.CleanAndSyncEverything;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class main extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(main.class.getResource("/com/grupdort/superligtakip/view/SplashView/Splash.fxml"));

        Scene scene = new Scene(fxmlLoader.load());

        stage.setTitle("Süper Lig Takip - Yükleniyor...");
        stage.setScene(scene);
        stage.setResizable(false); // Pencere boyutu sabit kalsın

        stage.show();
    }

    public static void main(String[] args) {
        //CleanAndSyncEverything cleanAndSyncEverything = new CleanAndSyncEverything();
        //cleanAndSyncEverything.cleanDatabase();
        launch();
    }
}