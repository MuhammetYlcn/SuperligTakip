package com.grupdort.superligtakip.model;


public class Week {
    private int week_ID;
    private String week_name;
    private Season season;
    private int status;

    public  Week(){
        this.status=0;
    }

    public  Week(int week_ID,String week_name,Season season,int status){
        this.week_ID=week_ID;
        this.week_name=week_name;
        this.season=season;
        this.status=status;
    }

    public int getWeek_ID() {
        return week_ID;
    }
    public void setWeek_ID(int week_ID) {
        this.week_ID = week_ID;
    }
    public String getWeek_name() {
        return week_name;
    }
    public void setWeek_name(String week_name) {
        this.week_name = week_name;
    }
    public Season getSeason() {
        return season;
    }
    public void setSeason(Season season) {
        this.season = season;
    }
    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
    @Override
    public String toString() {
        String seasonInfo = (season != null) ? season.getSeason_name() : "Bilinmeyen Sezon";
        String statusText="Oynanmadı";
        if(status==1){
            statusText="Devam ediyor";
        }
        else if (status==2){
            statusText="Tamamlandı";
        }
        return String.format(
                "ID: %-3d | %-15s | Sezon: %-15s | Aralık: %s - %s | Durum: %s",
                week_ID,
                week_name,
                seasonInfo,
                statusText
        );
    }
}
