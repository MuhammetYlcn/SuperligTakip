package com.grupdort.superligtakip.dao;

import com.grupdort.superligtakip.db.DatabaseManager;
import com.grupdort.superligtakip.model.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class FixtureDAO {

    // 1. HAFTALIK FİKSTÜR
    public ArrayList<Fixture> getFixturesByWeek(int weekId) {
        ArrayList<Fixture> fixtures = new ArrayList<>();
        String sql = "SELECT f.*, " +
                "h.team_name AS h_name, h.logo AS h_logo, h.manager_name AS h_manager_name, " +
                "a.team_name AS a_name, a.logo AS a_logo, a.manager_name AS a_manager_name, " +
                "w.week_name, w.status AS w_status, " +
                "s.season_ID, s.season_name " +
                "FROM Fixtures f " +
                "INNER JOIN Teams h ON f.home_team_ID = h.team_ID " +
                "INNER JOIN Teams a ON f.away_team_ID = a.team_ID " +
                "INNER JOIN Weeks w ON f.week_ID = w.week_ID " +
                "INNER JOIN Seasons s ON w.season_ID = s.season_ID " +
                "WHERE f.week_ID = ? " +
                "ORDER BY f.match_date ASC, f.match_time ASC";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, weekId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                fixtures.add(mapResultSetToFixture(rs));
            }
        } catch (SQLException e) {
            System.out.println("Haftalık fikstür çekme hatası: " + e.getMessage());
        }
        return fixtures;
    }

    public Fixture getFixtureByTeamsAndWeek(int homeId, int awayId, int weekId) {
        String sql = "SELECT * FROM Fixtures WHERE home_team_ID = ? AND away_team_ID = ? AND week_ID = ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, homeId);
            pstmt.setInt(2, awayId);
            pstmt.setInt(3, weekId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Fixture f = new Fixture();
                f.setFixture_ID(rs.getInt("fixture_ID"));
                return f;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // 2. TÜM FİKSTÜR
    public ArrayList<Fixture> getAllFixtures() {
        ArrayList<Fixture> fixtures = new ArrayList<>();
        String sql = "SELECT f.*, " +
                "h.team_name AS h_name, h.logo AS h_logo, h.manager_name AS h_manager_name, " +
                "a.team_name AS a_name, a.logo AS a_logo, a.manager_name AS a_manager_name, " +
                "w.week_name, w.status AS w_status, " +
                "s.season_ID, s.season_name " +
                "FROM Fixtures f " +
                "INNER JOIN Teams h ON f.home_team_ID = h.team_ID " +
                "INNER JOIN Teams a ON f.away_team_ID = a.team_ID " +
                "INNER JOIN Weeks w ON f.week_ID = w.week_ID " +
                "INNER JOIN Seasons s ON w.season_ID = s.season_ID " +
                "ORDER BY f.week_ID ASC, f.match_date ASC, f.match_time ASC";

        try (Connection conn = DatabaseManager.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                fixtures.add(mapResultSetToFixture(rs));
            }
        } catch (SQLException e) {
            System.out.println("Fikstür listeleme hatası: " + e.getMessage());
        }
        return fixtures;
    }

    // 3. TEK TAKIM FİKSTÜRÜ
    public ArrayList<Fixture> getFixturesByTeam(int teamId) {
        ArrayList<Fixture> fixtures = new ArrayList<>();
        String sql = "SELECT f.*, " +
                "h.team_name AS h_name, h.logo AS h_logo, h.manager_name AS h_manager_name, " +
                "a.team_name AS a_name, a.logo AS a_logo, a.manager_name AS a_manager_name, " +
                "w.week_name, w.status AS w_status, " +
                "s.season_ID, s.season_name " +
                "FROM Fixtures f " +
                "INNER JOIN Teams h ON f.home_team_ID = h.team_ID " +
                "INNER JOIN Teams a ON f.away_team_ID = a.team_ID " +
                "INNER JOIN Weeks w ON f.week_ID = w.week_ID " +
                "INNER JOIN Seasons s ON w.season_ID = s.season_ID " +
                "WHERE f.home_team_ID = ? OR f.away_team_ID = ? " +
                "ORDER BY f.match_date ASC";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, teamId);
            pstmt.setInt(2, teamId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                fixtures.add(mapResultSetToFixture(rs));
            }
        } catch (SQLException e) {
            System.out.println("Takım fikstürü çekme hatası: " + e.getMessage());
        }
        return fixtures;
    }

    // 4. READ BY ID
    public Fixture getFixtureById(int fixtureId) {
        String sql = "SELECT f.*, " +
                "h.team_name AS h_name, h.logo AS h_logo, h.manager_name AS h_manager_name, " +
                "a.team_name AS a_name, a.logo AS a_logo, a.manager_name AS a_manager_name, " +
                "w.week_name, w.status AS w_status, " +
                "s.season_ID, s.season_name " +
                "FROM Fixtures f " +
                "INNER JOIN Teams h ON f.home_team_ID = h.team_ID " +
                "INNER JOIN Teams a ON f.away_team_ID = a.team_ID " +
                "INNER JOIN Weeks w ON f.week_ID = w.week_ID " +
                "INNER JOIN Seasons s ON w.season_ID = s.season_ID " +
                "WHERE f.fixture_ID = ?";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, fixtureId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToFixture(rs);
            }
        } catch (SQLException e) {
            System.out.println("ID ile maç çekme hatası: " + e.getMessage());
        }
        return null;
    }

    // 5. UPDATE
    public void updateFixture(Fixture existingFixture, Fixture newFixture) {
        String sql = "UPDATE Fixtures SET fixture_ID = ?, match_date = ?, match_time = ?, status = ?, " +
                "home_score = ?, away_score = ?, event_live = ?, final_result = ? " +
                "WHERE fixture_ID = ?";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            boolean useNew = newFixture.getStatus() != null &&
                    !newFixture.getStatus().trim().isEmpty() &&
                    !newFixture.getStatus().equals("-");

            Fixture source = useNew ? newFixture : existingFixture;

            // 1. parametre: YENİ ID (newFixture'dan gelen)
            pstmt.setInt(1, newFixture.getFixture_ID());

            // 2. Tarih
            if (newFixture.getMatch_date() != null) {
                pstmt.setString(2, newFixture.getMatch_date().toString());
            } else {
                pstmt.setNull(2, java.sql.Types.VARCHAR);
            }

            // 3. Saat
            if (newFixture.getMatch_time() != null) {
                pstmt.setString(3, newFixture.getMatch_time().toString());
            } else {
                pstmt.setNull(3, java.sql.Types.VARCHAR);
            }

            // 4. Durum (Status)
            pstmt.setString(4, source.getStatus());

            // 5-6-7. Skorlar ve Canlılık
            pstmt.setInt(5, source.getHome_score());
            pstmt.setInt(6, source.getAway_score());
            pstmt.setInt(7, source.getEvent_live());

            // 8. Final Result
            pstmt.setString(8, source.getFinal_result());

            // 9. parametre: ESKİ ID (existingFixture'dan gelen - WHERE koşulu için)
            pstmt.setInt(9, existingFixture.getFixture_ID());

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Maç ve ID Güncellendi. Eski ID: " + existingFixture.getFixture_ID() +
                        " -> Yeni ID: " + newFixture.getFixture_ID());
            }

        } catch (SQLException e) {
            System.out.println("Fikstür güncelleme hatası: " + e.getMessage());
        }
    }

    // 6. CREATE(INSERT OR IGNORE)
    public void insertFixture(Fixture fixture) {
        String sql = "INSERT OR IGNORE INTO Fixtures (fixture_ID, home_team_ID, away_team_ID, week_ID, match_date, match_time, status, home_score, away_score, event_live, final_result) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, fixture.getFixture_ID());
            pstmt.setInt(2, fixture.getHome_team().getTeam_ID());
            pstmt.setInt(3, fixture.getAway_team().getTeam_ID());
            pstmt.setInt(4, fixture.getWeek().getWeek_ID());
            pstmt.setString(5, fixture.getMatch_date().toString());
            pstmt.setString(6, fixture.getMatch_time().toString());
            pstmt.setString(7, fixture.getStatus());
            pstmt.setInt(8, fixture.getHome_score());
            pstmt.setInt(9, fixture.getAway_score());
            pstmt.setInt(10, fixture.getEvent_live());
            pstmt.setString(11, fixture.getFinal_result()); // Yeni alan eklendi

            int rows = pstmt.executeUpdate();
            if (rows > 0) System.out.println("Maç eklendi: " + fixture.getFixture_ID());
            else System.out.println("Maç zaten var: " + fixture.getFixture_ID());

        } catch (SQLException e) {
            System.out.println("Ekleme hatası: " + e.getMessage());
        }
    }

    // 9. DELETE
    public void deleteFixture(int fixtureId) {
        String sql = "DELETE FROM Fixtures WHERE fixture_ID = ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, fixtureId);
            pstmt.executeUpdate();
        } catch (SQLException e) { System.out.println("Silme hatası: " + e.getMessage()); }
    }

    // MERKEZİ MAPPING
    private Fixture mapResultSetToFixture(ResultSet rs) throws SQLException {
        Team home = new Team(rs.getInt("home_team_ID"), rs.getString("h_name"), rs.getString("h_logo"), rs.getString("h_manager_name"));
        Team away = new Team(rs.getInt("away_team_ID"), rs.getString("a_name"), rs.getString("a_logo"), rs.getString("a_manager_name"));

        Season season = new Season();
        season.setSeason_ID(rs.getInt("season_ID"));
        season.setSeason_name(rs.getString("season_name"));

        Week week = new Week();
        week.setWeek_ID(rs.getInt("week_ID"));
        week.setWeek_name(rs.getString("week_name"));
        week.setStatus(rs.getInt("w_status"));
        week.setSeason(season);

        Fixture fx = new Fixture();
        fx.setFixture_ID(rs.getInt("fixture_ID"));
        fx.setHome_team(home);
        fx.setAway_team(away);
        fx.setWeek(week);
        fx.setMatch_date(LocalDate.parse(rs.getString("match_date")));
        fx.setMatch_time(LocalTime.parse(rs.getString("match_time")));
        fx.setStatus(rs.getString("status"));
        fx.setHome_score(rs.getInt("home_score"));
        fx.setAway_score(rs.getInt("away_score"));
        fx.setEvent_live(rs.getInt("event_live"));
        fx.setFinal_result(rs.getString("final_result")); // Veritabanından okuma eklendi

        return fx;
    }
}