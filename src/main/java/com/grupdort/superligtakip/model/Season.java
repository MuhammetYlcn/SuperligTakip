package com.grupdort.superligtakip.model;

public class Season {
    private int season_ID;
    private String season_name;
    private int is_active;

    public Season(){
        this.is_active=0;
    }
    public Season(int season_ID,String season_name,int is_active){
        this.season_ID=season_ID;
        this.season_name=season_name;
        this.is_active=is_active;
    }
    public int getSeason_ID() {
        return season_ID;
    }
    public void setSeason_ID(int season_ID) {
        this.season_ID = season_ID;
    }
    public String getSeason_name() {
        return season_name;
    }
    public void setSeason_name(String season_name) {
        this.season_name = season_name;
    }
    public int getIs_active() {
        return is_active;
    }
    public void setIs_active(int is_active) {
        this.is_active = is_active;
    }
}
