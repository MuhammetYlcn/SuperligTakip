package com.grupdort.superligtakip.dao;

import com.grupdort.superligtakip.db.DatabaseManager;
import com.grupdort.superligtakip.model.Team;

import java.sql.*;
import java.util.ArrayList;

public class TeamDAO {

    // 1. TÜM TAKIMLARI GETİR (READ)
    public ArrayList<Team> getAllTeams() {
        ArrayList<Team> teamList = new ArrayList<>();
        String sql = "SELECT * FROM Teams";

        try (Connection conn = DatabaseManager.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Team team = new Team(
                        rs.getInt("team_ID"),
                        rs.getString("team_name"),
                        rs.getString("logo"),
                        rs.getString("manager_name")
                );
                teamList.add(team);
            }
        } catch (SQLException e) {
            System.out.println("Veri çekme hatası: " + e.getMessage());
        }
        return teamList;
    }

    // 2. YENİ TAKIM EKLE (CREATE) - API'den gelen ID ile beraber
    public void insertTeam(Team team) {
        // SQL sorgusuna team_ID eklendi çünkü API'den gelen anahtarı kullanmalıyız
        String sql = "INSERT OR IGNORE INTO Teams (team_ID, team_name, logo, manager_name) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, team.getTeam_ID()); // API'den gelen team_key
            pstmt.setString(2, team.getTeam_name());
            pstmt.setString(3, team.getLogo());
            pstmt.setString(4, team.getManager_name());

            int rowsAffected = pstmt.executeUpdate();
            if(rowsAffected>0){
                System.out.println("takım manuel ID ile eklendi: ");
            }
            else{
                System.out.println("takım zaten mevcut,işlem atlandı");
            }

        } catch (SQLException e) {
            System.out.println("Ekleme hatası: " + e.getMessage());
        }
    }

    // 3. TAKIM GÜNCELLE (UPDATE)
    public void updateTeam(Team team) {
        // ID değişmez, sadece isim, logo ve kısa ad güncellenir
        String sql = "UPDATE Teams SET team_name = ?, logo = ?, manager_name = ? WHERE team_ID = ?";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, team.getTeam_name());
            pstmt.setString(2, team.getLogo());
            pstmt.setString(3, team.getManager_name());
            pstmt.setInt(4, team.getTeam_ID()); // Hangi takımın güncelleneceğini belirleyen ID

            pstmt.executeUpdate();
            System.out.println("Takım güncellendi: " + team.getTeam_name());

        } catch (SQLException e) {
            System.out.println("Güncelleme hatası: " + e.getMessage());
        }
    }

    // --- Servis Katmanı İçin Ekstra Yardımcı Metot ---
    public Team getTeamById(int teamId) {
        String sql = "SELECT * FROM Teams WHERE team_ID = ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, teamId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Team(
                        rs.getInt("team_ID"),
                        rs.getString("team_name"),
                        rs.getString("logo"),
                        rs.getString("manager_name")
                );
            }
        } catch (SQLException e) {
            System.out.println("Takım arama hatası: " + e.getMessage());
        }
        return null;
    }

    // 4. TAKIM SİL (DELETE)
    public void deleteTeam(int teamId) {
        String sql = "DELETE FROM Teams WHERE team_ID = ?";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, teamId);
            pstmt.executeUpdate();
            System.out.println("ID'si " + teamId + " olan takım silindi.");

        } catch (SQLException e) {
            System.out.println("Silme hatası: " + e.getMessage());
        }
    }
}