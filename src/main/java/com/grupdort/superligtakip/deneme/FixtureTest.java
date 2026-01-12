package com.grupdort.superligtakip.deneme;

import com.grupdort.superligtakip.model.Fixture;
import com.grupdort.superligtakip.service.FixtureService;
import java.util.List;

public class FixtureTest {
    public static void main(String[] args) {
        FixtureService fixtureService = new FixtureService();

        // 1. TEST: istenilen ID'li Takımın Fikstürü (Örn: Fenerbahçe veya Galatasaray)
        System.out.println("\n********** 187 ID'Lİ TAKIMIN FİKSTÜRÜ **********");
        List<Fixture> teamFixtures = fixtureService.getFixturesByTeam(187);

        if (teamFixtures.isEmpty()) {
            System.out.println("Bu ID'ye sahip takımın maçı bulunamadı.");
        } else {
            for (Fixture f : teamFixtures) {
                // Modelindeki toString() metodu sayesinde tüm detaylar güzelce basılır
                System.out.println(f);
            }
        }

        // 2. TEST: istenilen Haftanın Maçları
        System.out.println("\n********** 7. HAFTANIN TÜM MAÇLARI **********");
        List<Fixture> weekFixtures = fixtureService.getFixturesForWeek(1);

        if (weekFixtures.isEmpty()) {
            System.out.println("7. haftaya ait maç bulunamadı.");
        } else {
            for (Fixture f : weekFixtures) {
                System.out.println(f);
            }
        }

        System.out.println("\nTest işlemi tamamlandı.");
    }
}