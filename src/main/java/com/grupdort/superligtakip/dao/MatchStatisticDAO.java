package com.grupdort.superligtakip.dao;

import com.grupdort.superligtakip.db.DatabaseManager;
import com.grupdort.superligtakip.model.*;
import java.sql.*;
import java.util.ArrayList;

public class MatchStatisticDAO {

    // 1. CREATE (Insert) - Manager alanları eklendi
    public void insertStatistic(MatchStatistic ms) {
        String sql = "INSERT OR IGNORE INTO MatchStatistics (fixture_ID, home_possession, away_possession, " +
                "home_pressure_index, away_pressure_index, home_total_shots, away_total_shots, " +
                "home_shots_on_target, away_shots_on_target, home_shots_off_target, away_shots_off_target, " +
                "home_blocked_shots, away_blocked_shots, home_saves, away_saves, " +
                "home_total_passes, away_total_passes, home_accurate_passes, away_accurate_passes, " +
                "home_pass_accuracy_perc, away_pass_accuracy_perc, home_corners, away_corners, " +
                "home_offsides, away_offsides, home_fouls, away_fouls, home_yellow_cards, away_yellow_cards, " +
                "home_red_card_from_yellows, away_red_card_from_yellows, home_direct_red_cards, away_direct_red_cards, " +
                "home_manager, away_manager) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            setStatisticParameters(pstmt, ms);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("İstatistik kaydedildi. Fixture ID: " + ms.getFixture().getFixture_ID());
            } else {
                System.out.println("İstatistik zaten mevcut veya fixture bulunamadı, işlem atlandı.");
            }
        } catch (SQLException e) {
            System.out.println("Ekleme Hatası: " + e.getMessage());
        }
    }

    // 2. READ (Get by Fixture ID) - Manager alanları eklendi
    public MatchStatistic getStatisticByFixtureId(int fixtureId) {
        String sql = "SELECT ms.*, f.*, h.team_name AS h_name, a.team_name AS a_name " +
                "FROM MatchStatistics ms " +
                "INNER JOIN Fixtures f ON ms.fixture_ID = f.fixture_ID " +
                "INNER JOIN Teams h ON f.home_team_ID = h.team_ID " +
                "INNER JOIN Teams a ON f.away_team_ID = a.team_ID " +
                "WHERE ms.fixture_ID = ?";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, fixtureId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToMatchStatistic(rs);
            }
        } catch (SQLException e) {
            System.out.println("Tekil Okuma Hatası: " + e.getMessage());
        }
        return null;
    }

    // 3. UPDATE - Manager alanları eklendi
    public void updateStatistic(MatchStatistic ms) {
        String sql = "UPDATE MatchStatistics SET home_possession = ?, away_possession = ?, " +
                "home_pressure_index = ?, away_pressure_index = ?, home_total_shots = ?, away_total_shots = ?, " +
                "home_shots_on_target = ?, away_shots_on_target = ?, home_shots_off_target = ?, away_shots_off_target = ?, " +
                "home_blocked_shots = ?, away_blocked_shots = ?, home_saves = ?, away_saves = ?, " +
                "home_total_passes = ?, away_total_passes = ?, home_accurate_passes = ?, away_accurate_passes = ?, " +
                "home_pass_accuracy_perc = ?, away_pass_accuracy_perc = ?, home_corners = ?, away_corners = ?, " +
                "home_offsides = ?, away_offsides = ?, home_fouls = ?, away_fouls = ?, home_yellow_cards = ?, away_yellow_cards = ?, " +
                "home_red_card_from_yellows = ?, away_red_card_from_yellows = ?, home_direct_red_cards = ?, away_direct_red_cards = ?, " +
                "home_manager = ?, away_manager = ? " +
                "WHERE fixture_ID = ?";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            setUpdateParameters(pstmt, ms);
            pstmt.setInt(35, ms.getFixture().getFixture_ID()); // Index 35 oldu (34 parametre + 1 ID)
            pstmt.executeUpdate();
            System.out.println("İstatistik güncellendi.");
        } catch (SQLException e) {
            System.out.println("Güncelleme Hatası: " + e.getMessage());
        }
    }

    // 4. DELETE (Değişmedi)
    public void deleteStatistic(int fixtureId) {
        String sql = "DELETE FROM MatchStatistics WHERE fixture_ID = ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, fixtureId);
            pstmt.executeUpdate();
            System.out.println("Fixture ID " + fixtureId + " olan istatistik silindi.");
        } catch (SQLException e) {
            System.out.println("Silme Hatası: " + e.getMessage());
        }
    }

    // --- YARDIMCI METOTLAR ---

    private void setStatisticParameters(PreparedStatement pstmt, MatchStatistic ms) throws SQLException {
        pstmt.setInt(1, ms.getFixture().getFixture_ID());
        setUpdateParameters(pstmt, ms, 2);
    }

    private void setUpdateParameters(PreparedStatement pstmt, MatchStatistic ms) throws SQLException {
        setUpdateParameters(pstmt, ms, 1);
    }

    private void setUpdateParameters(PreparedStatement pstmt, MatchStatistic ms, int startIdx) throws SQLException {
        int i = startIdx;
        pstmt.setInt(i++, ms.getHome_possession());
        pstmt.setInt(i++, ms.getAway_possession());
        pstmt.setDouble(i++, ms.getHome_pressure_index());
        pstmt.setDouble(i++, ms.getAway_pressure_index());
        pstmt.setInt(i++, ms.getHome_total_shots());
        pstmt.setInt(i++, ms.getAway_total_shots());
        pstmt.setInt(i++, ms.getHome_shots_on_target());
        pstmt.setInt(i++, ms.getAway_shots_on_target());
        pstmt.setInt(i++, ms.getHome_shots_off_target());
        pstmt.setInt(i++, ms.getAway_shots_off_target());
        pstmt.setInt(i++, ms.getHome_blocked_shots());
        pstmt.setInt(i++, ms.getAway_blocked_shots());
        pstmt.setInt(i++, ms.getHome_saves());
        pstmt.setInt(i++, ms.getAway_saves());
        pstmt.setInt(i++, ms.getHome_total_passes());
        pstmt.setInt(i++, ms.getAway_total_passes());
        pstmt.setInt(i++, ms.getHome_accurate_passes());
        pstmt.setInt(i++, ms.getAway_accurate_passes());
        pstmt.setDouble(i++, ms.getHome_pass_accuracy_perc());
        pstmt.setDouble(i++, ms.getAway_pass_accuracy_perc());
        pstmt.setInt(i++, ms.getHome_corners());
        pstmt.setInt(i++, ms.getAway_corners());
        pstmt.setInt(i++, ms.getHome_offsides());
        pstmt.setInt(i++, ms.getAway_offsides());
        pstmt.setInt(i++, ms.getHome_fouls());
        pstmt.setInt(i++, ms.getAway_fouls());
        pstmt.setInt(i++, ms.getHome_yellow_cards());
        pstmt.setInt(i++, ms.getAway_yellow_cards());
        pstmt.setInt(i++, ms.getHome_red_card_from_yellows());
        pstmt.setInt(i++, ms.getAway_red_card_from_yellows());
        pstmt.setInt(i++, ms.getHome_direct_red_cards());
        pstmt.setInt(i++, ms.getAway_direct_red_cards());
        pstmt.setString(i++, ms.getHome_manager());
        pstmt.setString(i++, ms.getAway_manager());
    }

    private MatchStatistic mapResultSetToMatchStatistic(ResultSet rs) throws SQLException {
        Team home = new Team(); home.setTeam_ID(rs.getInt("home_team_ID")); home.setTeam_name(rs.getString("h_name"));
        Team away = new Team(); away.setTeam_ID(rs.getInt("away_team_ID")); away.setTeam_name(rs.getString("a_name"));

        Fixture fx = new Fixture();
        fx.setFixture_ID(rs.getInt("fixture_ID"));
        fx.setHome_team(home);
        fx.setAway_team(away);

        MatchStatistic ms = new MatchStatistic();
        ms.setFixture(fx);
        ms.setHome_possession(rs.getInt("home_possession"));
        ms.setAway_possession(rs.getInt("away_possession"));
        ms.setHome_pressure_index(rs.getDouble("home_pressure_index"));
        ms.setAway_pressure_index(rs.getDouble("away_pressure_index"));
        ms.setHome_total_shots(rs.getInt("home_total_shots"));
        ms.setAway_total_shots(rs.getInt("away_total_shots"));
        ms.setHome_shots_on_target(rs.getInt("home_shots_on_target"));
        ms.setAway_shots_on_target(rs.getInt("away_shots_on_target"));
        ms.setHome_shots_off_target(rs.getInt("home_shots_off_target"));
        ms.setAway_shots_off_target(rs.getInt("away_shots_off_target"));
        ms.setHome_blocked_shots(rs.getInt("home_blocked_shots"));
        ms.setAway_blocked_shots(rs.getInt("away_blocked_shots"));
        ms.setHome_saves(rs.getInt("home_saves"));
        ms.setAway_saves(rs.getInt("away_saves"));
        ms.setHome_total_passes(rs.getInt("home_total_passes"));
        ms.setAway_total_passes(rs.getInt("away_total_passes"));
        ms.setHome_accurate_passes(rs.getInt("home_accurate_passes"));
        ms.setAway_accurate_passes(rs.getInt("away_accurate_passes"));

        ms.hesaplaHome_pass_accuracy_perc();
        ms.hesaplaAway_pass_accuracy_perc();

        ms.setHome_corners(rs.getInt("home_corners"));
        ms.setAway_corners(rs.getInt("away_corners"));
        ms.setHome_offsides(rs.getInt("home_offsides"));
        ms.setAway_offsides(rs.getInt("away_offsides"));
        ms.setHome_fouls(rs.getInt("home_fouls"));
        ms.setAway_fouls(rs.getInt("away_fouls"));
        ms.setHome_yellow_cards(rs.getInt("home_yellow_cards"));
        ms.setAway_yellow_cards(rs.getInt("away_yellow_cards"));
        ms.setHome_red_card_from_yellows(rs.getInt("home_red_card_from_yellows"));
        ms.setAway_red_card_from_yellows(rs.getInt("away_red_card_from_yellows"));
        ms.setHome_direct_red_cards(rs.getInt("home_direct_red_cards"));
        ms.setAway_direct_red_cards(rs.getInt("away_direct_red_cards"));
        ms.setHome_manager(rs.getString("home_manager"));
        ms.setAway_manager(rs.getString("away_manager"));

        return ms;
    }
}