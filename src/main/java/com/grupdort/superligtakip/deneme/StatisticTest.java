package com.grupdort.superligtakip.deneme;

import com.grupdort.superligtakip.dto.statistic.MatchFullDetailDTO;
import com.grupdort.superligtakip.model.*;
import com.grupdort.superligtakip.service.MatchDetailService;
import java.util.ArrayList;

public class StatisticTest {
    public static void main(String[] args) {
        MatchDetailService service = new MatchDetailService();

        // TEST: Bir maç ID'si belirleyelim (Örn: Fenerbahçe maçı 1607292 veya taze bir ID)
        int randomMatchId = 1607284;

        System.out.println(">>> İşlem Başlatılıyor: Maç ID " + randomMatchId);

        // Servis katmanı tüm Cache-Aside (Önce DB, sonra API) mantığını yönetir
        MatchFullDetailDTO data = service.getAndProcessMatchDetails(randomMatchId);

        if (data != null) {
            printMatchReport(data);
        } else {
            System.err.println("!!! HATA: Maç verisi ne veritabanından ne de API'den alınamadı.");
        }
    }

    private static void printMatchReport(MatchFullDetailDTO data) {
        MatchStatistic ms = data.getStatistics();
        ArrayList<Event> events = data.getEvents();
        ArrayList<MatchLineup> lineups = data.getLineups();

        System.out.println("\n" + "=".repeat(60));
        System.out.println(String.format("%30s", "MAÇ DETAY RAPORU"));
        System.out.println("=".repeat(60));

        // 1. TEMEL BİLGİLER
        System.out.println("MAÇ: " + ms.getFixture().getFinal_result());
        System.out.println("TEKNİK DİREKTÖRLER: " + ms.getHome_manager() + " vs " + ms.getAway_manager());
        System.out.println("-".repeat(60));

        // 2. İSTATİSTİKLER (DTO'dan geliyor)
        System.out.println(String.format("%-20s | %-10s | %-10s", "İSTATİSTİK", "EV", "DEPLASMAN"));
        System.out.println("-".repeat(60));
        System.out.println(String.format("%-20s | %-10s | %-10s", "Topla Oynama", "%" + ms.getHome_possession(), "%" + ms.getAway_possession()));
        System.out.println(String.format("%-20s | %-10s | %-10s", "Toplam Şut", ms.getHome_total_shots(), ms.getAway_total_shots()));
        System.out.println(String.format("%-20s | %-10s | %-10s", "İsabetli Şut", ms.getHome_shots_on_target(), ms.getAway_shots_on_target()));
        System.out.println(String.format("%-20s | %-10s | %-10s", "Baskı İndeksi", String.format("%.1f", ms.getHome_pressure_index()), String.format("%.1f", ms.getAway_pressure_index())));

        // 3. OLAYLAR (GOL, KART, SUB)
        System.out.println("\n--- MAÇIN ÖNEMLİ ANLARI ---");
        if (events.isEmpty()) System.out.println("Olay kaydı bulunamadı.");
        for (Event e : events) {
            String pName = (e.getPlayer() != null) ? e.getPlayer().getPlayer_name() : "";
            System.out.println(String.format("%-4s' [%-12s] %s %s", e.getTime(), e.getType().toUpperCase(), e.getInfo(), pName));
        }

        // --- EV SAHİBİ KADROSU ---
        System.out.println("\n--- " + ms.getFixture().getHome_team().getTeam_name().toUpperCase() + " KADROSU ---");
        System.out.print("İLK 11: ");
        data.getLineups().stream()
                .filter(l -> l.getTeam().getTeam_ID() == ms.getFixture().getHome_team().getTeam_ID() && l.getIs_substitute() == 0)
                .forEach(l -> System.out.print(l.getPlayer().getPlayer_name() + " (" + l.getNumber() + "), "));

        System.out.print("\n\nYEDEKLER: ");
        data.getLineups().stream()
                .filter(l -> l.getTeam().getTeam_ID() == ms.getFixture().getHome_team().getTeam_ID() && l.getIs_substitute() == 1)
                .forEach(l -> System.out.print(l.getPlayer().getPlayer_name() + ", "));
        System.out.println();


        // --- DEPLASMAN KADROSU ---
        System.out.println("\n--- " + ms.getFixture().getAway_team().getTeam_name().toUpperCase() + " KADROSU ---");
        System.out.print("İLK 11: ");
        data.getLineups().stream()
                .filter(l -> l.getTeam().getTeam_ID() == ms.getFixture().getAway_team().getTeam_ID() && l.getIs_substitute() == 0)
                .forEach(l -> System.out.print(l.getPlayer().getPlayer_name() + " (" + l.getNumber() + "), "));

        System.out.print("\n\nYEDEKLER: ");
        data.getLineups().stream()
                .filter(l -> l.getTeam().getTeam_ID() == ms.getFixture().getAway_team().getTeam_ID() && l.getIs_substitute() == 1)
                .forEach(l -> System.out.print(l.getPlayer().getPlayer_name() + ", "));
        System.out.println();

        System.out.println("\n" + "=".repeat(60));
    }

    private static void printLineupSection(String title, ArrayList<MatchLineup> lineups, boolean isHome) {
        System.out.println("\n--- " + title + " KADROSU ---");
        System.out.print("İLK 11: ");
        // Burada basit bir mantıkla ilk 11'leri ayırıyoruz (Örn: Takım ID kontrolü gerekebilir)
        lineups.stream()
                .filter(l -> l.getIs_substitute() == 0) // Serviste is_substitute 0 ise ilk 11 demiştik
                .forEach(l -> System.out.print(l.getPlayer().getPlayer_name() + ", "));
        System.out.println();
    }
}