package com.grupdort.superligtakip.service;

import com.grupdort.superligtakip.dao.LastUpdateDAO;
import com.grupdort.superligtakip.db.DatabaseManager;
import com.grupdort.superligtakip.model.LastUpdate;
import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;

public class DataSyncService {
    private final LastUpdateDAO lastUpdateDAO = new LastUpdateDAO();

    // Diğer servislerimizi bağlıyoruz
    private final TeamService teamService = new TeamService();
    private final PlayerService playerService = new PlayerService();
    private final FixtureService fixtureService = new FixtureService();
    private final StandingService standingService = new StandingService(); // Puan durumu servisi

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Uygulama açılışında tüm kategorileri tek tek kontrol eder.
     * Vakti gelen tabloyu önce temizler (cleanTable), sonra güncel veriyi çeker.
     */
    public void performSmartSync() {
        System.out.println(">>> Akıllı Senkronizasyon Başlatıldı...");

        // 1. TAKIMLAR (Ana tablo, önce bu güncellenmeli)
        if (isUpdateRequired("TEAMS", 60)) {
            System.out.println("-> Takım süresi doldu. Tablo sıfırlanıyor ve güncelleniyor...");
            cleanTable("Teams");
            teamService.syncTeams();
            saveOrUpdateTimestamp("TEAMS");
        }

        // 2. PUAN DURUMU (Standings)
        if (isUpdateRequired("STANDINGS", 60)) {
            System.out.println("-> Puan Durumu süresi doldu. Tablo sıfırlanıyor ve güncelleniyor...");
            cleanTable("Standings");
            standingService.syncStandingWithAPI(); // API'den çekip DB'ye yazan metodun
            saveOrUpdateTimestamp("STANDINGS");
        }

        // 3. FİKSTÜR VE HAFTALAR
        if (isUpdateRequired("FIXTURES", 60)) {
            System.out.println("-> Fikstür süresi doldu. Tablolar sıfırlanıyor ve güncelleniyor...");
            cleanTable("Fixtures");
            cleanTable("Weeks");
            fixtureService.syncAllSeasonFixtures();
            saveOrUpdateTimestamp("FIXTURES");
        }

        // 4. OYUNCULAR
        if (isUpdateRequired("PLAYERS", 60)) {
            System.out.println("-> Oyuncu verileri süresi doldu. Tablo sıfırlanıyor ve güncelleniyor...");
            cleanTable("Players");
            playerService.syncAllPlayers();
            saveOrUpdateTimestamp("PLAYERS");
        }

        System.out.println(">>> Akıllı Senkronizasyon Tamamlandı. Sistem Güncel.");
    }

    /**
     * Belirli bir tabloyu verilerden temizler ve ID sayacını sıfırlar.
     */
    private void cleanTable(String tableName) {
        try (Connection conn = DatabaseManager.connect();
             Statement stmt = conn.createStatement()) {

            stmt.execute("PRAGMA foreign_keys = OFF;");

            // Verileri sil
            stmt.execute("DELETE FROM " + tableName);

            // SQLite ID sayacını sıfırla
            stmt.execute("DELETE FROM sqlite_sequence WHERE name = '" + tableName + "'");

            stmt.execute("PRAGMA foreign_keys = ON;");
            System.out.println("   [TEMİZLENDİ] " + tableName);

        } catch (Exception e) {
            System.err.println("   [HATA] " + tableName + " temizlenirken hata: " + e.getMessage());
        }
    }

    /**
     * Veritabanındaki LastUpdates tablosuna bakarak 60 dakika geçip geçmediğini kontrol eder.
     */
    private boolean isUpdateRequired(String key, int thresholdMinutes) {
        LastUpdate lastUpdate = lastUpdateDAO.getLastUpdateByKey(key);
        if (lastUpdate == null) return true;

        try {
            LocalDateTime lastTime = LocalDateTime.parse(lastUpdate.getUpdated_at(), formatter);
            long minutesPassed = Duration.between(lastTime, LocalDateTime.now()).toMinutes();
            return minutesPassed >= thresholdMinutes;
        } catch (Exception e) {
            return true; // Hata varsa güncellemeye zorla
        }
    }

    /**
     * Güncelleme işlemi başarılı bittiğinde zaman damgasını günceller.
     */
    private void saveOrUpdateTimestamp(String key) {
        String now = LocalDateTime.now().format(formatter);
        LastUpdate lu = new LastUpdate(key, now);

        if (lastUpdateDAO.getLastUpdateByKey(key) == null) {
            lastUpdateDAO.insertLastUpdate(lu);
        } else {
            lastUpdateDAO.updateLastUpdate(lu);
        }
    }
}