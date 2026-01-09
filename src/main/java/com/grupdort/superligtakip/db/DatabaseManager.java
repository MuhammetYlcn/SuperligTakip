package com.grupdort.superligtakip.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    // Veritabanı dosya yolu
    private static final String URL = "jdbc:sqlite:database/superlig.db";

    public static Connection connect() {
        Connection connection = null;
        try {
            // Driver'ı yükle
            Class.forName("org.sqlite.JDBC");

            // Bağlantıyı kur
            connection = DriverManager.getConnection(URL);

            // Foreign Key desteğini aç
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON;");
            }

            // --- OTOMATİK TABLO OLUŞTURMA ---
            // Bağlantı her kurulduğunda tabloları kontrol et
            createTablesIfNotExist(connection);

        } catch (ClassNotFoundException e) {
            System.err.println("!!! KRITIK HATA: SQLite Sürücüsü bulunamadı.");
        } catch (SQLException e) {
            System.err.println("!!! SQL HATASI: Bağlantı kurulamadı: " + e.getMessage());
        }
        return connection;
    }

    // Tabloları yoksa oluşturan devasa metodumuz
    private static void createTablesIfNotExist(Connection conn) {
        // Her tablo için ayrı SQL komutu
        String[] sqlStatements = {
                // 1. SEZONLAR
                "CREATE TABLE IF NOT EXISTS Seasons (" +
                        "   season_ID INTEGER PRIMARY KEY," +
                        "   season_name TEXT NOT NULL," +
                        "   is_active INTEGER DEFAULT 0" +
                        ");",

                // 2. HAFTALAR
                "CREATE TABLE IF NOT EXISTS Weeks (" +
                        "   week_ID INTEGER PRIMARY KEY," +
                        "   week_name TEXT," +
                        "   season_ID INTEGER NOT NULL," +
                        "   status INTEGER DEFAULT 0," +
                        "   FOREIGN KEY(season_ID) REFERENCES Seasons(season_ID) ON DELETE CASCADE" +
                        ");",

                // 3. TAKIMLAR
                "CREATE TABLE IF NOT EXISTS Teams (" +
                        "   team_ID INTEGER PRIMARY KEY," +
                        "   team_name TEXT NOT NULL," +
                        "   logo TEXT," +
                        "   manager_name TEXT" +
                        ");",

                // 4. OYUNCULAR (player_ID TEXT olarak ayarlandı)
                "CREATE TABLE IF NOT EXISTS Players (" +
                        "   player_ID TEXT PRIMARY KEY," +
                        "   team_ID INTEGER NOT NULL," +
                        "   player_name TEXT," +
                        "   position TEXT," +
                        "   age INTEGER," +
                        "   appearances INTEGER DEFAULT 0," +
                        "   goal INTEGER DEFAULT 0," +
                        "   assist INTEGER DEFAULT 0," +
                        "   yellow_card INTEGER DEFAULT 0," +
                        "   red_card INTEGER DEFAULT 0," +
                        "   total_rating REAL DEFAULT 0," +
                        "   FOREIGN KEY(team_ID) REFERENCES Teams(team_ID) ON DELETE CASCADE" +
                        ");",

                // 5. FİKSTÜR
                "CREATE TABLE IF NOT EXISTS Fixtures (" +
                        "   fixture_ID INTEGER PRIMARY KEY," +
                        "   home_team_ID INTEGER NOT NULL," +
                        "   away_team_ID INTEGER NOT NULL," +
                        "   week_ID INTEGER NOT NULL," +
                        "   match_date TEXT," +
                        "   match_time TEXT," +
                        "   status TEXT," +
                        "   home_score INTEGER DEFAULT 0," +
                        "   away_score INTEGER DEFAULT 0," +
                        "   event_live INTEGER DEFAULT 0," +
                        "   final_result TEXT," +
                        "   FOREIGN KEY(home_team_ID) REFERENCES Teams(team_ID)," +
                        "   FOREIGN KEY(away_team_ID) REFERENCES Teams(team_ID)," +
                        "   FOREIGN KEY(week_ID) REFERENCES Weeks(week_ID)" +
                        ");",

                // 6. MAÇ KADROLARI
                "CREATE TABLE IF NOT EXISTS MatchLineups (" +
                        "   fixture_ID INTEGER NOT NULL," +
                        "   player_ID TEXT NOT NULL," +
                        "   team_ID INTEGER NOT NULL," +
                        "   is_substitute INTEGER DEFAULT 0," +
                        "   number INTEGER," +
                        "   position INTEGER," +
                        "   PRIMARY KEY(fixture_ID, player_ID)," +
                        "   FOREIGN KEY(fixture_ID) REFERENCES Fixtures(fixture_ID) ON DELETE CASCADE," +
                        "   FOREIGN KEY(player_ID) REFERENCES Players(player_ID)," +
                        "   FOREIGN KEY(team_ID) REFERENCES Teams(team_ID)" +
                        ");",

                // 7. MAÇ İSTATİSTİKLERİ
                "CREATE TABLE IF NOT EXISTS MatchStatistics (" +
                        "   fixture_ID INTEGER PRIMARY KEY," +
                        "   home_possession INTEGER DEFAULT 0," +
                        "   away_possession INTEGER DEFAULT 0," +
                        "   home_pressure_index REAL DEFAULT 0," +
                        "   away_pressure_index REAL DEFAULT 0," +
                        "   home_total_shots INTEGER DEFAULT 0," +
                        "   away_total_shots INTEGER DEFAULT 0," +
                        "   home_shots_on_target INTEGER DEFAULT 0," +
                        "   away_shots_on_target INTEGER DEFAULT 0," +
                        "   home_shots_off_target INTEGER DEFAULT 0," +
                        "   away_shots_off_target INTEGER DEFAULT 0," +
                        "   home_blocked_shots INTEGER DEFAULT 0," +
                        "   away_blocked_shots INTEGER DEFAULT 0," +
                        "   home_saves INTEGER DEFAULT 0," +
                        "   away_saves INTEGER DEFAULT 0," +
                        "   home_total_passes INTEGER DEFAULT 0," +
                        "   away_total_passes INTEGER DEFAULT 0," +
                        "   home_accurate_passes INTEGER DEFAULT 0," +
                        "   away_accurate_passes INTEGER DEFAULT 0," +
                        "   home_pass_accuracy_perc REAL DEFAULT 0," +
                        "   away_pass_accuracy_perc REAL DEFAULT 0," +
                        "   home_corners INTEGER DEFAULT 0," +
                        "   away_corners INTEGER DEFAULT 0," +
                        "   home_offsides INTEGER DEFAULT 0," +
                        "   away_offsides INTEGER DEFAULT 0," +
                        "   home_fouls INTEGER DEFAULT 0," +
                        "   away_fouls INTEGER DEFAULT 0," +
                        "   home_yellow_cards INTEGER DEFAULT 0," +
                        "   away_yellow_cards INTEGER DEFAULT 0," +
                        "   home_red_card_from_yellows INTEGER DEFAULT 0," +
                        "   away_red_card_from_yellows INTEGER DEFAULT 0," +
                        "   home_direct_red_cards INTEGER DEFAULT 0," +
                        "   away_direct_red_cards INTEGER DEFAULT 0," +
                        "   home_manager TEXT," +
                        "   away_manager TEXT," +
                        "   FOREIGN KEY(fixture_ID) REFERENCES Fixtures(fixture_ID) ON DELETE CASCADE" +
                        ");",

                // 8. OLAYLAR (Goller, Kartlar vb.)
                "CREATE TABLE IF NOT EXISTS Events (" +
                        "   event_ID INTEGER PRIMARY KEY," +
                        "   fixture_ID INTEGER NOT NULL," +
                        "   team_ID INTEGER," +
                        "   player_name TEXT," +
                        "   related_player_name TEXT," +
                        "   score_time TEXT," +
                        "   score TEXT," +
                        "   time TEXT," +
                        "   type TEXT," +
                        "   info TEXT," +
                        "   FOREIGN KEY(fixture_ID) REFERENCES Fixtures(fixture_ID) ON DELETE CASCADE," +
                        "   FOREIGN KEY(team_ID) REFERENCES Teams(team_ID)" +
                        ");",

                // 9. PUAN DURUMU
                "CREATE TABLE IF NOT EXISTS Standings (" +
                        "   standing_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "   team_ID INTEGER NOT NULL," +
                        "   season_ID INTEGER NOT NULL," +
                        "   rank INTEGER DEFAULT 0," +
                        "   won INTEGER DEFAULT 0," +
                        "   drawn INTEGER DEFAULT 0," +
                        "   lost INTEGER DEFAULT 0," +
                        "   points INTEGER DEFAULT 0," +
                        "   played INTEGER DEFAULT 0," +
                        "   goals_for INTEGER DEFAULT 0," +
                        "   goals_against INTEGER DEFAULT 0," +
                        "   goals_diff INTEGER DEFAULT 0," +
                        "   FOREIGN KEY(team_ID) REFERENCES Teams(team_ID)," +
                        "   FOREIGN KEY(season_ID) REFERENCES Seasons(season_ID)" +
                        ");",

                // 10. SON GÜNCELLEMELER
                "CREATE TABLE IF NOT EXISTS LastUpdates (" +
                        "   updated_key TEXT PRIMARY KEY," +
                        "   updated_at TEXT" +
                        ");"
        };

        try (Statement stmt = conn.createStatement()) {
            for (String sql : sqlStatements) {
                stmt.execute(sql);
            }
            // System.out.println("✅ Veritabanı tabloları kontrol edildi/oluşturuldu.");
        } catch (SQLException e) {
            System.err.println("!!! Tablo oluşturma hatası: " + e.getMessage());
        }
    }
}