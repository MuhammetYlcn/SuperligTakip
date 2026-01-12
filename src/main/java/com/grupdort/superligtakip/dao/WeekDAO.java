package com.grupdort.superligtakip.dao;

import com.grupdort.superligtakip.db.DatabaseManager;
import com.grupdort.superligtakip.model.Season;
import com.grupdort.superligtakip.model.Week;

import java.sql.*;
import java.util.ArrayList;

public class WeekDAO {

    public void insertWeek(Week week) {
        String sql = "INSERT OR IGNORE INTO Weeks (week_ID, week_name, season_ID, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, week.getWeek_ID());
            pstmt.setString(2, week.getWeek_name());
            pstmt.setInt(3, week.getSeason().getSeason_ID());

            pstmt.setInt(4, week.getStatus());

            int rowsAffected = pstmt.executeUpdate();
            if(rowsAffected>0){
                System.out.println("hafta manuel ID ile eklendi: " + week.getWeek_name());
            }
            else{
                System.out.println("hafta zaten mevcut,işlem atlandı"+week.getWeek_name());
            }
        } catch (SQLException e) {
            System.out.println("Hafta ekleme hatası: " + e.getMessage());
        }
    }

    // 2. READ BY ID - Tekil hafta çekme
    public Week getWeekById(int weekId) {
        String sql = "SELECT * FROM Weeks WHERE week_ID = ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, weekId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Week week = new Week();
                week.setWeek_ID(rs.getInt("week_ID"));
                week.setWeek_name(rs.getString("week_name"));
                // Veritabanındaki 5 veya 7 gibi sayıları doğrudan modele set eder
                week.setStatus(rs.getInt("status"));
                return week;
            }
        } catch (SQLException e) {
            System.out.println("Hafta arama hatası: " + e.getMessage());
        }
        return null;
    }

    // 3. READ ALL (Season JOIN ile)
    public ArrayList<Week> getAllWeeks() {
        ArrayList<Week> weeks = new ArrayList<>();
        // JOIN kullanarak sezona ait is_active gibi ekstra bilgileri de alıyoruz
        String sql = "SELECT w.*, s.season_name, s.is_active FROM Weeks w " +
                "INNER JOIN Seasons s ON w.season_ID = s.season_ID";

        try (Connection conn = DatabaseManager.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Season season = new Season();
                season.setSeason_ID(rs.getInt("season_ID"));
                season.setSeason_name(rs.getString("season_name"));
                season.setIs_active(rs.getInt("is_active"));

                Week week = new Week();
                week.setWeek_ID(rs.getInt("week_ID"));
                week.setWeek_name(rs.getString("week_name"));
                week.setSeason(season);
                week.setStatus(rs.getInt("status"));

                weeks.add(week);
            }
        } catch (SQLException e) {
            System.out.println("Hafta listeleme hatası: " + e.getMessage());
        }
        return weeks;
    }

    // 4. UPDATE - Gün indekslerini günceller
    public void updateWeek(Week week) {
        String sql = "UPDATE Weeks SET week_name = ?, status = ? WHERE week_ID = ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, week.getWeek_name());
            pstmt.setInt(2, week.getStatus());
            pstmt.setInt(3, week.getWeek_ID());

            pstmt.executeUpdate();
            System.out.println("Hafta güncellendi, ID: " + week.getWeek_ID());
        } catch (SQLException e) {
            System.out.println("Hafta güncelleme hatası: " + e.getMessage());
        }
    }

    // 5. DELETE
    public void deleteWeek(int weekId) {
        String sql = "DELETE FROM Weeks WHERE week_ID = ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, weekId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Hafta silme hatası: " + e.getMessage());
        }
    }
}