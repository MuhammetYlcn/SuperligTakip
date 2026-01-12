package com.grupdort.superligtakip.model;
public class Team {
    private int team_ID;
    private String team_name;
    private String logo;
    private String manager_name;

    public Team(){
    }
    public Team(int team_ID,String team_name,String logo,String manager_name){
        this.team_ID=team_ID;
        this.team_name=team_name;
        this.logo=logo;
        this.manager_name=manager_name;
    }

    public int getTeam_ID() {
        return team_ID;
    }
    public void setTeam_ID(int team_ID) {
        this.team_ID = team_ID;
    }
    public String getTeam_name() {
        // İsim boşsa boş dön
        if (team_name == null) return "";

        // --- MERKEZİ İSİM DÜZELTME ALANI ---

        // 1. Başakşehir Kontrolü
        if (team_name.contains("Başakşehir") || team_name.contains("Basaksehir")) {
            return "Başakşehir";
        }

        // 2. Karagümrük Kontrolü
        if (team_name.contains("Karagümrük") || team_name.contains("Karagumruk")) {
            return "Karagümrük";
        }

        // 3. Gaziantep FK (Bazen uzun geliyor)
        if (team_name.contains("Gaziantep")) {
            return "Gaziantep FK";
        }

        // Başka özel durum yoksa orjinalini döndür
        return team_name;
    }
    public void setTeam_name(String team_name) {
        this.team_name = team_name;
    }
    public String getLogo() {
        return logo;
    }
    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getManager_name() {
        return manager_name;
    }

    public void setManager_name(String manager_name) {
        this.manager_name = manager_name;
    }

    @Override
    public String toString() {
        return String.format("ID: %-6d | manager: %-5s | Takım: %-25s",team_ID,manager_name,team_name);
    }
}