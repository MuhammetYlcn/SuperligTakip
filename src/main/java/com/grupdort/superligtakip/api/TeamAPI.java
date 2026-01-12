package com.grupdort.superligtakip.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grupdort.superligtakip.dto.team.TeamResponseDTO;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TeamAPI {
    private static final String API_KEY = "apikey";
    private final HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .build();
    private final ObjectMapper mapper = new ObjectMapper();

    public TeamResponseDTO fetchAllTeams() {
        // Süper Lig ID: 322. Tüm takımları ve oyuncuları tek seferde getirir.
        String url = "https://apiv2.allsportsapi.com/football/?met=Teams&leagueId=322&APIkey=" + API_KEY;

        try {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return mapper.readValue(response.body(), TeamResponseDTO.class);
        } catch (Exception e) {
            System.err.println("API'den takımlar çekilirken hata oluştu: " + e.getMessage());
            return null;
        }
    }
}