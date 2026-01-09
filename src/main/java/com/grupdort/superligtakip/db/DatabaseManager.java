package com.grupdort.superligtakip.db;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String url="jdbc:sqlite:database/superlig.db";

    public static Connection connect() {
        try {
            // Driver'ı elle yükleyerek modül sistemine tanıtalım
            Class.forName("org.sqlite.JDBC");

            Connection connection = DriverManager.getConnection(url);

            try (Statement stmt = connection.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON;");
            }
            return connection;
        } catch (ClassNotFoundException e) {
            System.err.println("!!! KRITIK HATA: SQLite Sürücüsü (JAR) bulunamadı. Bağımlılıkları kontrol edin.");
        } catch (SQLException e) {
            System.err.println("!!! SQL HATASI: Bağlantı kurulamadı: " + e.getMessage());
        }
        return null; // Eğer buraya düşerse bağlantı başarısızdır
    }
}