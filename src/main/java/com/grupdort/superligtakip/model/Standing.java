package com.grupdort.superligtakip.model;

public class Standing {
    private int standing_ID;
    private Team team;
    private Season season;
    private int rank;
    private int won;
    private int drawn;
    private int lost;
    private int points;
    private int played;
    private int goal_for;
    private int goal_against;
    private int goal_diff;

    public Standing(){
        this.won=0;
        this.drawn=0;
        this.lost=0;
        this.points=0;
        this.played=0;
        this.goal_for=0;
        this.goal_against=0;
        this.goal_diff=0;
    }
    public Standing(int standing_ID,Team team,Season season,int rank,int won,int drawn,int lost,int points,int played,int goal_for,int goal_against){
        this.standing_ID=standing_ID;
        this.team=team;
        this.season=season;
        this.rank=rank;
        this.won=won;
        this.drawn=drawn;
        this.lost=lost;
        this.points=points;
        this.played=played;
        this.goal_for=goal_for;
        this.goal_against=goal_against;
    }
    public int getStanding_ID() {
        return standing_ID;
    }
    public void setStanding_ID(int standing_ID) {
        this.standing_ID = standing_ID;
    }
    public Team getTeam() {
        return team;
    }
    public void setTeam(Team team) {
        this.team = team;
    }
    public Season getSeason() {
        return season;
    }
    public void setSeason(Season season) {
        this.season = season;
    }
    public int getRank() {
        return rank;
    }
    public void setRank(int rank) {
        this.rank = rank;
    }
    public int getWon() {
        return won;
    }
    public void setWon(int won) {
        this.won = won;
    }
    public int getDrawn() {
        return drawn;
    }
    public void setDrawn(int drawn) {
        this.drawn = drawn;
    }
    public int getLost() {
        return lost;
    }
    public void setLost(int lost) {
        this.lost = lost;
    }
    public int getPoints() {
        return points;
    }
    public void setPoints(int points) {
        this.points = points;
    }
    public int getPlayed() {
        return played;
    }
    public void setPlayed(int played) {
        this.played = played;
    }
    public int getGoal_for() {
        return goal_for;
    }
    public void setGoal_for(int goal_for) {
        this.goal_for = goal_for;
        hesaplaGoal_diff();
    }
    public int getGoal_against() {
        return goal_against;
    }
    public void setGoal_against(int goal_against) {
        this.goal_against = goal_against;
        hesaplaGoal_diff();
    }
    public int getGoal_diff() {
        return goal_diff;
    }
    public void hesaplaGoal_diff() {
        this.goal_diff = this.goal_for-this.goal_against;
    }
    @Override
    public String toString() {
        return String.format("ID: %-5d | Sıra: %-2d | %-15s | O:%-2d G:%-2d B:%-2d M:%-2d | AG:%-2d YG:%-2d Av:%-3d | Puan: %-2d", 
            standing_ID,rank,team.getTeam_name(),played,won,drawn,lost,goal_for,goal_against,goal_diff,points);
        }
}