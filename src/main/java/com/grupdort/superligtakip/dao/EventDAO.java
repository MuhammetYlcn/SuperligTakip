package com.grupdort.superligtakip.dao;

import com.grupdort.superligtakip.db.DatabaseManager;
import com.grupdort.superligtakip.model.*;
import java.sql.*;
import java.util.ArrayList;

public class EventDAO {

    public void insertEvent(Event e) {
        String sql = "INSERT OR IGNORE INTO Events (event_ID, fixture_ID, team_ID, player_name, related_player_name, score_time, score, time, type, info) VALUES (?,?,?,?,?,?,?,?,?,?)";

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
        String sql = "UPDATE Events SET fixture_ID=?, team_ID=?, player_name=?, related_player_name=?, score_time=?, score=?, time=?, type=?, info=? WHERE event_ID=?";

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

    /* SQL MANTIĞI:
       1. INSTR(e.time || '+', '+') -> '+' işaretinin yerini bulur.
       2. SUBSTR ile '+' öncesini ve sonrasını ayırır.
       3. CAST(... AS INTEGER) ile sayıya çevirip toplar.
       Böylece "90+5" verisi 95 olarak hesaplanır ve doğru sıraya girer.
    */
        String sql = "SELECT e.*, t.team_name, " +
                "(CAST(SUBSTR(e.time, 1, INSTR(e.time || '+', '+') - 1) AS INTEGER) + " +
                " CAST(CASE WHEN INSTR(e.time, '+') > 0 THEN SUBSTR(e.time, INSTR(e.time, '+') + 1) ELSE 0 END AS INTEGER)) as sort_minute " +
                "FROM Events e " +
                "LEFT JOIN Teams t ON e.team_ID = t.team_ID " +
                "WHERE e.fixture_ID = ? " +
                "ORDER BY sort_minute ASC";

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
        // Fixture ID
        pstmt.setInt(startIndex, e.getFixture().getFixture_ID());

        //  Team ID
        if (e.getTeam() != null) {
            pstmt.setInt(startIndex + 1, e.getTeam().getTeam_ID());
        } else {
            pstmt.setNull(startIndex + 1, Types.INTEGER);
        }

        if (e.getPlayer() != null) {
            pstmt.setString(startIndex + 2, e.getPlayer());
        } else {
            pstmt.setNull(startIndex + 2, Types.VARCHAR);
        }

        if (e.getRelatedPlayer() != null) {
            pstmt.setString(startIndex + 3, e.getRelatedPlayer());
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
        Fixture fx = new Fixture();
        fx.setFixture_ID(rs.getInt("fixture_ID"));

        Team team = new Team();
        team.setTeam_ID(rs.getInt("team_ID"));
        team.setTeam_name(rs.getString("team_name"));

        return new Event(
                rs.getInt("event_ID"),
                fx,
                team,
                rs.getString("player_name"),         // DB'den direkt String okuyoruz
                rs.getString("related_player_name"), // DB'den direkt String okuyoruz
                rs.getString("score_time"),
                rs.getString("score"),
                rs.getString("time"),
                rs.getString("type"),
                rs.getString("info")
        );
    }
}