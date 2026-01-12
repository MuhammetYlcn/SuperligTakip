package com.grupdort.superligtakip.dto.fixture;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FixtureResponseDTO {
    private int success;
    private List<FixtureDTO> result;

    public int getSuccess() { return success; }
    public void setSuccess(int success) { this.success = success; }

    public List<FixtureDTO> getResult() { return result; }
    public void setResult(List<FixtureDTO> result) { this.result = result; }
}