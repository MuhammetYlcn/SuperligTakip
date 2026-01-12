package com.grupdort.superligtakip.dto.standing;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StandingResultDTO {
    // API "total" adında bir liste döndürüyor, asıl puan durumu burada
    private List<StandingDTO> total;

    public List<StandingDTO> getTotal() {
        return total;
    }

    public void setTotal(List<StandingDTO> total) {
        this.total = total;
    }
}