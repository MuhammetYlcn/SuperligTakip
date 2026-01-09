package com.grupdort.superligtakip.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Fixture {
    private int fixture_ID;
    private Team home_team;
    private Team away_team;
    private Week week;
    private LocalDate match_date;
    private LocalTime match_time;
    private String status;
    private int home_score;
    private int away_score;
    private int event_live;
    private String final_result;

    public Fixture() {
        this.home_score = 0;
        this.away_score = 0;
        this.status = "oynanmadi";
        this.event_live = 0;
    }

    public Fixture(int fixture_ID, Team home_team, Team away_team, Week week, LocalDate match_date, LocalTime match_time, String status, int home_score, int away_score, int event_live,String final_result) {
        this.fixture_ID = fixture_ID;
        this.home_team = home_team;
        this.away_team = away_team;
        this.week = week;
        this.match_date = match_date;
        this.match_time = match_time;
        this.status = status;
        this.home_score = home_score;
        this.away_score = away_score;
        this.event_live = event_live;
        this.final_result=final_result;
    }

    public int getFixture_ID() {
        return fixture_ID;
    }

    public void setFixture_ID(int fixture_ID) {
        this.fixture_ID = fixture_ID;
    }

    public Team getHome_team() {
        return home_team;
    }

    public void setHome_team(Team home_team) {
        this.home_team = home_team;
    }

    public Team getAway_team() {
        return away_team;
    }

    public void setAway_team(Team away_team) {
        this.away_team = away_team;
    }

    public Week getWeek() {
        return week;
    }

    public void setWeek(Week week) {
        this.week = week;
    }

    public LocalDate getMatch_date() {
        return match_date;
    }

    public void setMatch_date(LocalDate match_date) {
        this.match_date = match_date;
    }

    public LocalTime getMatch_time() {
        return match_time;
    }

    public void setMatch_time(LocalTime match_time) {
        this.match_time = match_time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getHome_score() {
        return home_score;
    }

    public void setHome_score(int home_score) {
        this.home_score = home_score;
    }

    public int getAway_score() {
        return away_score;
    }

    public void setAway_score(int away_score) {
        this.away_score = away_score;
    }

    public int getEvent_live() {
        return event_live;
    }

    public void setEvent_live(int event_live) {
        this.event_live = event_live;
    }

    public String getFinal_result() {
        return final_result;
    }

    public void setFinal_result(String final_result) {
        this.final_result = final_result;
    }

    @Override
    public String toString() {
        String weekName = (week != null) ? week.getWeek_name() : "Bilinmiyor";
        String homeName = (home_team != null) ? home_team.getTeam_name() : "Ev Sahibi Yok";
        String awayName = (away_team != null) ? away_team.getTeam_name() : "Deplasman Yok";
        String formattedDate = "Belirsiz";

        if (match_date != null) {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            formattedDate = match_date.format(dateFormatter);
        }

        // "Canlı: %d" kısmı formatın sonuna eklenmiştir.
        return String.format("ID: %-5d | %-10s | Tarih: %-10s %-5s | %-15s %d-%d %-15s | Durum: %-10s | Canlı: %d",
                fixture_ID,
                weekName,
                formattedDate,
                match_time,
                homeName,
                home_score,
                away_score,
                awayName,
                status,
                event_live);
    }
}