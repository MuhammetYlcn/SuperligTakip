package com.grupdort.superligtakip.dto.statistic;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MatchLineupDTO {
    @JsonProperty("player_key")
    private int playerId;

    private int teamId; // Manuel set edilecek

    private int isSubstitute; // 0: İlk 11, 1: Yedek

    @JsonProperty("player_position")
    private int position;

    @JsonProperty("player_number")
    private int jerseyNumber;

    public MatchLineupDTO() {}

    // Getters and Setters
    public int getPlayerId() { return playerId; }
    public void setPlayerId(int playerId) { this.playerId = playerId; }

    public int getTeamId() { return teamId; }
    public void setTeamId(int teamId) { this.teamId = teamId; }

    public int getIsSubstitute() { return isSubstitute; }
    public void setIsSubstitute(int isSubstitute) { this.isSubstitute = isSubstitute; }

    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }

    public int getJerseyNumber() { return jerseyNumber; }
    public void setJerseyNumber(int jerseyNumber) { this.jerseyNumber = jerseyNumber; }
}