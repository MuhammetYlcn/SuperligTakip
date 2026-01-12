package com.grupdort.superligtakip.dao;

import com.grupdort.superligtakip.db.DatabaseManager;
import com.grupdort.superligtakip.model.LastUpdate;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LastUpdateDAO {

    // 1. CREATE (Ekleme)
    // "INSERT OR IGNORE" kullanıldı: Varsa hiçbir şey yapmaz, yoksa ekler.
    public void insertLastUpdate(LastUpdate lastUpdate) {
        String sql = "INSERT OR IGNORE INTO LastUpdates (updated_key, updated_at) VALUES (?, ?)";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, lastUpdate.getUpdated_key());
            pstmt.setString(2, lastUpdate.getUpdated_at());

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Kayıt eklendi: " + lastUpdate.getUpdated_key());
            } else {
                System.out.println("Kayıt zaten var, ekleme atlandı (IGNORE): " + lastUpdate.getUpdated_key());
            }

        } catch (SQLException e) {
            System.out.println("Ekleme Hatası: " + e.getMessage());
        }
    }

    // 2. UPDATE (Güncelleme)
    // Sadece var olan kaydı günceller. Kayıt yoksa işlem yapmaz.
    public void updateLastUpdate(LastUpdate lastUpdate) {
        String sql = "UPDATE LastUpdates SET updated_at = ? WHERE updated_key = ?";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, lastUpdate.getUpdated_at());
            pstmt.setString(2, lastUpdate.getUpdated_key());

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Güncelleme başarılı: " + lastUpdate.getUpdated_key());
            } else {
                System.out.println("Güncellenecek kayıt bulunamadı: " + lastUpdate.getUpdated_key());
            }

        } catch (SQLException e) {
            System.out.println("Güncelleme Hatası: " + e.getMessage());
        }
    }

    // 3. READ (Tekil Okuma)
    public LastUpdate getLastUpdateByKey(String key) {
        String sql = "SELECT * FROM LastUpdates WHERE updated_key = ?";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, key);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToLastUpdate(rs);
            }
        } catch (SQLException e) {
            System.out.println("Okuma Hatası: " + e.getMessage());
        }
        return null;
    }

    // 4. READ ALL (Hepsini Getir)
    public List<LastUpdate> getAllLastUpdates() {
        List<LastUpdate> list = new ArrayList<>();
        String sql = "SELECT * FROM LastUpdates";

        try (Connection conn = DatabaseManager.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapResultSetToLastUpdate(rs));
            }
        } catch (SQLException e) {
            System.out.println("Listeleme Hatası: " + e.getMessage());
        }
        return list;
    }

    // 5. DELETE (Silme)
    public void deleteLastUpdate(String key) {
        String sql = "DELETE FROM LastUpdates WHERE updated_key = ?";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, key);
            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Silindi: " + key);
            } else {
                System.out.println("Silinecek kayıt bulunamadı: " + key);
            }

        } catch (SQLException e) {
            System.out.println("Silme Hatası: " + e.getMessage());
        }
    }

    // Yardımcı Metot
    private LastUpdate mapResultSetToLastUpdate(ResultSet rs) throws SQLException {
        return new LastUpdate(
                rs.getString("updated_key"),
                rs.getString("updated_at")
        );
    }
}