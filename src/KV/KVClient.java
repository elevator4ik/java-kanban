package KV;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import modul.LocalDateTypeAdapter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

public class KVClient {

    private final String apiToken;
    private final HttpClient client;
    private final URI url;
    Gson gson;

    public KVClient() {

        url = URI.create("http://localhost:8078");
        client = HttpClient.newHttpClient();
        apiToken = registerOnServer();
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTypeAdapter())
                .create();
    }

    public void save(String key, String json) {//отправляем на KVServer json из HttpTaskManager
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create(url + "/save/" + key + "?API_TOKEN=" + apiToken))
                .header("Accept", "application/json")
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                System.out.println("Данные отправлены на сервер.");
            } else {
                System.out.println("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса возникла ошибка.");
        }
    }

    public String load(String key) {//получаем от KVServer json по ключу, ранее полученный из HttpTaskManager

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url + "/load/" + key + "?API_TOKEN=" + apiToken))
                .header("Accept", "application/json")
                .build();
        return gettingResponse(request);
    }

    public String registerOnServer() {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/register"))
                .header("Accept", "application/json")
                .GET()
                .build();
        return gettingResponse(request);
    }

    private String gettingResponse(HttpRequest request) {
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return response.body();
            } else {
                System.out.println("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
                return null;
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса возникла ошибка.");
            return null;
        }
    }
}
