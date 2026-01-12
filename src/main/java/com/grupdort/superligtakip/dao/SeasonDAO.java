package com.grupdort.superligtakip.dao;

import com.grupdort.superligtakip.db.DatabaseManager;
import com.grupdort.superligtakip.model.Season;

import java.sql.*;
import java.util.ArrayList;

public class SeasonDAO {

    // 1. READ BY ID: Sezon ID'sine göre tek bir sezon nesnesi getirir (Yeni Yardımcı Metot)
    public Season getSeasonById(int seasonId) {
        String sql = "SELECT * FROM Seasons WHERE season_ID = ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, seasonId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToSeason(rs);
            }
        } catch (SQLException e) {
            System.out.println("ID ile sezon çekme hatası: " + e.getMessage());
        }
        return null;
    }

    // 2. CREATE: Sezonu manuel ID ile kaydetme (API'den gelen ID'yi korumak için)
    public void insertSeason(Season season) {
        String sql = "INSERT OR IGNORE INTO Seasons (season_ID, season_name, is_active) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, season.getSeason_ID()); // Manuel ID eklendi
            pstmt.setString(2, season.getSeason_name());
            pstmt.setInt(3, season.getIs_active());

            int rowsAffected = pstmt.executeUpdate();
            if(rowsAffected>0){
                System.out.println("Sezon manuel ID ile eklendi: " + season.getSeason_name());
            }
            else{
                System.out.println("Sezon zaten mevcut,işlem atlandı"+season.getSeason_name());
            }
        } catch (SQLException e) {
            System.out.println("Sezon ekleme hatası: " + e.getMessage());
        }
    }

    // 3. READ ALL: Tüm sezonları getirir
    public ArrayList<Season> getAllSeasons() {
        ArrayList<Season> seasons = new ArrayList<>();
        String sql = "SELECT * FROM Seasons";
        try (Connection conn = DatabaseManager.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                seasons.add(mapResultSetToSeason(rs));
            }
        } catch (SQLException e) {
            System.out.println("Sezon listeleme hatası: " + e.getMessage());
        }
        return seasons;
    }

    // 4. UPDATE: Belirli bir ID'ye sahip sezonu günceller
    public void updateSeason(Season season) {
        String sql = "UPDATE Seasons SET season_name = ?, is_active = ? WHERE season_ID = ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, season.getSeason_name());
            pstmt.setInt(2, season.getIs_active());
            pstmt.setInt(3, season.getSeason_ID()); // ID üzerinden güncelleme
            pstmt.executeUpdate();
            System.out.println("Sezon güncellendi: " + season.getSeason_name());
        } catch (SQLException e) {
            System.out.println("Sezon güncelleme hatası: " + e.getMessage());
        }
    }

    // 5. DELETE: ID üzerinden silme
    public void deleteSeason(int seasonId) {
        String sql = "DELETE FROM Seasons WHERE season_ID = ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, seasonId);
            pstmt.executeUpdate();
            System.out.println("ID: " + seasonId + " olan sezon silindi.");
        } catch (SQLException e) {
            System.out.println("Sezon silme hatası: " + e.getMessage());
        }
    }

    private Season mapResultSetToSeason(ResultSet rs) throws SQLException {
        Season s = new Season();
        s.setSeason_ID(rs.getInt("season_ID"));
        s.setSeason_name(rs.getString("season_name"));
        s.setIs_active(rs.getInt("is_active"));
        return s;
    }
    public void setActiveSeason(int seasonId) {
        String resetSql = "UPDATE Seasons SET is_active = 0";
        String setSql = "UPDATE Seasons SET is_active = 1 WHERE season_ID = ?";

        try (Connection conn = DatabaseManager.connect()) {
            // Otomatik commit'i kapatıyoruz (Opsiyonel ama daha güvenli)
            conn.setAutoCommit(false);

            try (Statement stmt = conn.createStatement();
                 PreparedStatement pstmt = conn.prepareStatement(setSql)) {

                // 1. Tüm sezonları pasif yap
                stmt.executeUpdate(resetSql);

                // 2. Seçilen sezonu aktif yap
                pstmt.setInt(1, seasonId);
                pstmt.executeUpdate();

                // İşlemleri onayla
                conn.commit();
                System.out.println("Sezon ID " + seasonId + " aktif olarak işaretlendi.");
            } catch (SQLException e) {
                conn.rollback(); // Hata olursa geri al
                throw e;
            }
        } catch (SQLException e) {
            System.out.println("Aktif sezon güncelleme hatası: " + e.getMessage());
        }
    }
}