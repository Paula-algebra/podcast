package org.podcast_fx.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.podcast_fx.models.Login;
import org.podcast_fx.models.TokenResponse;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.podcast_fx.models.Episode;


public class ApiService {

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();
    private String accessToken;

    public TokenResponse login(String username, String password) throws Exception {
        Login login = new Login(username, password);
        String json = mapper.writeValueAsString(login);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/auth/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        TokenResponse tokenResponse = mapper.readValue(response.body(), TokenResponse.class);
        this.accessToken = tokenResponse.getAccessToken();
        System.out.println("LOGIN TOKEN = " + accessToken);
        return tokenResponse;
    }

    public String getEpisodes() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/episodes"))
                .header("Authorization", "Bearer " + accessToken)
                .GET()
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );
        return response.body();
    }
    public Episode[] getEpisodeList() throws Exception {
        String json = getEpisodes();
        return mapper.readValue(json, Episode[].class);
    }

    public void createEpisode(Episode episode) throws Exception {

        String json = mapper.writeValueAsString(episode);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/episodes"))
                .header("accept", "*/*")
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new RuntimeException("POST failed with status " + response.statusCode() + ": " + response.body());
        }
    }

    public String getEpisodeById(Long id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/episodes/" + id))
                .header("Authorization", "Bearer " + accessToken)
                .GET()
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        return response.body();
    }
    public Episode getEpisodeByIdParsed(Long id) throws Exception {
        String json = getEpisodeById(id);
        return mapper.readValue(json, Episode.class);
    }

    public void deleteEpisode(Long id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/episodes/" + id))
                .header("Authorization", "Bearer " + accessToken)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new RuntimeException("DELETE failed with status " + response.statusCode() + ": " + response.body());
        }
    }

    public void updateEpisode(Long id, Episode episode) throws Exception {
        String json = mapper.writeValueAsString(episode);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/episodes/" + id))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new RuntimeException("PUT failed with status " + response.statusCode() + ": " + response.body());
        }
    }

    public String backupDatabase() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/database/backup"))
                .header("Authorization", "Bearer " + accessToken)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new RuntimeException("Backup failed with status " + response.statusCode() + ": " + response.body());
        }

        return response.body();
    }

    public String restoreDatabase(String path) throws Exception {
        String json = "{\"path\":\"" + path.replace("\\", "\\\\") + "\"}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/database/restore"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new RuntimeException("Restore failed with status " + response.statusCode() + ": " + response.body());
        }

        return response.body();
    }



}