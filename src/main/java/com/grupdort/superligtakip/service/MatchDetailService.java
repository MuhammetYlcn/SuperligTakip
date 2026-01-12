package com.grupdort.superligtakip.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.grupdort.superligtakip.api.MatchDetailAPI;
import com.grupdort.superligtakip.dao.*;
import com.grupdort.superligtakip.dto.statistic.MatchFullDetailDTO;
import com.grupdort.superligtakip.model.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class MatchDetailService {
    private final MatchDetailAPI api = new MatchDetailAPI();
    private final EventDAO eventDAO = new EventDAO();
    private final MatchStatisticDAO statsDAO = new MatchStatisticDAO();
    private final MatchLineupDAO lineupDAO = new MatchLineupDAO();
    private final PlayerDAO playerDAO = new PlayerDAO();
    private final TeamDAO teamDAO = new TeamDAO();

    /**
     * Ana Metot: UI bu metodu çağırır.
     * Önce DB'ye bakar, yoksa API'den çeker ve maç bitmişse DB'ye kaydeder.
     */
    public MatchFullDetailDTO getAndProcessMatchDetails(int matchId) {
        try {
            // 1. ADIM: Veritabanında var mı kontrol et
            MatchStatistic dbStats = statsDAO.getStatisticByFixtureId(matchId);
            if (dbStats != null) {
                System.out.println(">>> Veri DB'den getiriliyor: ID " + matchId);
                ArrayList<Event> events = eventDAO.getEventsByFixture(matchId);
                ArrayList<MatchLineup> lineups = lineupDAO.getLineupsByFixture(matchId);
                return new MatchFullDetailDTO(dbStats, events, lineups);
            }

            // 2. ADIM: DB'de yoksa API'ye git
            System.out.println(">>> Veri API'den çekiliyor: ID " + matchId);
            JsonNode matchNode = api.fetchSingleMatchDetails(matchId);
            if (matchNode == null) return null;

            // DTO hazırlığı için temel objeler
            Fixture fixture = new Fixture();
            fixture.setFixture_ID(matchId);

            // 1. Takım Objelerini Doldur (NPE almanı engeller)
            Team homeTeam = new Team();
            homeTeam.setTeam_ID(matchNode.path("home_team_key").asInt());
            homeTeam.setTeam_name(matchNode.path("event_home_team").asText("Ev Sahibi"));
            fixture.setHome_team(homeTeam);

            Team awayTeam = new Team();
            awayTeam.setTeam_ID(matchNode.path("away_team_key").asInt());
            awayTeam.setTeam_name(matchNode.path("event_away_team").asText("Deplasman"));
            fixture.setAway_team(awayTeam);

            // --- 2. Skor ve Durum Bilgileri (Garantili Versiyon) ---

            String finalResult = matchNode.path("event_final_result").asText("0 - 0");
            int homeScore = 0;
            int awayScore = 0;

            // Skoru parçalamadan önce format kontrolü yapıyoruz
            if (finalResult != null && finalResult.contains("-")) {
                String[] scoreParts = finalResult.split("-");

                // Dizinin hem sol hem sağ tarafının dolu olduğunu garanti ediyoruz
                if (scoreParts.length >= 2) {
                    try {
                        // Trim kullanarak olası boşlukları temizleyip sayıya çeviriyoruz
                        homeScore = Integer.parseInt(scoreParts[0].trim());
                        awayScore = Integer.parseInt(scoreParts[1].trim());
                    } catch (NumberFormatException e) {
                        // Skor "1 - 0 (Pen)" gibi metin içerirse çökmemek için varsayılan 0 kalır
                        System.err.println("Skor sayıya çevrilemedi, 0 kabul edildi: " + finalResult);
                    }
                }
            }

            // Modeli doldurma
            fixture.setHome_score(homeScore);
            fixture.setAway_score(awayScore);
            fixture.setFinal_result(finalResult);

            // Durum ve Canlılık bilgileri
            fixture.setStatus(matchNode.path("event_status").asText("oynanmadi"));

            // event_live bazen String ("0") bazen Int (0) gelebilir, path().asInt() ikisini de çözer
            fixture.setEvent_live(matchNode.path("event_live").asInt(0));

            // 3. Tarih ve Saat (LocalDate/LocalTime dönüşümü)
            String dateStr = matchNode.path("event_date").asText(); // Örn: 2024-05-19
            if (!dateStr.isEmpty()) {
                fixture.setMatch_date(LocalDate.parse(dateStr));
            }

            String timeStr = matchNode.path("event_time").asText(); // Örn: 19:00
            if (!timeStr.isEmpty()) {
                fixture.setMatch_time(LocalTime.parse(timeStr));
            }

            // 4. Haftayı Doldur (Eğer API'den geliyorsa)
            Week week = new Week();
            week.setWeek_name(matchNode.path("league_round").asText("Bilinmiyor"));
            fixture.setWeek(week);

            // Artık MatchStatistic'e tam dolu fixture'ı veriyoruz
            MatchStatistic ms = new MatchStatistic();
            ms.setFixture(fixture);

            // İstatistikleri bellekte doldur (Daha DB'ye yazmadık)
            fillGeneralStatistics(ms, matchNode, false);

            // 3. ADIM: Maç bittiyse DB'ye kalıcı olarak kaydet
            String status = matchNode.path("event_status").asText();
            if ("Finished".equalsIgnoreCase(status)) {
                // Önce oyuncular ve kadrolar (Foreign Key için)
                processLineups(fixture, matchNode);
                // Sonra olaylar (Gol, Kart)
                processEventsAndExtractCardStats(fixture, matchNode);
                // En son istatistikler ve teknik direktörler
                fillGeneralStatistics(ms, matchNode, true);
                System.out.println(">>> Maç bittiği için DB'ye kalıcı olarak işlendi.");
            }

            // 4. ADIM: UI'ya taze veriyi dön
            return new MatchFullDetailDTO(ms, eventDAO.getEventsByFixture(matchId), lineupDAO.getLineupsByFixture(matchId));

        } catch (Exception e) {
            System.err.println("!!! Kritik Hata: " + e.toString());
            e.printStackTrace(); // Bu satır sana hatanın kaçıncı satırda olduğunu söyler.
            return null;
        }
    }

    private MatchStatistic processEventsAndExtractCardStats(Fixture fixture, JsonNode root) {
        MatchStatistic ms = new MatchStatistic();
        ms.setFixture(fixture);

        // --- GOL VE ASİSTLER ---
        root.path("goalscorers").forEach(g -> {
            Event e = createBaseEvent(fixture, g, "goal");
            e.setScore(g.path("score").asText());

            boolean isHome = !g.path("home_scorer_id").asText().isEmpty();
            long pId = isHome ? g.path("home_scorer_id").asLong() : g.path("away_scorer_id").asLong();
            String pName = isHome ? g.path("home_scorer").asText() : g.path("away_scorer").asText();

            ensurePlayerExists(pId, pName, root.get("home_team_key").asInt()); // DB Güvenliği

            e.setTeam(createTeam(isHome ? root.get("home_team_key").asInt() : root.get("away_team_key").asInt()));
            e.setPlayer(createPlayer(pId));

            long assistId = isHome ? g.path("home_assist_id").asLong() : g.path("away_assist_id").asLong();
            if(assistId > 0) {
                ensurePlayerExists(assistId, isHome ? g.path("home_assist").asText() : g.path("away_assist").asText(), root.get("home_team_key").asInt());
                e.setRelatedPlayer(createPlayer(assistId));
            }

            eventDAO.insertEvent(e);
        });

        // --- KARTLAR ---
        root.path("cards").forEach(c -> {
            Event e = createBaseEvent(fixture, c, "card");
            e.setInfo(c.path("card").asText());

            boolean isHome = !c.path("home_player_id").asText().isEmpty();
            long pId = isHome ? c.path("home_player_id").asLong() : c.path("away_player_id").asLong();
            ensurePlayerExists(pId, isHome ? c.path("home_fault").asText() : c.path("away_fault").asText(), root.get("home_team_key").asInt());

            e.setPlayer(createPlayer(pId));
            e.setTeam(createTeam(isHome ? root.get("home_team_key").asInt() : root.get("away_team_key").asInt()));
            eventDAO.insertEvent(e);
        });

        // --- DEĞİŞİKLİKLER ---
        root.path("substitutes").forEach(s -> {
            Event e = createBaseEvent(fixture, s, "substitution");
            JsonNode homeSub = s.get("home_scorer");
            int teamId = homeSub.has("out_id") ? root.get("home_team_key").asInt() : root.get("away_team_key").asInt();
            JsonNode subData = homeSub.has("out_id") ? homeSub : s.get("away_scorer");

            ensurePlayerExists(subData.get("in_id").asLong(), subData.get("in").asText(), teamId);
            ensurePlayerExists(subData.get("out_id").asLong(), subData.get("out").asText(), teamId);

            e.setTeam(createTeam(teamId));
            e.setPlayer(createPlayer(subData.get("in_id").asLong()));
            e.setRelatedPlayer(createPlayer(subData.get("out_id").asLong()));
            e.setInfo("In: " + subData.get("in").asText());
            eventDAO.insertEvent(e);
        });

        return ms;
    }

    private void fillGeneralStatistics(MatchStatistic ms, JsonNode root, boolean shouldSave) {
        JsonNode homeCoaches = root.path("lineups").path("home_team").path("coaches");
        if (homeCoaches.isArray() && homeCoaches.size() > 0) {
            ms.setHome_manager(homeCoaches.get(0).path("coache").asText("Bilinmiyor"));
        } else {
            ms.setHome_manager("Bilinmiyor");
        }
        JsonNode awayCoaches = root.path("lineups").path("away_team").path("coaches");
        if (awayCoaches.isArray() && awayCoaches.size() > 0) {
            ms.setAway_manager(awayCoaches.get(0).path("coache").asText("Bilinmiyor"));
        } else {
            ms.setAway_manager("Bilinmiyor");
        }

        root.path("statistics").forEach(s -> {
            String type = s.get("type").asText();
            String h = s.get("home").asText();
            String a = s.get("away").asText();

            switch (type) {
                case "Ball Possession": ms.setHome_possession(parsePercent(h)); ms.setAway_possession(parsePercent(a)); break;
                case "Shots Total": ms.setHome_total_shots(safeParse(h)); ms.setAway_total_shots(safeParse(a)); break;
                case "Shots On Goal": ms.setHome_shots_on_target(safeParse(h)); ms.setAway_shots_on_target(safeParse(a)); break;
                case "Corners": ms.setHome_corners(safeParse(h)); ms.setAway_corners(safeParse(a)); break;
                case "Fouls": ms.setHome_fouls(safeParse(h)); ms.setAway_fouls(safeParse(a)); break;
                case "Passes Accurate": ms.setHome_accurate_passes(safeParse(h)); ms.setAway_accurate_passes(safeParse(a)); break;
                case "Passes Total": ms.setHome_total_passes(safeParse(h)); ms.setAway_total_passes(safeParse(a)); break;
                case "Dangerous Attacks":
                    int hD = safeParse(h), aD = safeParse(a);
                    ms.setHome_pressure_index(hD+aD == 0 ? 0 : (hD * 100.0 / (hD+aD)));
                    ms.setAway_pressure_index(hD+aD == 0 ? 0 : (aD * 100.0 / (hD+aD)));
                    break;
            }
        });

        ms.hesaplaHome_pass_accuracy_perc();
        ms.hesaplaAway_pass_accuracy_perc();

        if (shouldSave) statsDAO.insertStatistic(ms);
    }

    private void processLineups(Fixture fixture, JsonNode root) {
        // 1. Ev Sahibi Takım ID'sini al ve kadrosunu işle
        int homeTeamId = root.path("home_team_key").asInt();
        processTeamLineup(fixture, root.path("lineups").path("home_team"), homeTeamId, 0);

        // 2. Deplasman Takım ID'sini al ve kadrosunu işle
        int awayTeamId = root.path("away_team_key").asInt();
        processTeamLineup(fixture, root.path("lineups").path("away_team"), awayTeamId, 0);
    }

    private void processTeamLineup(Fixture f, JsonNode teamNode, int teamId, int subStatus) {
        // BURASI KRİTİK: Her takım için yeni bir Team nesnesi oluşturulmalı
        Team team = new Team();
        team.setTeam_ID(teamId);

        // İlk 11 (Starting Lineups)
        teamNode.path("starting_lineups").forEach(p -> {
            long pId = p.path("player_key").asLong();
            ensurePlayerExists(pId, p.path("player").asText(), teamId);

            MatchLineup ml = new MatchLineup();
            ml.setFixture(f);
            ml.setPlayer(createPlayer(pId));
            ml.setTeam(team); // Parametre olarak gelen doğru takımı set ediyoruz
            ml.setIs_substitute(0);
            ml.setNumber(p.path("player_number").asInt());
            ml.setPosition(p.path("player_position").asInt());

            lineupDAO.insertLineup(ml);
        });

        // Yedekler (Substitutes)
        teamNode.path("substitutes").forEach(p -> {
            long pId = p.path("player_key").asLong();
            ensurePlayerExists(pId, p.path("player").asText(), teamId);

            MatchLineup ml = new MatchLineup();
            ml.setFixture(f);
            ml.setPlayer(createPlayer(pId));
            ml.setTeam(team); // Parametre olarak gelen doğru takımı set ediyoruz
            ml.setIs_substitute(1);
            ml.setNumber(p.path("player_number").asInt());
            ml.setPosition(0);

            lineupDAO.insertLineup(ml);
        });
    }

    private Event createBaseEvent(Fixture f, JsonNode node, String type) {
        Event e = new Event();
        e.setFixture(f);
        e.setTime(node.path("time").asText());
        e.setScore_time(node.path("info_time").asText());
        e.setType(type);
        // Benzersiz ID üretimi (FixtureID + TimeHash + Random)
        e.setEvent_ID(Math.abs(f.getFixture_ID() + e.getTime().hashCode() + (int)(Math.random() * 1000)));
        return e;
    }

    private void ensurePlayerExists(long pId, String pName, int teamId) {
        // PlayerDAO'da getPlayerById(long id) metodu olmalı
        Player existing = playerDAO.getPlayerById(pId);

        if (existing == null) {
            System.out.println("Yeni veya Farklı ID'li Oyuncu Tespit Edildi, Kaydediliyor: " + pName + " (ID: " + pId + ")");
            Player newPlayer = new Player();
            newPlayer.setPlayer_ID(pId);
            newPlayer.setPlayer_name(pName);
            newPlayer.setAge(0);
            newPlayer.setGoal(0);
            newPlayer.setAppearances(0);
            newPlayer.setAssist(0);
            newPlayer.setPosition("");
            newPlayer.setRed_card(0);
            newPlayer.setYellow_card(0);
            newPlayer.setTotal_rating(0);

            Team team = new Team();
            team.setTeam_ID(0);
            newPlayer.setTeam(team);

            // Diğer istatistikleri (gol, asist) bilmediğimiz için 0 olarak kaydedilir
            playerDAO.insertPlayer(newPlayer);
        }
    }

    private int safeParse(String s) { try { return Integer.parseInt(s.trim()); } catch(Exception e) { return 0; } }
    private int parsePercent(String s) { return safeParse(s.replace("%", "")); }
    private Player createPlayer(long id) {
        Player p = new Player();
        p.setPlayer_ID(id);
        p.setTeam(teamDAO.getTeamById(0));
        p.setAge(0);
        p.setPlayer_name("");
        p.setGoal(0);
        p.setAppearances(0);
        p.setAssist(0);
        p.setPosition("");
        p.setRed_card(0);
        p.setYellow_card(0);
        p.setTotal_rating(0);
        return p;
    }
    private Team createTeam(int id) { Team t = new Team(); t.setTeam_ID(id); return t; }

    public String getLogoUrl(int teamId){
        return teamDAO.getTeamById(teamId).getLogo();
    }
}