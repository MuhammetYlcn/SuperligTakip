package com.grupdort.superligtakip.service;

import com.grupdort.superligtakip.api.TeamAPI;
import com.grupdort.superligtakip.dao.PlayerDAO;
import com.grupdort.superligtakip.dao.TeamDAO;
import com.grupdort.superligtakip.dto.team.TeamDTO;
import com.grupdort.superligtakip.dto.team.TeamResponseDTO;
import com.grupdort.superligtakip.model.Player;
import com.grupdort.superligtakip.model.Team;
import com.grupdort.superligtakip.dto.team.TeamSquad;

import java.util.ArrayList;
import java.util.List;

public class TeamService {
    private final TeamDAO teamDAO = new TeamDAO();
    private final TeamAPI teamAPI = new TeamAPI();
    private final PlayerDAO playerDAO = new PlayerDAO();

    public void syncTeams() {
        TeamResponseDTO response = teamAPI.fetchAllTeams();
        if (response == null || response.getResult() == null) return;

        for (TeamDTO dto : response.getResult()) {
            Team existing = teamDAO.getTeamById(dto.getTeamKey());

            Team teamModel = new Team();
            teamModel.setTeam_ID(dto.getTeamKey());
            teamModel.setTeam_name(dto.getTeamName());
            teamModel.setLogo(dto.getTeamLogo());
            teamModel.setManager_name(dto.getCoachName());

            if (existing == null) {
                teamDAO.insertTeam(teamModel);
            } else {
                teamDAO.updateTeam(teamModel);
            }

            // Eğer veritabanında coach_name sütunu açtıysan:
            // updateCoachInDatabase(dto.getTeamKey(), dto.getCoachName());
        }
        Team ekstraTeam = new Team();
        ekstraTeam.setTeam_ID(0);
        ekstraTeam.setTeam_name("");
        ekstraTeam.setLogo("");
        ekstraTeam.setManager_name("");
        teamDAO.insertTeam(ekstraTeam);
    }

    public TeamSquad getTeamSquadSorted(int teamId) {
        // 1. Veritabanından o takımın tüm oyuncularını çekiyoruz (Senin DAO metodun)
        ArrayList<Player> allPlayers = playerDAO.getPlayersByTeam(teamId);

        // 2. Mevkilerine göre listeler oluşturuyoruz
        List<Player> goalkeepers = new ArrayList<>();
        List<Player> defenders = new ArrayList<>();
        List<Player> midfielders = new ArrayList<>();
        List<Player> forwards = new ArrayList<>();

        // 3. Oyuncuları mevkilerine göre ayırıyoruz (API'den gelen player_type'a göre)
        for (Player p : allPlayers) {
            String pos = p.getPosition().toLowerCase();
            if (pos.contains("goalkeeper")) {
                goalkeepers.add(p);
            } else if (pos.contains("defender")) {
                defenders.add(p);
            } else if (pos.contains("midfielder")) {
                midfielders.add(p);
            } else if (pos.contains("forward")) {
                forwards.add(p);
            }
        }

        // 4. Teknik direktör bilgisini TeamDAO'dan alıyoruz
        // Not: Teams tablosuna coach_name eklediğini varsayıyorum
        String coach = "Bilinmiyor";
        Team team = teamDAO.getTeamById(teamId);
        if(team != null) {
            coach = team.getManager_name();
        }

        // 5. Hepsini tek bir pakette UI'ya gönderiyoruz
        return new TeamSquad(coach, goalkeepers, defenders, midfielders, forwards);
    }
}