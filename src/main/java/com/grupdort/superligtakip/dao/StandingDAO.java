package com.grupdort.superligtakip.dao;

import com.grupdort.superligtakip.db.DatabaseManager;
import com.grupdort.superligtakip.model.Season;
import com.grupdort.superligtakip.model.Standing;
import com.grupdort.superligtakip.model.Team;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StandingDAO {

    // 1. READ BY ID: standing_ID'ye göre tekil veri getir (İstediğin yeni fonksiyon)
    public Standing getStandingById(int standingId) {
        String sql = "SELECT s.*, t.team_name, t.logo, t.manager_name, sea.season_name " +
                "FROM Standings s " +
                "INNER JOIN Teams t ON s.team_ID = t.team_ID " +
                "INNER JOIN Seasons sea ON s.season_ID = sea.season_ID " +
                "WHERE s.standing_ID = ?";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, standingId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToStanding(rs);
            }
        } catch (SQLException e) {
            System.out.println("ID ile puan durumu çekme hatası: " + e.getMessage());
        }
        return null;
    }

    // 2. CREATE: Yeni sezon ve takım için boş satır açar
    public void insertStanding(int teamId, int seasonId) {
        // standing_ID AUTOINCREMENT olduğu için SQL sorgusuna eklemiyoruz.
        String sql = "INSERT OR IGNORE INTO Standings (team_ID, season_ID, rank, won, drawn, lost, points, played, goals_for, goals_against, goals_diff) " +
                "VALUES (?, ?, 0, 0, 0, 0, 0, 0, 0, 0, 0)";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, teamId);
            pstmt.setInt(2, seasonId);
            int rowsAffected = pstmt.executeUpdate();
            if(rowsAffected>0){
                System.out.println("tablo manuel ID ile eklendi: " );
            }
            else{
                System.out.println("tablo zaten mevcut,işlem atlandı");
            }
        } catch (SQLException e) {
            System.out.println("Puan satırı oluşturma hatası: " + e.getMessage());
        }
    }

    // 3. READ ALL (BY SEASON): Belirli bir sezona ait tabloyu sıralı getirir
    public List<Standing> getStandingsBySeason(int seasonId) {
        List<Standing> standings = new ArrayList<>();
        String sql = "SELECT s.*, t.team_name, t.logo, t.manager_name, sea.season_name " +
                "FROM Standings s " +
                "INNER JOIN Teams t ON s.team_ID = t.team_ID " +
                "INNER JOIN Seasons sea ON s.season_ID = sea.season_ID " +
                "WHERE s.season_ID = ? " +
                "ORDER BY s.points DESC, s.goals_diff DESC, s.goals_for DESC";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, seasonId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                standings.add(mapResultSetToStanding(rs));
            }
        } catch (SQLException e) {
            System.out.println("Sezonluk puan durumu çekme hatası: " + e.getMessage());
        }
        return standings;
    }

    // 4. UPDATE: Mevcut verileri günceller
    public void updateStanding(Standing standing) {
        String sql = "UPDATE Standings SET rank = ?, won = ?, drawn = ?, lost = ?, points = ?, " +
                "played = ?, goals_for = ?, goals_against = ?, goals_diff = ? " +
                "WHERE team_ID = ? AND season_ID = ?";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, standing.getRank());
            pstmt.setInt(2, standing.getWon());
            pstmt.setInt(3, standing.getDrawn());
            pstmt.setInt(4, standing.getLost());
            pstmt.setInt(5, standing.getPoints());
            pstmt.setInt(6, standing.getPlayed());
            pstmt.setInt(7, standing.getGoal_for());
            pstmt.setInt(8, standing.getGoal_against());
            pstmt.setInt(9, standing.getGoal_diff());
            pstmt.setInt(10, standing.getTeam().getTeam_ID());
            pstmt.setInt(11, standing.getSeason().getSeason_ID());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Puan durumu güncelleme hatası: " + e.getMessage());
        }
    }

    // 5. DELETE: ID bazlı silme
    public void deleteStanding(int standingId) {
        String sql = "DELETE FROM Standings WHERE standing_ID = ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, standingId);
            pstmt.executeUpdate();
            System.out.println("Puan verisi silindi.");
        } catch (SQLException e) {
            System.out.println("Silme hatası: " + e.getMessage());
        }
    }
    public void deleteAllStandings() {
        String sql = "DELETE FROM Standings"; // Tüm satırları siler
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.executeUpdate();
            System.out.println("Tablo tamamen temizlendi.");
        } catch (SQLException e) {
            System.out.println("Tablo temizleme hatası: " + e.getMessage());
        }
    }
    private Standing mapResultSetToStanding(ResultSet rs) throws SQLException {
        Team team = new Team(
                rs.getInt("team_ID"),
                rs.getString("team_name"),
                rs.getString("logo"),
                rs.getString("manager_name")
        );

        Season season = new Season();
        season.setSeason_ID(rs.getInt("season_ID"));
        season.setSeason_name(rs.getString("season_name"));

        Standing st = new Standing();
        st.setStanding_ID(rs.getInt("standing_ID"));
        st.setTeam(team);
        st.setSeason(season);
        st.setRank(rs.getInt("rank"));
        st.setWon(rs.getInt("won"));
        st.setDrawn(rs.getInt("drawn"));
        st.setLost(rs.getInt("lost"));
        st.setPoints(rs.getInt("points"));
        st.setPlayed(rs.getInt("played"));
        st.setGoal_for(rs.getInt("goals_for"));
        st.setGoal_against(rs.getInt("goals_against"));

        return st;
    }
}