package com.grupdort.superligtakip.dto.fixture;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FixtureDTO {
    @JsonProperty("event_key")
    private String eventKey;

    @JsonProperty("event_date")
    private String eventDate; // Örn: 2025-08-15

    @JsonProperty("event_time")
    private String eventTime; // Örn: 21:00

    @JsonProperty("event_home_team")
    private String homeTeamName;

    @JsonProperty("home_team_key")
    private String homeTeamKey;

    @JsonProperty("event_away_team")
    private String awayTeamName;

    @JsonProperty("away_team_key")
    private String awayTeamKey;

    @JsonProperty("event_final_result")
    private String finalResult; // Örn: 2 - 1

    @JsonProperty("event_status")
    private String eventStatus; // Maçın durumu: "Finished", "Time Postponed" veya "21:00"

    @JsonProperty("event_live")
    private String eventLive; // "1" ise maç şu an canlı, "0" ise değil

    @JsonProperty("league_round")
    private String leagueRound; // Kaçıncı hafta olduğu (Örn: Round 5)

    @JsonProperty("home_team_logo")
    private String homeTeamLogo;

    @JsonProperty("away_team_logo")
    private String awayTeamLogo;

    // --- GETTER VE SETTERLAR ---
    public String getEventKey() { return eventKey; }
    public void setEventKey(String eventKey) { this.eventKey = eventKey; }
    public String getEventDate() { return eventDate; }
    public void setEventDate(String eventDate) { this.eventDate = eventDate; }
    public String getEventTime() { return eventTime; }
    public void setEventTime(String eventTime) { this.eventTime = eventTime; }
    public String getHomeTeamName() { return homeTeamName; }
    public void setHomeTeamName(String homeTeamName) { this.homeTeamName = homeTeamName; }
    public String getHomeTeamKey() { return homeTeamKey; }
    public void setHomeTeamKey(String homeTeamKey) { this.homeTeamKey = homeTeamKey; }
    public String getAwayTeamName() { return awayTeamName; }
    public void setAwayTeamName(String awayTeamName) { this.awayTeamName = awayTeamName; }
    public String getAwayTeamKey() { return awayTeamKey; }
    public void setAwayTeamKey(String awayTeamKey) { this.awayTeamKey = awayTeamKey; }
    public String getFinalResult() { return finalResult; }
    public void setFinalResult(String finalResult) { this.finalResult = finalResult; }
    public String getEventStatus() { return eventStatus; }
    public void setEventStatus(String eventStatus) { this.eventStatus = eventStatus; }
    public String getEventLive() { return eventLive; }
    public void setEventLive(String eventLive) { this.eventLive = eventLive; }
    public String getLeagueRound() { return leagueRound; }
    public void setLeagueRound(String leagueRound) { this.leagueRound = leagueRound; }
    public String getHomeTeamLogo() { return homeTeamLogo; }
    public void setHomeTeamLogo(String homeTeamLogo) { this.homeTeamLogo = homeTeamLogo; }
    public String getAwayTeamLogo() { return awayTeamLogo; }
    public void setAwayTeamLogo(String awayTeamLogo) { this.awayTeamLogo = awayTeamLogo; }

    /**
     * Yardımcı Metot: Maçın skoru yoksa veya oynanmamışsa
     * ekranda çirkin bir görüntü olmaması için kontrol yapar.
     */
    public String getDisplayResult() {
        if (finalResult != null && !finalResult.isEmpty() && !finalResult.equals("-")) {
            return finalResult;
        }
        return "v"; // Maç oynanmadıysa skor yerine "v" veya "-" gösteririz.
    }
}