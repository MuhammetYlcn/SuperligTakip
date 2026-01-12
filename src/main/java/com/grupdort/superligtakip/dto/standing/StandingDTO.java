package com.grupdort.superligtakip.dto.standing;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StandingDTO {
    private String team_key;          // Takım Benzersiz ID (Eklendi)
    private String standing_place;    // Sıra
    private String standing_team;     // Takım Adı
    private String team_logo;         // Logo URL
    private String standing_P;        // Oynanan Maç
    private String standing_W;        // Galibiyet
    private String standing_D;        // Beraberlik
    private String standing_L;        // Mağlubiyet
    private String standing_F;        // Atılan Gol
    private String standing_A;        // Yenilen Gol
    private String standing_GD;       // Averaj
    private String standing_PTS;      // Puan

    // --- Yeni Eklenen Getter ve Setter ---
    public String getTeam_key() {
        return team_key;
    }

    public void setTeam_key(String team_key) {
        this.team_key = team_key;
    }

    // --- Mevcut Metotlar ---
    public String getStanding_place() {
        return standing_place;
    }

    public void setStanding_place(String standing_place) {
        this.standing_place = standing_place;
    }

    public String getStanding_team() {
        return standing_team;
    }

    public void setStanding_team(String standing_team) {
        this.standing_team = standing_team;
    }

    public String getTeam_logo() {
        return team_logo;
    }

    public void setTeam_logo(String team_logo) {
        this.team_logo = team_logo;
    }

    public String getStanding_P() {
        return standing_P;
    }

    public void setStanding_P(String standing_P) {
        this.standing_P = standing_P;
    }

    public String getStanding_W() {
        return standing_W;
    }

    public void setStanding_W(String standing_W) {
        this.standing_W = standing_W;
    }

    public String getStanding_D() {
        return standing_D;
    }

    public void setStanding_D(String standing_D) {
        this.standing_D = standing_D;
    }

    public String getStanding_L() {
        return standing_L;
    }

    public void setStanding_L(String standing_L) {
        this.standing_L = standing_L;
    }

    public String getStanding_F() {
        return standing_F;
    }

    public void setStanding_F(String standing_F) {
        this.standing_F = standing_F;
    }

    public String getStanding_A() {
        return standing_A;
    }

    public void setStanding_A(String standing_A) {
        this.standing_A = standing_A;
    }

    public String getStanding_GD() {
        return standing_GD;
    }

    public void setStanding_GD(String standing_GD) {
        this.standing_GD = standing_GD;
    }

    public String getStanding_PTS() {
        return standing_PTS;
    }

    public void setStanding_PTS(String standing_PTS) {
        this.standing_PTS = standing_PTS;
    }

    @Override
    public String toString() {
        return "StandingDTO{" +
                "team_key='" + team_key + '\'' + // toString'e eklendi
                ", rank='" + standing_place + '\'' +
                ", team='" + standing_team + '\'' +
                ", points='" + standing_PTS + '\'' +
                '}';
    }
}