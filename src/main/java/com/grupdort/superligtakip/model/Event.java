package com.grupdort.superligtakip.model;

public class Event {
    private int event_ID;
    private Fixture fixture;
    private Team team;
    private Player player;
    private Player relatedPlayer;
    private String score_time;
    private String score;
    private String time;
    private String type;
    private String info;

    public Event() {
        this.score_time = "";
        this.score = "";
        this.time = "";
        this.type = "";
        this.info = "";
    }

    public Event(int event_ID, Fixture fixture, Team team, Player player, Player relatedPlayer, String score_time, String score, String time, String type, String info) {
        this.event_ID = event_ID;
        this.fixture = fixture;
        this.team = team;
        this.player = player;
        this.relatedPlayer = relatedPlayer;
        this.score_time = score_time;
        this.score = score;
        this.time = time;
        this.type = type;
        this.info = info;
    }

    public int getEvent_ID() { return event_ID; }
    public void setEvent_ID(int event_ID) { this.event_ID = event_ID; }
    public Fixture getFixture() { return fixture; }
    public void setFixture(Fixture fixture) { this.fixture = fixture; }
    public Team getTeam() { return team; }
    public void setTeam(Team team) { this.team = team; }
    public Player getPlayer() { return player; }
    public void setPlayer(Player player) { this.player = player; }
    public Player getRelatedPlayer() { return relatedPlayer; }
    public void setRelatedPlayer(Player relatedPlayer) { this.relatedPlayer = relatedPlayer; }
    public String getScore_time() { return score_time; }
    public void setScore_time(String score_time) { this.score_time = score_time; }
    public String getScore() { return score; }
    public void setScore(String score) { this.score = score; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getInfo() { return info; }
    public void setInfo(String info) { this.info = info; }

    @Override
    public String toString() {
        return "Event Details [" + event_ID + "]:\n" +
                "  Fixture: " + (fixture != null ? fixture.getFixture_ID() : "N/A") + "\n" +
                "  Time: " + time + " (" + score_time + ")\n" +
                "  Type: " + type.toUpperCase() + (info.isEmpty() ? "" : " - " + info) + "\n" +
                "  Team: " + (team != null ? team.getTeam_name() : "N/A") + "\n" +
                "  Player: " + (player != null ? player.getPlayer_name() : "N/A") + "\n" +
                "  Related: " + (relatedPlayer != null ? relatedPlayer.getPlayer_name() : "None") + "\n" +
                "  Score After Event: " + score;
    }
}