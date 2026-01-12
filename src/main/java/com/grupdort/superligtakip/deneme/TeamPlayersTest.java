package com.grupdort.superligtakip.deneme;

import com.grupdort.superligtakip.model.Player;
import com.grupdort.superligtakip.dto.team.TeamSquad;
import com.grupdort.superligtakip.service.PlayerService;
import com.grupdort.superligtakip.service.TeamService;

public class TeamPlayersTest {
    public static void main(String[] args) {
        // 1. Servisleri Başlat
        TeamService teamService = new TeamService();
        PlayerService playerService = new PlayerService();

        System.out.println(">>> VERİ SENKRONİZASYONU BAŞLIYOR...");

        // 2. Önce Takımları Kaydet/Güncelle
        // (Bu işlem Teams tablosunu doldurur, coach_name bilgisini de günceller)
        teamService.syncTeams();
        System.out.println(">>> Takımlar güncellendi.");

        // 3. Sonra Oyuncuları Kaydet/Güncelle
        // (API'den gelen istatistikleri DB'ye yazar)
        playerService.syncAllPlayers();
        System.out.println(">>> Oyuncular ve istatistikler güncellendi.");

        System.out.println("\n--------------------------------------------------");
        System.out.println("      BEŞİKTAŞ J.K. KADRO DETAYLARI (DB'DEN)");
        System.out.println("--------------------------------------------------\n");

        // 4. Beşiktaş'ın (ID: 187) verilerini mevkilerine göre DB'den çek
        TeamSquad bjkSquad = teamService.getTeamSquadSorted(7694);

        // 5. Konsola Yazdır
        System.out.println("TEKNİK DİREKTÖR: " + bjkSquad.getCoachName());
        System.out.println();

        System.out.println("--- KALECİLER ---");
        printList(bjkSquad.getGoalkeepers());

        System.out.println("\n--- DEFANS OYUNCULARI ---");
        printList(bjkSquad.getDefenders());

        System.out.println("\n--- ORTA SAHA OYUNCULARI ---");
        printList(bjkSquad.getMidfielders());

        System.out.println("\n--- FORVETLER ---");
        printList(bjkSquad.getForwards());
    }

    // Konsola düzgün yazdırmak için yardımcı metot
    private static void printList(java.util.List<Player> players) {
        if (players.isEmpty()) {
            System.out.println("  [Kayıt Yok]");
        } else {
            for (Player p : players) {
                // Senin Player modelindeki toString() metodun burada devreye girecek
                System.out.println("  " + p.toString());
            }
        }
    }
}