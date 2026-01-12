package com.grupdort.superligtakip.api;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class MatchDetailAPI {
    private static final String API_KEY = "apikey";
    private final ObjectMapper mapper;
    private final HttpClient httpClient;

    public MatchDetailAPI() {
        this.httpClient = HttpClient.newHttpClient();
        this.mapper = new ObjectMapper();
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public JsonNode fetchSingleMatchDetails(int matchId) throws Exception {
        String url = "https://apiv2.allsportsapi.com/football/?met=Fixtures"
                + "&matchId=" + matchId
                + "&APIkey=" + API_KEY;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JsonNode root = mapper.readTree(response.body());
            JsonNode result = root.get("result");
            // API tek maç olsa bile dizi içinde döner, ilk elemanı alıyoruz
            return (result != null && result.isArray()) ? result.get(0) : null;
        }
        return null;
    }

    /**
     * Belirtilen tarih aralığındaki tüm Süper Lig maçlarını
     * İstatistik, Olay ve Kadro detaylarıyla birlikte tek requestte çeker.
     */
    public JsonNode fetchAllMatchesWithDetails(String fromDate, String toDate) throws Exception {
        // Senin paylaştığın toplu veri linki (leagueId=322 Süper Lig)
        String url = "https://apiv2.allsportsapi.com/football/?met=Fixtures"
                + "&leagueId=322"
                + "&from=" + fromDate
                + "&to=" + toDate
                + "&APIkey=" + API_KEY;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // MatchDetailAPI içindeki ilgili kısmı şöyle güncelle:
        if (response.statusCode() == 200) {
            System.out.println("API RAW RESPONSE: " + response.body()); // GELEN VERİYİ KONSOLA BAS
            JsonNode root = mapper.readTree(response.body());
            JsonNode result = root.get("result");

            if (result != null && result.isArray() && result.size() > 0) {
                return result.get(0);
            } else {
                System.out.println("API 'result' boş döndü!");
                return null;
            }
        } else {
            System.err.println("API Hatası (Toplu Çekim): " + response.statusCode());
            return null;
        }
    }
}