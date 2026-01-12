package com.grupdort.superligtakip.dto.team;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class  TeamDTO {
    @JsonProperty("team_key")
    private int teamKey;

    @JsonProperty("team_name")
    private String teamName;

    @JsonProperty("team_logo")
    private String teamLogo;

    private String coachName; // Sadece isim tutacağız

    @JsonProperty("players")
    private List<PlayerDTO> players;

    // API'deki "coaches" dizisinden ilk elemanın ismini çekiyoruz
    @JsonSetter("coaches")
    public void setCoachNameFromList(JsonNode coachesNode) {
        if (coachesNode != null && coachesNode.isArray() && coachesNode.size() > 0) {
            this.coachName = coachesNode.get(0).get("coach_name").asText();
        } else {
            this.coachName = "Bilinmiyor";
        }
    }

    // Getter ve Setter'lar
    public int getTeamKey() { return teamKey; }
    public String getTeamName() { return teamName; }
    public String getTeamLogo() { return teamLogo; }
    public String getCoachName() { return coachName; }
    public List<PlayerDTO> getPlayers() { return players; }
}
