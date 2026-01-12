package com.grupdort.superligtakip.service;

import com.grupdort.superligtakip.db.DatabaseManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CleanAndSyncEverything {
    // Diğer servisleri burada tanımlıyoruz
    private final TeamService teamService = new TeamService();
    private final PlayerService playerService = new PlayerService();
    private final FixtureService fixtureService = new FixtureService();
    private final StandingService standingService = new StandingService();

    /**
     * TÜM SİSTEMİ SENKRONİZE EDER
     * Sıralama kritiktir: Önce temizle, sonra Takım, sonra Fikstür, en son Oyuncu.
     */
    public void syncEverything() {
        System.out.println(">>> SENKRONİZASYON İŞLEMİ BAŞLATILDI <<<");

        // 1. Veritabanını Sıfırla
        cleanDatabase();

        // 2. Takımları API'den çek ve kaydet
        System.out.println("1/4: Takımlar senkronize ediliyor...");
        teamService.syncTeams();

        // 3. Takımları API'den çek ve kaydet
        System.out.println("2/4: Puan tablosu senkronize ediliyor...");
        standingService.syncStandingWithAPI();

        // 4. Fikstürü API'den çek ve kaydet
        System.out.println("3/4: Fikstür ve haftalar senkronize ediliyor...");
        fixtureService.syncAllSeasonFixtures();

        // 5. Oyuncuları API'den çek ve kaydet (İstatistikler dahil)
        System.out.println("4/4: Oyuncular ve detaylı istatistikler senkronize ediliyor...");
        playerService.syncAllPlayers();

        System.out.println(">>> TÜM VERİLER BAŞARIYLA GÜNCELLENDİ VE KAYDEDİLDİ <<<");
    }

    /**
     * VERİTABANINI TERTEMİZ YAPAR
     * Senin yazdığın profesyonel temizlik mantığını kullanır.
     */
    public void cleanDatabase() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseManager.connect();
            if (conn == null) return;

            conn.setAutoCommit(false);
            stmt = conn.createStatement();

            System.out.println("Sistem: SQLite Veritabanı temizliği başlıyor...");

            // Foreign Key kontrolünü kapat
            stmt.execute("PRAGMA foreign_keys = OFF;");

            // Tablo isimlerini otomatik olarak çek
            List<String> tables = new ArrayList<>();
            rs = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name NOT LIKE 'sqlite_%';");

            while (rs.next()) {
                tables.add(rs.getString("name"));
            }
            rs.close();

            // Verileri sil ve ID'leri sıfırla
            for (String tableName : tables) {
                stmt.execute("DELETE FROM " + tableName);
                // ID sayaçlarını (auto-increment) sıfırla
                stmt.execute("DELETE FROM sqlite_sequence WHERE name = '" + tableName + "'");
                System.out.println("  -> " + tableName + " tablosu temizlendi.");
            }

            conn.commit();
            conn.setAutoCommit(true);

            // VACUUM işlemi veritabanı dosyasını fiziksel olarak küçültür
            stmt.execute("VACUUM;");
            System.out.println("Sistem: Veritabanı başarıyla sıfırlandı ve sıkıştırıldı.");

        } catch (SQLException e) {
            System.err.println("Temizlik hatası: " + e.getMessage());
            try {
                if (conn != null && !conn.getAutoCommit()) conn.rollback();
            } catch (SQLException ex) { ex.printStackTrace(); }
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }
}