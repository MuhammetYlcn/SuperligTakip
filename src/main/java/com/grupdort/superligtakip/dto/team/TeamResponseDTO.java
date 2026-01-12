package com.grupdort.superligtakip.dto.team;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TeamResponseDTO {
    @JsonProperty("success")
    private int success;

    @JsonProperty("result")
    private List<TeamDTO> result;

    // Getter ve Setter'lar...
    public int getSuccess() { return success; }
    public List<TeamDTO> getResult() { return result; }
}