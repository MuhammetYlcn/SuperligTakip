package com.grupdort.superligtakip.dao;

import com.grupdort.superligtakip.db.DatabaseManager;
import com.grupdort.superligtakip.model.*;
import java.sql.*;
import java.util.ArrayList;

public class EventDAO {

    public void insertEvent(Event e) {
        String sql = "INSERT OR IGNORE INTO Events (event_ID, fixture_ID, team_ID, player_ID, related_player_ID, score_time, score, time, type, info) VALUES (?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, e.getEvent_ID());
            setEventParameters(pstmt, e, 2);

            pstmt.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("Event Insert Error: " + ex.getMessage());
        }
    }

    public void updateEvent(Event e) {
        String sql = "UPDATE Events SET fixture_ID=?, team_ID=?, player_ID=?, related_player_ID=?, score_time=?, score=?, time=?, type=?, info=? WHERE event_ID=?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            setEventParameters(pstmt, e, 1);
            pstmt.setInt(10, e.getEvent_ID());

            pstmt.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("Event Update Error: " + ex.getMessage());
        }
    }

    public ArrayList<Event> getEventsByFixture(int fixtureId) {
        ArrayList<Event> list = new ArrayList<>();

        // --- DEĞİŞİKLİK BURADA: ORDER BY KISMI SİLİNDİ ---
        // Artık sıralamayı veritabanı değil, Java (Service) tarafı yapacak.
        String sql = "SELECT e.*, t.team_name, p.player_name, rp.player_name AS rp_name FROM Events e " +
                "LEFT JOIN Teams t ON e.team_ID = t.team_ID " +
                "LEFT JOIN Players p ON e.player_ID = p.player_ID " +
                "LEFT JOIN Players rp ON e.related_player_ID = rp.player_ID " +
                "WHERE e.fixture_ID = ?";
        // -------------------------------------------------

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, fixtureId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToEvent(rs));
            }
        } catch (SQLException ex) {
            System.out.println("Event Read Error: " + ex.getMessage());
        }
        return list;
    }

    public void deleteEvent(int eventId) {
        String sql = "DELETE FROM Events WHERE event_ID = ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, eventId);
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("Event Delete Error: " + ex.getMessage());
        }
    }

    private void setEventParameters(PreparedStatement pstmt, Event e, int startIndex) throws SQLException {
        pstmt.setInt(startIndex, e.getFixture().getFixture_ID());
        pstmt.setInt(startIndex + 1, e.getTeam().getTeam_ID());

        if (e.getPlayer() != null && e.getPlayer().getPlayer_ID() != 0) {
            pstmt.setString(startIndex + 2, String.valueOf(e.getPlayer().getPlayer_ID()));
        } else {
            pstmt.setNull(startIndex + 2, Types.VARCHAR);
        }

        if (e.getRelatedPlayer() != null && e.getRelatedPlayer().getPlayer_ID() != 0) {
            pstmt.setString(startIndex + 3, String.valueOf(e.getRelatedPlayer().getPlayer_ID()));
        } else {
            pstmt.setNull(startIndex + 3, Types.VARCHAR);
        }

        pstmt.setString(startIndex + 4, e.getScore_time());
        pstmt.setString(startIndex + 5, e.getScore());
        pstmt.setString(startIndex + 6, e.getTime());
        pstmt.setString(startIndex + 7, e.getType());
        pstmt.setString(startIndex + 8, e.getInfo());
    }

    private Event mapResultSetToEvent(ResultSet rs) throws SQLException {
        Team team = new Team();
        team.setTeam_ID(rs.getInt("team_ID"));
        team.setTeam_name(rs.getString("team_name"));

        Player p = new Player();
        String pId = rs.getString("player_ID");
        if (pId != null) {
            p.setPlayer_ID(Long.parseLong(pId));
            p.setPlayer_name(rs.getString("player_name"));
        }

        Player rp = new Player();
        String rpId = rs.getString("related_player_ID");
        if (rpId != null) {
            rp.setPlayer_ID(Long.parseLong(rpId));
            rp.setPlayer_name(rs.getString("rp_name"));
        }

        Fixture fx = new Fixture();
        fx.setFixture_ID(rs.getInt("fixture_ID"));

        return new Event(
                rs.getInt("event_ID"),
                fx, team, p, rp,
                rs.getString("score_time"),
                rs.getString("score"),
                rs.getString("time"),
                rs.getString("type"),
                rs.getString("info")
        );
    }
}