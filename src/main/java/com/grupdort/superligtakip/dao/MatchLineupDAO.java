package com.grupdort.superligtakip.dao;

import com.grupdort.superligtakip.db.DatabaseManager;
import com.grupdort.superligtakip.model.*;
import java.sql.*;
import java.util.ArrayList;

public class MatchLineupDAO {

    public void insertLineup(MatchLineup ml) {
        String sql = "INSERT OR IGNORE INTO MatchLineups (fixture_ID, player_ID, team_ID, is_substitute, number, position) VALUES (?,?,?,?,?,?)";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, ml.getFixture().getFixture_ID());
            // Veritabanı TEXT olduğu için String'e çeviriyoruz
            pstmt.setString(2, String.valueOf(ml.getPlayer().getPlayer_ID()));
            pstmt.setInt(3, ml.getTeam().getTeam_ID());
            pstmt.setInt(4, ml.getIs_substitute());
            pstmt.setInt(5, ml.getNumber());
            pstmt.setInt(6, ml.getPosition());

            pstmt.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("Lineup Insert Error: " + ex.getMessage());
        }
    }

    public ArrayList<MatchLineup> getLineupsByFixture(int fixtureId) {
        ArrayList<MatchLineup> list = new ArrayList<>();
        String sql = "SELECT ml.*, p.player_name, t.team_name FROM MatchLineups ml " +
                "INNER JOIN Players p ON ml.player_ID = p.player_ID " +
                "INNER JOIN Teams t ON ml.team_ID = t.team_ID WHERE ml.fixture_ID = ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, fixtureId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToLineup(rs));
            }
        } catch (SQLException ex) {
            System.out.println("Lineup Read Error: " + ex.getMessage());
        }
        return list;
    }

    public void updateLineup(MatchLineup ml) {
        String sql = "UPDATE MatchLineups SET team_ID=?, is_substitute=?, number=?, position=? WHERE fixture_ID=? AND player_ID=?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, ml.getTeam().getTeam_ID());
            pstmt.setInt(2, ml.getIs_substitute());
            pstmt.setInt(3, ml.getNumber());
            pstmt.setInt(4, ml.getPosition());
            pstmt.setInt(5, ml.getFixture().getFixture_ID());
            pstmt.setString(6, String.valueOf(ml.getPlayer().getPlayer_ID()));

            pstmt.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("Lineup Update Error: " + ex.getMessage());
        }
    }

    // 4. DELETE: TEXT ID üzerinden silme
    public void deletePlayerFromLineup(int fixtureId, long playerId) {
        String sql = "DELETE FROM MatchLineups WHERE fixture_ID = ? AND player_ID = ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, fixtureId);
            pstmt.setString(2, String.valueOf(playerId));

            pstmt.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("Lineup Delete Error: " + ex.getMessage());
        }
    }

    // Ortak Mapleme Metodu
    private MatchLineup mapResultSetToLineup(ResultSet rs) throws SQLException {
        Fixture fx = new Fixture();
        fx.setFixture_ID(rs.getInt("fixture_ID"));

        Player p = new Player();
        // Veritabanındaki TEXT veriyi Java'nın beklediği Long'a çeviriyoruz
        String dbId = rs.getString("player_ID");
        p.setPlayer_ID(Long.parseLong(dbId));
        p.setPlayer_name(rs.getString("player_name"));

        Team t = new Team();
        t.setTeam_ID(rs.getInt("team_ID"));
        t.setTeam_name(rs.getString("team_name"));

        return new MatchLineup(
                fx, p, t,
                rs.getInt("is_substitute"),
                rs.getInt("number"),
                rs.getInt("position")
        );
    }
}