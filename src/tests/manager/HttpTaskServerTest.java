package tests.manager;

import KV.HttpTaskServer;
import KV.KVServer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import manager.HttpTaskManager;
import manager.Managers;
import modul.Epic;
import modul.LocalDateTypeAdapter;
import modul.SubTask;
import modul.Task;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

import static modul.Status.IN_PROGRESS;
import static modul.Status.NEW;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HttpTaskServerTest {

    HttpClient client;
    Gson gson;
    KVServer server;
    URI url;
    HttpRequest request;
    HttpTaskManager manager;
    HttpTaskServer taskServer;
    final Task task = new Task("Test Task", "Test save description", NEW, 10,
            LocalDateTime.of(2023, 1, 1, 11, 0));//id 0
    final Task newTask = new Task("Test Second Task", "Test save description", IN_PROGRESS,
            10, LocalDateTime.of(2023, 1, 1, 11, 11));//id 1
    final Epic epic = new Epic("Test Epic", "Test save description", NEW);//id 2
    final Epic newEpic = new Epic("Test Second Epic", "Test save description", NEW);//id 3
    final SubTask subTask = new SubTask("Test SubTask", "Test save description", NEW, 10,
            LocalDateTime.of(2023, 1, 1, 11, 22), 2);//id 4
    final SubTask newSubTask = new SubTask("Test Second SubTask", "Test save description", NEW, 10,
            LocalDateTime.of(2023, 1, 1, 11, 33), 3);//id 5
    final Task thirdTask = new Task("Test Third Task", "Test save description", IN_PROGRESS,
            10, LocalDateTime.of(2023, 1, 1, 11, 11));
    final SubTask thirdSubTask = new SubTask("Test Third SubTask", "Test save description", NEW, 10,
            LocalDateTime.of(2023, 1, 1, 11, 21), 1);


    @BeforeAll
    void creating() {

        try {

            server = new KVServer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        server.start();
        taskServer = new HttpTaskServer();
        manager = Managers.getHttpTaskManager();
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTypeAdapter())
                .create();
        client = HttpClient.newHttpClient();

    }

    @Test
    void saveOnServerAndLoadingFromServerTest() {//проверяем и запись на сервер и чтение сразу в одном тесте, чтобы не
        // писать каждый раз заново одни и те же данные, все остальное покрыто в FileBackedTasksManagerTest.

        String jsonTask = gson.toJson(task);
        url = URI.create("http://localhost:8080/tasks/task?id=0&key=1234");
        request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                assertEquals("200\nTask с id 0 успешно добавлен", response.statusCode() + "\n" +
                        response.body(), "Неверное сохранение на сервер.");
            }

        } catch (IOException | InterruptedException e) {
            System.out.println("Тест выкинул эксепшен");
        }

        jsonTask = gson.toJson(newTask);
        url = URI.create("http://localhost:8080/tasks/task?id=1&key=1234");
        request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals("200\nTask с id 1 успешно добавлен", response.statusCode() + "\n" +
                    response.body(), "Неверное сохранение на сервер.");

        } catch (IOException | InterruptedException e) {
            System.out.println("Где-то случилось непоправимое 2");
        }

        jsonTask = gson.toJson(epic);
        url = URI.create("http://localhost:8080/tasks/epic?id=2&key=1234");
        request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals("200\nEpic с id 2 успешно добавлен", response.statusCode() + "\n" +
                    response.body(), "Неверное сохранение на сервер.");
        } catch (IOException | InterruptedException e) {
            System.out.println("Где-то случилось непоправимое 3");
        }

        jsonTask = gson.toJson(newEpic);
        url = URI.create("http://localhost:8080/tasks/epic?id=3&key=1234");
        request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals("200\nEpic с id 3 успешно добавлен", response.statusCode() + "\n" +
                    response.body(), "Неверное сохранение на сервер.");
        } catch (IOException | InterruptedException e) {
            System.out.println("Где-то случилось непоправимое 4");
        }

        url = URI.create("http://localhost:8080/tasks?key=1234");
        request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals("200\nid,name,status,description,duration,startTime,endTime,epic\n" +
                    "0,Test Task,NEW,Test save description,10,2023-01-01T11:00,2023-01-01T11:10,\n" +
                    "1,Test Second Task,IN_PROGRESS,Test save description,10,2023-01-01T11:11,2023-01-01T11:21,\n" +
                    "2,Test Epic,NEW,Test save description,0,null,null,\n" +
                    "3,Test Second Epic,NEW,Test save description,0,null,null,\n",
                    response.statusCode() + "\n" +
                    response.body(), "Неверное сохранение на сервер.");

        } catch (IOException | InterruptedException e) {
            System.out.println("Где-то случилось непоправимое 5");
        }


        jsonTask = gson.toJson(subTask);
        url = URI.create("http://localhost:8080/tasks/subtask?id=4&key=1234");
        request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals("200\nSubTask с id 4 успешно добавлен", response.statusCode() + "\n" +
                    response.body(), "Неверное сохранение на сервер.");
        } catch (IOException | InterruptedException e) {
            System.out.println("Где-то случилось непоправимое 6");
        }

        jsonTask = gson.toJson(newSubTask);
        url = URI.create("http://localhost:8080/tasks/subtask?id=5&key=1234");
        request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals("200\nSubTask с id 5 успешно добавлен", response.statusCode() + "\n" +
                    response.body(), "Неверное сохранение на сервер.");
        } catch (IOException | InterruptedException e) {
            System.out.println("Где-то случилось непоправимое 7");
        }

        jsonTask = gson.toJson(thirdTask);
        url = URI.create("http://localhost:8080/tasks/task?id=6&key=1234");
        request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals("402\nЗадача пересекается с одной из существующих.", response.statusCode() + "\n" +
                    response.body(), "Неверное сохранение на сервер.");
        } catch (IOException | InterruptedException e) {
            System.out.println("Где-то случилось непоправимое 8");
        }

        jsonTask = gson.toJson(thirdSubTask);
        url = URI.create("http://localhost:8080/tasks/subtask?id=7&key=1234");
        request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals("402\nЗадача пересекается с одной из существующих.", response.statusCode() + "\n" +
                    response.body(), "Неверное сохранение на сервер.");
        } catch (IOException | InterruptedException e) {
            System.out.println("Где-то случилось непоправимое 9");
        }

        url = URI.create("http://localhost:8080/tasks?key=1234");
        request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals("200\nid,name,status,description,duration,startTime,endTime,epic\n" +
                            "0,Test Task,NEW,Test save description,10,2023-01-01T11:00,2023-01-01T11:10,\n" +
                            "1,Test Second Task,IN_PROGRESS,Test save description,10,2023-01-01T11:11,2023-01-01T11:21,\n" +
                            "2,Test Epic,NEW,Test save description,10,2023-01-01T11:22,2023-01-01T11:32,\n" +
                            "3,Test Second Epic,NEW,Test save description,10,2023-01-01T11:33,2023-01-01T11:43,\n" +
                            "4,Test SubTask,NEW,Test save description,10,2023-01-01T11:22,2023-01-01T11:32,2,\n" +
                            "5,Test Second SubTask,NEW,Test save description,10,2023-01-01T11:33,2023-01-01T11:43,3,\n",
                    response.statusCode() + "\n" +
                            response.body(), "Неверное сохранение на сервер.");
        } catch (IOException | InterruptedException e) {
            System.out.println("Где-то случилось непоправимое 10");
        }

        final Task taskUpdate = new Task("Test Task", "Test save description", NEW, 10,
                LocalDateTime.of(2023, 1, 2, 11, 0));//id 0
        jsonTask = gson.toJson(taskUpdate);
        url = URI.create("http://localhost:8080/tasks/task?id=0&key=1234");
        request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals("200\nTask с id 0 успешно обновлен", response.statusCode() + "\n" +
                    response.body(), "Неверное сохранение на сервер.");
        } catch (IOException | InterruptedException e) {
            System.out.println("Где-то случилось непоправимое 11");
        }

        url = URI.create("http://localhost:8080/tasks/task?id=1&key=1234");
        request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .DELETE()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals("200\nTask с id 1 успешно удален", response.statusCode() + "\n" +
                    response.body(), "Неверное сохранение на сервер.");
        } catch (IOException | InterruptedException e) {
            System.out.println("Где-то случилось непоправимое 12");
        }

        url = URI.create("http://localhost:8080/tasks/task?id=0&key=1234");
        request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals("200\nid,name,status,description,duration,startTime,endTime\n" +
                            "0,Test Task,NEW,Test save description,10,2023-01-02T11:00,2023-01-02T11:10,\n",
                    response.statusCode() + "\n" + response.body(), "Неверное сохранение на сервер.");
        } catch (IOException | InterruptedException e) {
            System.out.println("Где-то случилось непоправимое 13");
        }

        final SubTask subTask = new SubTask("Test SubTask", "Test save description", NEW, 10,
                LocalDateTime.of(2023, 1, 2, 11, 22), 2);//id 4
        jsonTask = gson.toJson(subTask);
        url = URI.create("http://localhost:8080/tasks/subtask?id=4&key=1234");
        request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals("200\nSubTask с id 4 успешно обновлен", response.statusCode() + "\n" +
                    response.body(), "Неверное сохранение на сервер.");
        } catch (IOException | InterruptedException e) {
            System.out.println("Где-то случилось непоправимое 14");
        }

        url = URI.create("http://localhost:8080/tasks/subtask?id=5&key=1234");
        request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .DELETE()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals("200\nSubTask с id 5 успешно удален", response.statusCode() + "\n" +
                    response.body(), "Неверное сохранение на сервер.");
        } catch (IOException | InterruptedException e) {
            System.out.println("Где-то случилось непоправимое 15");
        }

        url = URI.create("http://localhost:8080/tasks/subtask?id=4&key=1234");
        request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals("200\nid,name,status,description,duration,startTime,endTime,epic\n" +
                            "4,Test SubTask,NEW,Test save description,10,2023-01-02T11:22,2023-01-02T11:32,2,\n",
                    response.statusCode() + "\n" + response.body(), "Неверное сохранение на сервер.");
        } catch (IOException | InterruptedException e) {
            System.out.println("Где-то случилось непоправимое 16");
        }

        final Epic epic = new Epic("Test Epic", "Test save description", NEW);//id2
        jsonTask = gson.toJson(epic);
        url = URI.create("http://localhost:8080/tasks/epic?id=2&key=1234");
        request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals("402\nEpic с id 2 уже существует", response.statusCode() + "\n" +
                    response.body(), "Неверное сохранение на сервер.");
        } catch (IOException | InterruptedException e) {
            System.out.println("Где-то случилось непоправимое 17");
        }

        url = URI.create("http://localhost:8080/tasks/epic?id=3&key=1234");
        request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .DELETE()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals("200\nEpic с id 3 и все его subTask'и успешно удалены", response.statusCode() +
                    "\n" + response.body(), "Неверное сохранение на сервер.");
        } catch (IOException | InterruptedException e) {
            System.out.println("Где-то случилось непоправимое 18");
        }

        url = URI.create("http://localhost:8080/tasks/epic?id=2&key=1234");
        request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals("200\nid,name,status,description,duration,startTime,endTime\n" +
                            "2,Test Epic,NEW,Test save description,10,2023-01-01T11:22,2023-01-02T11:32,\n",
                    response.statusCode() + "\n" + response.body(), "Неверное сохранение на сервер.");
        } catch (IOException | InterruptedException e) {
            System.out.println("Где-то случилось непоправимое 19");
        }

        taskServer.stopIt();
        server.stopIt();
    }

}
