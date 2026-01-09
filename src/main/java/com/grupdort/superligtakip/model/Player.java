package com.grupdort.superligtakip.model;

public class Player {
    private long player_ID;
    private Team team;
    private String player_name;
    private String position;
    private int age;
    private int appearances;
    private int goal;
    private int assist;
    private int yellow_card;
    private int red_card;
    private double total_rating;

    public Player(){
        this.goal=0;
        this.assist=0;
        this.yellow_card=0;
        this.red_card=0;
        this.appearances=0;
        this.total_rating=0;
    }
    public Player(long player_ID,Team team,String player_name,String position,int age,int appearances,int goal,int assist,int yellow_card,int red_card,double total_rating){
        this.player_ID=player_ID;
        this.team=team;
        this.player_name=player_name;
        this.position=position;
        this.age=age;
        this.appearances=appearances;
        this.goal=goal;
        this.assist=assist;
        this.yellow_card=yellow_card;
        this.red_card=red_card;
        this.total_rating=total_rating;
    }
    public long getPlayer_ID() {
        return player_ID;
    }
    public void setPlayer_ID(long player_ID) {
        this.player_ID = player_ID;
    }
    public Team getTeam() {
        return team;
    }
    public void setTeam(Team team) {
        this.team = team;
    }
    public String getPlayer_name() {
        return player_name;
    }
    public void setPlayer_name(String player_name) {
        this.player_name = player_name;
    }
    public String getPosition() {
        return position;
    }
    public void setPosition(String position) {
        this.position = position;
    }
    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }
    public int getAppearances() {
        return appearances;
    }
    public void setAppearances(int appearances) {
        this.appearances = appearances;
    }
    public int getGoal() {
        return goal;
    }
    public void setGoal(int goal) {
        this.goal = goal;
    }
    public int getAssist() {
        return assist;
    }
    public void setAssist(int assist) {
        this.assist = assist;
    }
    public int getYellow_card() {
        return yellow_card;
    }
    public void setYellow_card(int yellow_card) {
        this.yellow_card = yellow_card;
    }
    public int getRed_card() {
        return red_card;
    }
    public void setRed_card(int red_card) {
        this.red_card = red_card;
    }
    public double getTotal_rating() {
        return total_rating;
    }
    public void setTotal_rating(double total_rating) {
        this.total_rating = total_rating;
    }
    @Override
    public String toString() {
        String teamName = (team != null) ? team.getTeam_name() : "Takımsız";
        return String.format(
                "ID: %3d | %20s | %15s | %3s | Yaş: %2d | Maç: %2d | G: %2d | A: %2d | SK: %d | KK: %d | Genel rating: %2.2f",
                player_ID,             // Oyuncu ID
                player_name,    // Oyuncu Adı
                teamName,       // Takım Adı
                position,       // Mevki
                age,            // Yaş
                appearances,    // Oynadığı Maçlar
                goal,           // Gol Sayısı
                assist,         // Asist Sayısı
                yellow_card,    // Sarı Kart
                red_card,       // Kırmızı Kart
                total_rating    // ortalama puan
        );
    }
}
