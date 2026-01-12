package com.grupdort.superligtakip.service;

import com.grupdort.superligtakip.api.FixtureAPI;
import com.grupdort.superligtakip.dao.*;
import com.grupdort.superligtakip.dto.fixture.FixtureDTO;
import com.grupdort.superligtakip.dto.fixture.FixtureResponseDTO;
import com.grupdort.superligtakip.model.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class FixtureService {
    private FixtureAPI fixtureAPI = new FixtureAPI();
    private FixtureDAO fixtureDAO = new FixtureDAO();
    private WeekDAO weekDAO = new WeekDAO();
    private TeamDAO teamDAO = new TeamDAO();
    private SeasonDAO seasonDAO = new SeasonDAO();

    /**
     * Tüm sezon fikstürünü API'den çeker ve veritabanına kaydeder.
     * Artık start_date ve end_date alanları hiçbir şekilde kullanılmıyor.
     */
    public void syncAllSeasonFixtures() {
        // API aralığı (Örn: 2024-2025 sezonu verileri için uygun tarihler)
        String from = "2025-08-01";
        String to = "2026-06-01";

        System.out.println("Fikstür senkronizasyonu başladı...");

        try {
            FixtureResponseDTO response = fixtureAPI.fetchFixtures(from, to);
            if (response == null || response.getResult() == null) {
                System.out.println("Hata: API'den boş veri döndü.");
                return;
            }

            // Sezon nesnesi (Sadece ID ve Name)
            Season currentSeason = new Season(2025, "2025/2026", 1);
            seasonDAO.insertSeason(currentSeason);

            for (FixtureDTO dto : response.getResult()) {

                // 1. HAFTA (ROUND) İŞLEMİ
                // API'den gelen "Round 15" ifadesini sadece "15" rakamına çeviriyoruz.
                int weekNum = Integer.parseInt(dto.getLeagueRound().replaceAll("[^0-9]", ""));
                int homeId = Integer.parseInt(dto.getHomeTeamKey());
                int awayId = Integer.parseInt(dto.getAwayTeamKey());

                // Veritabanında bu hafta var mı kontrol et, yoksa ekle
                if (weekDAO.getWeekById(weekNum) == null) {
                    Week week = new Week();
                    week.setWeek_ID(weekNum);
                    week.setWeek_name(String.valueOf(weekNum)); // Hafta ismi "15" olacak
                    week.setSeason(currentSeason);
                    week.setStatus(0); // Başlangıçta oynanmadı

                    weekDAO.insertWeek(week);
                }

                // 2. TAKIMLARI GÜNCELLE/KONTROL ET
                // (Foreign Key hatası almamak için takımların Teams tablosunda olması şart)
                Team home = teamDAO.getTeamById(Integer.parseInt(dto.getHomeTeamKey()));
                if (home == null){
                    home = new Team(Integer.parseInt(dto.getHomeTeamKey()), dto.getHomeTeamName(), dto.getHomeTeamLogo(), "TD");
                    teamDAO.insertTeam(home);
                }
                Team away = teamDAO.getTeamById(Integer.parseInt(dto.getAwayTeamKey()));
                if (away == null){
                    away = new Team(Integer.parseInt(dto.getAwayTeamKey()), dto.getAwayTeamName(), dto.getAwayTeamLogo(), "TD");
                    teamDAO.insertTeam(away);
                }


                // 2. VERİTABANINDA BU MAÇ ZATEN VAR MI? (ID'den bağımsız kontrol)
                // Bunun için FixtureDAO'ya küçük bir yardımcı metot ekleyeceğiz (Aşağıda)
                Fixture existing = fixtureDAO.getFixtureByTeamsAndWeek(homeId, awayId, weekNum);

                // 3. MAÇ (FIXTURE) MODELİNİ DOLDUR
                Fixture f = new Fixture();
                f.setFixture_ID(Integer.parseInt(dto.getEventKey()));
                f.setHome_team(home);
                f.setAway_team(away);

                // Haftayı tekrar DB'den çekmeye gerek yok, ID'si ile yeni bir nesne bağlayabiliriz
                Week w = new Week();
                w.setWeek_ID(weekNum);
                f.setWeek(w);

                try {
                    if (dto.getEventDate() != null && !dto.getEventDate().isEmpty()) {
                        f.setMatch_date(LocalDate.parse(dto.getEventDate()));
                    } else {
                        // Eğer API'den tarih gelmediyse sistemin çökmemesi için bugünü veya eski tarihi ata
                        f.setMatch_date(LocalDate.now());
                        System.out.println("Uyarı: Maç ID " + dto.getEventKey() + " için tarih bulunamadı, bugüne set edildi.");
                    }
                } catch (Exception e) {
                    f.setMatch_date(LocalDate.now());
                    System.err.println("Tarih parse hatası: " + e.getMessage());
                }
                try {
                    String timeStr = dto.getEventTime();
                    if (timeStr != null && !timeStr.isEmpty()) {
                        // Esnek saat formatlayıcı (saniyeli veya saniyesiz)
                        java.time.format.DateTimeFormatter timeFormatter = new java.time.format.DateTimeFormatterBuilder()
                                .appendPattern("H:mm")
                                .optionalStart()
                                .appendPattern(":ss")
                                .optionalEnd()
                                .toFormatter();

                        // API'den gelen ham saati al
                        LocalTime apiTime = LocalTime.parse(timeStr, timeFormatter);

                        // --- SAAT DİLİMİ DÜZELTMESİ ---
                        // API UTC gönderiyor, biz UTC+3 (veya gözlemine göre +1) ekliyoruz
                        LocalTime localTime = apiTime.plusHours(1);

                        f.setMatch_time(localTime);
                    } else {
                        f.setMatch_time(LocalTime.of(0, 0));
                    }
                } catch (Exception e) {
                    System.err.println("Saat işleme hatası: " + e.getMessage());
                    f.setMatch_time(LocalTime.of(0, 0));
                }
                //f.setMatch_time(LocalTime.parse(dto.getEventTime()));

                // Durum ve Canlı Skor Ayarları
                int isLive = "1".equals(dto.getEventLive()) ? 1 : 0;
                f.setEvent_live(isLive);
                f.setStatus(dto.getEventStatus()); // "Finished", "21:00" veya "45'"

                // Skorları güvenli bir şekilde çek
                f.setHome_score(0);
                f.setAway_score(0);
                if (dto.getFinalResult() != null && dto.getFinalResult().contains("-")) {
                    String[] scores = dto.getFinalResult().split("-");
                    if (scores.length >= 2) {
                        try {
                            f.setHome_score(Integer.parseInt(scores[0].trim()));
                            f.setAway_score(Integer.parseInt(scores[1].trim()));
                        } catch (Exception e) { /* Skor sayı değilse 0 kalsın */ }
                    }
                }

                f.setFinal_result(dto.getDisplayResult());

                if (existing == null) {
                    fixtureDAO.insertFixture(f);
                } else {
                    // Eğer maç varsa ama ID'si farklıysa veya verisi daha doluysa eskisini güncelliyoruz:
                    fixtureDAO.updateFixture(existing, f);
                    //fixtureDAO.insertFixture(f);
                }
            }
            System.out.println("Senkronizasyon başarıyla tamamlandı.");

        } catch (Exception e) {
            System.err.println("Fikstür işleme sırasında hata: " + e.getMessage());
        }
    }

    /**
     * Belirli bir haftanın (Round) tüm maçlarını sıralı getirir.
     */
    public List<Fixture> getFixturesForWeek(int weekId) {
        return fixtureDAO.getFixturesByWeek(weekId);
    }

    /**
     * Bir takıma ait tüm maçları getirir.
     */
    public List<Fixture> getFixturesByTeam(int teamId) {
        return fixtureDAO.getFixturesByTeam(teamId);
    }

    /**
     * Bugünün tarihine bakarak oynanacak ilk haftayı bulur.
     * Döngü 1'den 38'e kadar bakar, bugünden sonra maçı olan ilk haftayı döndürür.
     * Eğer tüm maçlar geçmişse (sezon bitmişse) son haftayı (38) döner.
     */
    public int findCurrentWeek() {
        LocalDate today = LocalDate.now();

        // 1. Haftadan 38. Haftaya kadar kontrol et
        for (int i = 1; i <= 38; i++) {
            // O haftanın maçlarını veritabanından çek
            List<Fixture> matches = fixtureDAO.getFixturesByWeek(i);

            // Eğer o hafta hiç maç yoksa (fikstür çekilmemişse) atla
            if (matches == null || matches.isEmpty()) continue;

            // Bu haftadaki maçlara bak
            for (Fixture f : matches) {
                // Eğer maçın tarihi BUGÜN veya GELECEKTE ise, demek ki güncel hafta budur.
                if (f.getMatch_date() != null &&
                        (f.getMatch_date().isAfter(today) || f.getMatch_date().isEqual(today))) {
                    return i;
                }
            }
        }
        return 38; // Eğer gelecekte hiç maç yoksa lig bitmiştir, son haftayı göster.
    }
}