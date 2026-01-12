package com.grupdort.superligtakip.dao;

import com.grupdort.superligtakip.db.DatabaseManager;
import com.grupdort.superligtakip.model.Player;
import com.grupdort.superligtakip.model.Team;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PlayerDAO {

    // 1. READ BY ID: TEXT'i Long'a çevirerek getir
    public Player getPlayerById(long playerId) {
        String sql = "SELECT p.*, t.team_name, t.logo, t.manager_name FROM Players p " +
                "INNER JOIN Teams t ON p.team_ID = t.team_ID WHERE p.player_ID = ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, String.valueOf(playerId));
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToPlayer(rs);
            }
        } catch (SQLException e) {
            System.out.println("ID ile oyuncu çekme hatası: " + e.getMessage());
        }
        return null;
    }

    // 2. FIND ALL: Tüm oyuncuları getir
    public ArrayList<Player> findAll() {
        ArrayList<Player> playerList = new ArrayList<>();
        String sql = "SELECT p.*, t.team_name, t.logo, t.manager_name FROM Players p " +
                "INNER JOIN Teams t ON p.team_ID = t.team_ID";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                playerList.add(mapResultSetToPlayer(rs));
            }
        } catch (SQLException e) {
            System.out.println("Tüm oyuncuları çekme hatası: " + e.getMessage());
        }
        return playerList;
    }
    public ArrayList<Player> getPlayersByTeam(int teamId) {
        ArrayList<Player> playerList = new ArrayList<>();
        String sql = "SELECT p.*, t.team_name, t.logo, t.manager_name FROM Players p " +
                "INNER JOIN Teams t ON p.team_ID = t.team_ID WHERE p.team_ID = ?";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, teamId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                playerList.add(mapResultSetToPlayer(rs));
            }
        } catch (SQLException e) {
            System.out.println("Takıma göre oyuncu çekme hatası: " + e.getMessage());
        }
        return playerList;
    }

    // 4. CREATE: INSERT OR IGNORE yapısı korundu
    public void insertPlayer(Player player) {
        String sql = "INSERT OR IGNORE INTO Players (player_ID, team_ID, player_name, position, age, appearances, " +
                "goal, assist, yellow_card, red_card, total_rating) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, String.valueOf(player.getPlayer_ID()));
            pstmt.setInt(2, player.getTeam().getTeam_ID());
            pstmt.setString(3, player.getPlayer_name());
            pstmt.setString(4, player.getPosition());
            pstmt.setInt(5, player.getAge());
            pstmt.setInt(6, player.getAppearances());
            pstmt.setInt(7, player.getGoal());
            pstmt.setInt(8, player.getAssist());
            pstmt.setInt(9, player.getYellow_card());
            pstmt.setInt(10, player.getRed_card());
            pstmt.setDouble(11, player.getTotal_rating());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Oyuncu ekleme hatası: " + e.getMessage());
        }
    }

    // 5. UPDATE: Manuel ID güncelleme mantığı korundu
    public void updatePlayer(Player player) {
        String sql = "UPDATE Players SET player_name = ?, position = ?, age = ?, appearances = ?, " +
                "goal = ?, assist = ?, yellow_card = ?, red_card = ? , total_rating = ? " +
                "WHERE player_ID = ?";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, player.getPlayer_name());
            pstmt.setString(2, player.getPosition());
            pstmt.setInt(3, player.getAge());
            pstmt.setInt(4, player.getAppearances());
            pstmt.setInt(5, player.getGoal());
            pstmt.setInt(6, player.getAssist());
            pstmt.setInt(7, player.getYellow_card());
            pstmt.setInt(8, player.getRed_card());
            pstmt.setDouble(9, player.getTotal_rating());
            pstmt.setString(10, String.valueOf(player.getPlayer_ID()));

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Oyuncu güncelleme hatası: " + e.getMessage());
        }
    }

    // 6. DELETE: Silme yapısı korundu
    public void deletePlayer(long playerId) {
        String sql = "DELETE FROM Players WHERE player_ID = ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, String.valueOf(playerId));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Oyuncu silme hatası: " + e.getMessage());
        }
    }
    public Player getPlayerByNameAndTeam(String playerName, int teamId) {
        String sql = "SELECT p.*, t.team_name, t.logo, t.manager_name FROM Players p " +
                "INNER JOIN Teams t ON p.team_ID = t.team_ID " +
                "WHERE p.player_name = ? AND p.team_ID = ?";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, playerName.trim()); // Boşlukları temizle
            pstmt.setInt(2, teamId);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                System.out.println("   ✅ BULUNDU: " + playerName + " -> ID: "+ rs.getString("player_ID"));
                return mapResultSetToPlayer(rs); // Mevcut oyuncuyu döndür
            }
            else{
                System.out.println("   ❌ BULUNAMADI: " + playerName + " (Veritabanında yok)"+teamId);
            }
        } catch (SQLException e) {
            System.out.println("İsimle oyuncu bulma hatası: " + e.getMessage());
        }
        return null; // Bulunamazsa null döner
    }
    public void updatePlayerId(long oldId, long newId) {
        // Eğer ID'ler aynıysa işlem yapma
        if (oldId == newId) return;

        String sql = "UPDATE Players SET player_ID = ? WHERE player_ID = ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Veritabanı TEXT olduğu için String'e çeviriyoruz
            pstmt.setString(1, String.valueOf(newId));
            pstmt.setString(2, String.valueOf(oldId));

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("✅ Oyuncu ID Güncellendi: " + oldId + " -> " + newId);
            } else {
                System.out.println("⚠️ Güncellenecek oyuncu bulunamadı (Eski ID: " + oldId + ")");
            }
        } catch (SQLException e) {
            System.out.println("❌ ID Güncelleme Hatası: " + e.getMessage());
        }
    }

    // Ortak Mapleme Metodu: TEXT DB -> Long Model dönüşümü burada yapılıyor
    private Player mapResultSetToPlayer(ResultSet rs) throws SQLException {
        Team team = new Team(
                rs.getInt("team_ID"),
                rs.getString("team_name"),
                rs.getString("logo"),
                rs.getString("manager_name")
        );
        Player player = new Player();

        // Veritabanındaki TEXT player_ID'yi çekip Long'a çeviriyoruz
        String dbId = rs.getString("player_ID");
        player.setPlayer_ID(Long.parseLong(dbId));

        player.setTeam(team);
        player.setPlayer_name(rs.getString("player_name"));
        player.setPosition(rs.getString("position"));
        player.setAge(rs.getInt("age"));
        player.setAppearances(rs.getInt("appearances"));
        player.setGoal(rs.getInt("goal"));
        player.setAssist(rs.getInt("assist"));
        player.setYellow_card(rs.getInt("yellow_card"));
        player.setRed_card(rs.getInt("red_card"));
        player.setTotal_rating(rs.getDouble("total_rating"));
        return player;
    }
}