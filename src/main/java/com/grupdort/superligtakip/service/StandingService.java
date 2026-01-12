package com.grupdort.superligtakip.service;

import com.grupdort.superligtakip.api.StandingAPI;
import com.grupdort.superligtakip.dao.SeasonDAO;
import com.grupdort.superligtakip.dao.StandingDAO;
import com.grupdort.superligtakip.dao.TeamDAO;
import com.grupdort.superligtakip.dto.standing.StandingDTO;
import com.grupdort.superligtakip.dto.standing.StandingResponseDTO;
import com.grupdort.superligtakip.model.Season;
import com.grupdort.superligtakip.model.Standing;
import com.grupdort.superligtakip.model.Team;

import java.util.List;



public class StandingService {
    private StandingAPI standingAPI;
    private StandingDAO standingDAO;
    private SeasonDAO seasonDAO;
    private TeamDAO teamDAO;

    public StandingService() {
        this.standingAPI = new StandingAPI();
        this.standingDAO = new StandingDAO();
        this.seasonDAO = new SeasonDAO();
        this.teamDAO = new TeamDAO();
    }

    public StandingService(StandingAPI standingAPI,StandingDAO standingDAO,SeasonDAO seasonDAO,TeamDAO teamDAO) {
        this.standingAPI = standingAPI;
        this.standingDAO =standingDAO;
        this.seasonDAO =seasonDAO;
        this.teamDAO =teamDAO;
    }

    public void syncStandingWithAPI() {
        try {
            int seasonId = 2025;
            Season currentSeason = new Season(seasonId, "2025/2026", 1);
            seasonDAO.insertSeason(currentSeason);

            // 1. ÖNCE API'DEN VERİYİ ÇEK (Hemen silme!)
            StandingResponseDTO response = standingAPI.fetchStandings();

            // Eğer API cevap vermezse işlem yapma (Böylece eski veriler silinmez, korunur)
            if (response == null || response.getSuccess() != 1) {
                System.out.println("API'den veri alınamadı, işlem iptal edildi.");
                return;
            }

            // 2. VERİ GELDİĞİNE EMİNİZ, ŞİMDİ TEMİZLİK YAPABİLİRİZ
            standingDAO.deleteAllStandings();
            System.out.println("Tablo temizlendi, güncel veriler yazılıyor...");

            for (StandingDTO dto : response.getResult().getTotal()) {
                int teamId = Integer.parseInt(dto.getTeam_key());

                Team team = teamDAO.getTeamById(teamId);

                // Takım Tablosu Kontrolü
                if (team == null) {
                    team = new Team(teamId, dto.getStanding_team(), dto.getTeam_logo(), "TD");
                    teamDAO.insertTeam(team);
                }

                // --- KRİTİK DÜZELTME BURASI ---
                // Tabloyu komple sildiğimiz için (deleteAllStandings), takım eski de olsa yeni de olsa
                // puan tablosunda satırı yok demektir. Her halükarda yeni satır açmalıyız.
                standingDAO.insertStanding(teamId, seasonId);

                // Verileri Hazırla ve Güncelle (Insert boş satır açtığı için üstüne yazıyoruz)
                Standing standing = new Standing();
                standing.setTeam(team);
                standing.setSeason(currentSeason);
                standing.setRank(Integer.parseInt(dto.getStanding_place()));
                standing.setPlayed(Integer.parseInt(dto.getStanding_P()));
                standing.setWon(Integer.parseInt(dto.getStanding_W()));
                standing.setDrawn(Integer.parseInt(dto.getStanding_D()));
                standing.setLost(Integer.parseInt(dto.getStanding_L()));
                standing.setGoal_for(Integer.parseInt(dto.getStanding_F()));
                standing.setGoal_against(Integer.parseInt(dto.getStanding_A()));
                standing.setPoints(Integer.parseInt(dto.getStanding_PTS()));

                standingDAO.updateStanding(standing);
            }
            System.out.println("Veriler başarıyla işlendi.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Standing> getStandingsForTable(int seasonId) {
        // DAO'daki ilişkisel metodu kullanıyoruz
        List<Standing> standings = standingDAO.getStandingsBySeason(seasonId);

        // Burada gerekirse veriyi Controller'a göndermeden önce
        // ek işlemler (filtreleme, özel sıralama vb.) yapabilirsin.

        return standings;
    }
}