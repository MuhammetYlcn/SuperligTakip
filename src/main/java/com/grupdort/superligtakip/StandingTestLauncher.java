package com.grupdort.superligtakip;

import com.grupdort.superligtakip.dao.*;
import com.grupdort.superligtakip.model.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class StandingTestLauncher extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/grupdort/superligtakip/view/StandingView/Standing.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 600);
        stage.setTitle("Süper Lig Puan Durumu - Test");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        // Uygulama başlamadan önce kesinlikle veri olduğundan emin olalım
        prepareData();
        launch();
    }

    private static void prepareData() {
        SeasonDAO seasonDAO = new SeasonDAO();
        TeamDAO teamDAO = new TeamDAO();
        StandingDAO standingDAO = new StandingDAO();

        try {
            // 1. Önce mevcut verileri temizleyelim (Testin her seferinde taze veriyle çalışması için)
            // Not: Gerçek projede bunu yapmayın, sadece testi doğrulamak için.
            System.out.println("Veritabanı kontrol ediliyor...");

            // 2. Sezon Ekle
            Season s = new Season();
            s.setSeason_ID(2025); // Belirgin bir ID
            s.setSeason_name("2024/2025");
            s.setIs_active(1);
            if (seasonDAO.getSeasonById(2025) == null) {
                seasonDAO.insertSeason(s); //
                System.out.println("Sezon oluşturuldu.");
            }

            // 3. Takım Ekle (Galatasaray)
            Team t = new Team(1905, "Galatasaray", "logo_url", "GS"); //
            if (teamDAO.getTeamById(1905) == null) {
                teamDAO.insertTeam(t); //
                System.out.println("Takım oluşturuldu.");
            }

            // 4. Puan Durumu Satırı Oluştur
            // StandingDAO'daki insertStanding sadece team_ID ve season_ID alır
            standingDAO.insertStanding(1905, 2025);

            // 5. Verileri Güncelle
            Standing st = new Standing();
            st.setTeam(t);
            st.setSeason(s);
            st.setRank(1);
            st.setPlayed(38);
            st.setWon(33);
            st.setDrawn(3);
            st.setLost(2);
            st.setGoal_for(92);
            st.setGoal_against(26);
            st.hesaplaGoal_diff();
            st.setPoints(102);

            standingDAO.updateStanding(st); //
            System.out.println("Galatasaray 1. sıraya başarıyla eklendi.");

        } catch (Exception e) {
            System.err.println("TEST VERİSİ HAZIRLAMA HATASI: " + e.getMessage());
            e.printStackTrace();
        }
    }
}