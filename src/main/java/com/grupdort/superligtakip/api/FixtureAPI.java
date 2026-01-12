package com.grupdort.superligtakip.api;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grupdort.superligtakip.dto.fixture.FixtureResponseDTO;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class FixtureAPI {
    private static final String API_KEY = "apikey";
    private static final String BASE_URL = "https://apiv2.allsportsapi.com/football/";

    public FixtureResponseDTO fetchFixtures(String fromDate, String toDate) {
        try {
            // leagueId=322 (Süper Lig)
            String url = BASE_URL + "?met=Fixtures&leagueId=322&from=" + fromDate + "&to=" + toDate + "&APIkey=" + API_KEY;

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                ObjectMapper mapper = new ObjectMapper();
                // DTO'da olmayan bir alan gelirse hataya düşmemesi için:
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                return mapper.readValue(response.body(), FixtureResponseDTO.class);
            }
        } catch (Exception e) {
            System.err.println("API'den fikstür çekilirken hata oluştu: " + e.getMessage());
        }
        return null;
    }
}