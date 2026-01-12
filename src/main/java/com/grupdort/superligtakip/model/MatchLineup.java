package com.grupdort.superligtakip.model;

public class MatchLineup {
    private Fixture fixture;
    private Player player;
    private Team team;
    private int is_substitute;
    private int number;
    private int position;

    public MatchLineup() {
        this.is_substitute = 0;
        this.number = 0;
        this.position = 0;
    }

    // Full Parametreli Constructor
    public MatchLineup(Fixture fixture, Player player, Team team, int is_substitute, int number, int position) {
        this.fixture = fixture;
        this.player = player;
        this.team = team;
        this.is_substitute = is_substitute;
        this.number = number;
        this.position = position;
    }

    // Getters and Setters
    public Fixture getFixture() { return fixture; }
    public void setFixture(Fixture fixture) { this.fixture = fixture; }

    public Player getPlayer() { return player; }
    public void setPlayer(Player player) { this.player = player; }

    public Team getTeam() { return team; }
    public void setTeam(Team team) { this.team = team; }

    public int getIs_substitute() { return is_substitute; }
    public void setIs_substitute(int is_substitute) { this.is_substitute = is_substitute; }

    public int getNumber() { return number; }
    public void setNumber(int number) { this.number = number; }

    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }

    @Override
    public String toString() {
        return "MatchLineup Detail:\n" +
                "  Fixture ID: " + (fixture != null ? fixture.getFixture_ID() : "N/A") + "\n" +
                "  Team: " + (team != null ? team.getTeam_name() : "N/A") + "\n" +
                "  Player: " + (player != null ? player.getPlayer_name() : "N/A") + "\n" +
                "  Number: " + number + "\n" +
                "  Position: " + position + "\n" +
                "  Status: " + (is_substitute == 0 ? "STARTING 11" : "SUBSTITUTE") + "\n" +
                "--------------------------------------------------";
    }
}