package com.grupdort.superligtakip.dto.standing;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StandingResponseDTO {
    private int success;
    private StandingResultDTO result; // Değişen kısım burası (Artık Liste değil, Obje)

    public int getSuccess() { return success; }
    public void setSuccess(int success) { this.success = success; }

    public StandingResultDTO getResult() { return result; }
    public void setResult(StandingResultDTO result) { this.result = result; }
}