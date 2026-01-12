package com.grupdort.superligtakip.dto.team;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayerDTO {
    @JsonProperty("player_key")
    private long playerKey;

    private int teamKey; // Service katmanında TeamDTO'dan buraya aktaracağız

    @JsonProperty("player_name")
    private String playerName;

    @JsonProperty("player_number")
    private String playerNumber;

    @JsonProperty("player_type") // Mevki (Forwards, Midfielders vb.)
    private String playerType;

    @JsonProperty("player_age")
    private String playerAge;

    @JsonProperty("player_match_played")
    private String matchPlayed;

    @JsonProperty("player_goals")
    private String goals;

    @JsonProperty("player_assists")
    private String assists;

    @JsonProperty("player_yellow_cards")
    private String yellowCards;

    @JsonProperty("player_red_cards")
    private String redCards;

    @JsonProperty("player_rating")
    private String rating;

    // Getter ve Setter'lar
    public long getPlayerKey() { return playerKey; }
    public void setPlayerKey(long playerKey) { this.playerKey = playerKey; }

    public int getTeamKey() { return teamKey; }
    public void setTeamKey(int teamKey) { this.teamKey = teamKey; }

    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }

    public String getPlayerNumber() { return playerNumber; }
    public void setPlayerNumber(String playerNumber) { this.playerNumber = playerNumber; }

    public String getPlayerType() { return playerType; }
    public void setPlayerType(String playerType) { this.playerType = playerType; }

    public String getPlayerAge() { return playerAge; }
    public void setPlayerAge(String playerAge) { this.playerAge = playerAge; }

    public String getMatchPlayed() { return matchPlayed; }
    public void setMatchPlayed(String matchPlayed) { this.matchPlayed = matchPlayed; }

    public String getGoals() { return goals; }
    public void setGoals(String goals) { this.goals = goals; }

    public String getAssists() { return assists; }
    public void setAssists(String assists) { this.assists = assists; }

    public String getYellowCards() { return yellowCards; }
    public void setYellowCards(String yellowCards) { this.yellowCards = yellowCards; }

    public String getRedCards() { return redCards; }
    public void setRedCards(String redCards) { this.redCards = redCards; }

    public String getRating() { return rating; }
    public void setRating(String rating) { this.rating = rating; }
}