package com.grupdort.superligtakip.api;

import com.grupdort.superligtakip.dto.standing.StandingResponseDTO;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class StandingAPI {
    private static final String API_KEY = "apikey";
    // leagueId=322 Süper Lig'i temsil eder
    private static final String URL = "https://apiv2.allsportsapi.com/football/?met=Standings&leagueId=322&APIkey=" + API_KEY;

    public StandingResponseDTO fetchStandings() throws Exception {
        // 1. HTTP Client ve Request yapılandırması
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .GET()
                .build();

        // 2. API'den cevabı String olarak al
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Hata ayıklama için konsola ham veriyi basabilirsin (İsteğe bağlı)
        // System.out.println("API Ham Veri: " + response.body());

        if (response.statusCode() == 200) {
            ObjectMapper mapper = new ObjectMapper();

            // Çalışan kodundaki kritik ayar: Bilinmeyen alanları görmezden gel
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            // 3. JSON'ı DTO'ya dönüştür
            String body = response.body();

            // Eğer API 'result' kısmını boş dizi [] olarak dönüyorsa MismatchedInputException almamak için:
            if (body != null && body.contains("\"result\":[]")) {
                System.out.println("Bilgi: API şu an boş bir sonuç listesi döndürdü.");
                return null;
            }

            return mapper.readValue(body, StandingResponseDTO.class);
        } else {
            System.err.println("API Hatası! Durum Kodu: " + response.statusCode());
            return null;
        }
    }
}