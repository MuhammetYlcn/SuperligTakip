package com.grupdort.superligtakip.service;

import com.grupdort.superligtakip.api.TeamAPI;
import com.grupdort.superligtakip.dao.PlayerDAO;
import com.grupdort.superligtakip.dao.TeamDAO;
import com.grupdort.superligtakip.dto.team.PlayerDTO;
import com.grupdort.superligtakip.dto.team.TeamDTO;
import com.grupdort.superligtakip.dto.team.TeamResponseDTO;
import com.grupdort.superligtakip.model.Player;
import com.grupdort.superligtakip.model.Team;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerService {
    private final PlayerDAO playerDAO = new PlayerDAO();
    private final TeamDAO teamDAO = new TeamDAO();
    private final TeamAPI teamAPI = new TeamAPI();

    public void syncAllPlayers() {
        TeamResponseDTO response = teamAPI.fetchAllTeams();
        if (response == null || response.getResult() == null) return;

        for (TeamDTO teamDto : response.getResult()) {
            // Önce veritabanından ilgili takım nesnesini alıyoruz (Foreign Key için)
            Team currentTeam = teamDAO.getTeamById(teamDto.getTeamKey());

            if (currentTeam != null) {
                for (PlayerDTO pDto : teamDto.getPlayers()) {
                    syncSinglePlayer(pDto, currentTeam);
                }
            }
        }
    }

    private void syncSinglePlayer(PlayerDTO dto, Team team) {
        // PlayerDAO.getPlayerById kullanarak kontrol
        Player existing = playerDAO.getPlayerById(dto.getPlayerKey());
        Player playerModel = convertToModel(dto, team);

        if (existing == null) {
            playerDAO.insertPlayer(playerModel);
        } else {
            playerDAO.updatePlayer(playerModel);
        }
    }

    private Player convertToModel(PlayerDTO dto, Team team) {
        Player p = new Player();
        p.setPlayer_ID(dto.getPlayerKey());
        p.setTeam(team);
        p.setPlayer_name(dto.getPlayerName());
        p.setPosition(dto.getPlayerType());
        p.setAge(safeParseInt(dto.getPlayerAge()));
        p.setAppearances(safeParseInt(dto.getMatchPlayed()));
        p.setGoal(safeParseInt(dto.getGoals()));
        p.setAssist(safeParseInt(dto.getAssists()));
        p.setYellow_card(safeParseInt(dto.getYellowCards()));
        p.setRed_card(safeParseInt(dto.getRedCards()));
        p.setTotal_rating(safeParseDouble(dto.getRating()));
        return p;
    }
    public List<Player> getTopScorers() {
        // Tüm oyuncuları çek, gollere göre büyükten küçüğe sırala ve ilk 20'yi al
        return playerDAO.findAll().stream()
                .sorted((p1, p2) -> Integer.compare(p2.getGoal(), p1.getGoal()))
                .limit(20)
                .collect(Collectors.toList());
    }

    public List<Player> getTopAssisters() {
        // Tüm oyuncuları çek, asistlere göre büyükten küçüğe sırala ve ilk 20'yi al
        return playerDAO.findAll().stream()
                .sorted((p1, p2) -> Integer.compare(p2.getAssist(), p1.getAssist()))
                .limit(20)
                .collect(Collectors.toList());
    }

    private int safeParseInt(String val) {
        if (val == null || val.isEmpty() || val.equals("-")) return 0;
        try { return Integer.parseInt(val); } catch (Exception e) { return 0; }
    }

    private double safeParseDouble(String val) {
        if (val == null || val.isEmpty() || val.equals("-")) return 0.0;
        try { return Double.parseDouble(val); } catch (Exception e) { return 0.0; }
    }
}