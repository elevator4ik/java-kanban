package KV;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import manager.InMemoryTaskManager;
import manager.Managers;
import modul.*;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class HttpTaskServer {

    InMemoryTaskManager manager;
    private final Gson gson;
    private final HttpServer httpServer;
    private final HttpClient client;

    public HttpTaskServer() {
        try {
            httpServer = HttpServer.create();
            httpServer.bind(new InetSocketAddress("localhost", 8080), 0);
            httpServer.createContext("/tasks", this::getAllTasks);
            httpServer.createContext("/tasks/task", this::taskHandler);
            httpServer.createContext("/tasks/subtask", this::subTaskHandler);
            httpServer.createContext("/tasks/epic", this::epicHandler);
            httpServer.createContext("/tasks/history", this::historyHandler);
            httpServer.start();

            client = HttpClient.newHttpClient();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        endOfEndpoint();
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTypeAdapter())
                .create();//применяем LDT адаптер
    }

    private void endOfEndpoint() { //в конце каждого эндпоинта мы обновляем менеджера на сервере
        manager = new InMemoryTaskManager();
    }

    public void stopIt() {
        httpServer.stop(0);
    }

    private void getAllTasks(HttpExchange h) {

        String method = h.getRequestMethod();
        String param = h.getRequestURI().getRawQuery();//получаем параметры запроса

        try {
            int key;
            if (param != null && param.contains("key=")) {
                key = chekForKey(h, param);
            } else {
                System.out.println("Некорректный запрос?.");
                h.sendResponseHeaders(405, 0);
                h.getResponseBody();
                return;
            }
            if (key == -1) {
                System.out.println("Некорректный запрос.");
                h.sendResponseHeaders(405, 0);
                h.getResponseBody();
                h.close();
                return;
            }
            if (method.equals("GET")) {
                loadFromServer(key);
                String value = ("id,type,name,status,description,duration,startTime,endTime,epic\n" +
                        manager.getTaskList() +
                        manager.getEpicList() +
                        manager.getSubTaskList()).replaceAll("[\\[\\]]", "")
                        .replaceAll("\n, ", "\n");//удаляем [] и ", " после каждой задачи из списков
                // перед отправкой, чтобы получить однородную структуру ответа, как при записи в файл, с которой
                // потом можно работать.

                h.sendResponseHeaders(200, 0);
                h.getResponseBody().write(value.getBytes());
                System.out.println("Задачи успешно отправлены!");

            } else {
                System.out.println("/tasks ждёт GET-запрос, а получил: " + method);

                h.sendResponseHeaders(405, 0);
                h.getResponseBody();
            }
        } catch (IOException e) {
            System.out.println("Тут случилась беда");
        } finally {
            h.close();
        }
        endOfEndpoint();
    }

    private void taskHandler(HttpExchange h) {

        String method = h.getRequestMethod();
        String param = h.getRequestURI().getRawQuery();//получаем параметры запроса
        try {
            int id;
            int key;
            if (param != null && (param.contains("id=") & param.contains("&"))) {
                key = chekForKey(h, param);
                id = chekForId(h, param);
            } else {
                System.out.println("Некорректный запрос.");
                h.sendResponseHeaders(405, 0);
                h.getResponseBody();
                h.close();
                return;
            }
            if (key == -1 && id == -1) {
                System.out.println("Некорректный запрос.");
                h.sendResponseHeaders(405, 0);
                h.getResponseBody();
                h.close();
                return;
            }

            switch (method) {
                case "GET":
                    loadFromServer(key);
                    if (checkForContains(h, id).equals("Task")) {

                        String value = ("id,type,name,status,description,duration,startTime,endTime\n" +
                                manager.getTaskById(id)).replaceAll("[\\[\\]]", "");

                        h.sendResponseHeaders(200, 0);
                        h.getResponseBody().write(value.getBytes());
                        System.out.println("task успешно отправлен!");
                    } else {
                        System.out.println("taskId указан некорректно.");
                        h.sendResponseHeaders(403, 0);
                        h.getResponseBody();
                    }
                    break;
                case "POST":
                    loadFromServer(key);
                    String result = new BufferedReader(new InputStreamReader(h.getRequestBody()))
                            .lines().collect(Collectors.joining("\n"));
                    JsonElement jsonElement = JsonParser.parseString(result);

                    Task task = gson.fromJson(jsonElement, Task.class);

                    if (id != -11 && checkForContains(h, id).equals("Task")) {
                        task.setTaskId(id);
                        manager.updateTask(task);
                        saveOnServer(key, h);
                        String value = "Task с id " + id + " успешно обновлен";

                        h.sendResponseHeaders(200, 0);
                        h.getResponseBody().write(value.getBytes());
                        System.out.println(value);
                    } else if (id == -11) {
                        int listSize = manager.getTaskList().size();//записываем длинну тасклиста, чтобы удостовериться,
                        //что новый таск не попал в пересечение с другими и, если таки попал, сигнализировать об ошибке
                        manager.addTask(task);

                        if (manager.getTaskList().size() == listSize) {
                            String value = "Задача пересекается с одной из существующих.";

                            h.sendResponseHeaders(402, 0);
                            h.getResponseBody().write(value.getBytes());
                            System.out.println(value);
                            break;
                        }
                        int newId = manager.getLastId() - 1;
                        saveOnServer(key, h);
                        String value = "Task с id " + newId + " успешно добавлен";
                        h.sendResponseHeaders(200, 0);
                        h.getResponseBody().write(value.getBytes());
                        System.out.println(value);
                    } else {
                        System.out.println("taskId указан некорректно.");
                        h.sendResponseHeaders(403, 0);
                        h.getResponseBody();
                    }
                    break;
                case "DELETE":
                    loadFromServer(key);
                    if (checkForContains(h, id).equals("Task")) {

                        manager.deleteTaskById(id);
                        saveOnServer(key, h);
                        String value = "Task с id " + id + " успешно удален";

                        h.sendResponseHeaders(200, 0);
                        h.getResponseBody().write(value.getBytes());
                        System.out.println(value);
                    } else {
                        System.out.println("taskId указан некорректно.");
                        h.sendResponseHeaders(403, 0);
                        h.getResponseBody();
                    }
                    break;
                default:
                    System.out.println("Получен некорректный запрос: " + method +
                            ". Ожидаются только GET, POST или DELETE запросы");
                    h.sendResponseHeaders(405, 0);
                    h.getResponseBody();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            h.close();
        }
        endOfEndpoint();
    }

    private void epicHandler(HttpExchange h) {

        String method = h.getRequestMethod();
        String param = h.getRequestURI().getRawQuery();//получаем параметры запроса
        try {
            int id = -1;
            int key = -1;
            if (param != null && (param.contains("id=") & param.contains("&"))) {
                key = chekForKey(h, param);
                id = chekForId(h, param);
            } else {
                System.out.println("Некорректный запрос.");
                h.sendResponseHeaders(405, 0);
                h.getResponseBody();
            }
            if (key == -1 && id == -1) {
                System.out.println("Некорректный запрос.");
                h.sendResponseHeaders(405, 0);
                h.getResponseBody();
                h.close();
                return;
            }

            switch (method) {
                case "GET":
                    loadFromServer(key);
                    if (checkForContains(h, id).equals("Epic")) {

                        String value = ("id,type,name,status,description,duration,startTime,endTime\n" +
                                manager.getEpicById(id)).replaceAll("[\\[\\]]", "");

                        h.sendResponseHeaders(200, 0);
                        h.getResponseBody().write(value.getBytes());
                        System.out.println("epic успешно отправлен!");
                    } else {
                        System.out.println("epicId указан некорректно.");
                        h.sendResponseHeaders(403, 0);
                        h.getResponseBody();
                    }

                case "POST":
                    loadFromServer(key);
                    String result = new BufferedReader(new InputStreamReader(h.getRequestBody()))
                            .lines().collect(Collectors.joining("\n"));
                    JsonElement jsonElement = JsonParser.parseString(result);

                    Epic task = gson.fromJson(jsonElement, Epic.class);
                    if (id != -11 && checkForContains(h, id).equals("Epic")) {

                        String value = "Epic с id " + id + " уже существует";

                        h.sendResponseHeaders(402, 0);
                        h.getResponseBody().write(value.getBytes());
                        System.out.println(value);
                    } else if (id == -11) {
                        int listSize = manager.getEpicList().size();

                        manager.addEpic(task);
                        if (manager.getEpicList().size() == listSize) {
                            String value = "Задача пересекается с одной из существующих.";

                            h.sendResponseHeaders(402, 0);
                            h.getResponseBody().write(value.getBytes());
                            System.out.println(value);
                            break;
                        }
                        int newId = manager.getLastId() - 1;
                        saveOnServer(key, h);
                        String value = "Epic с id " + newId + " успешно добавлен";

                        h.sendResponseHeaders(200, 0);
                        h.getResponseBody().write(value.getBytes());
                        System.out.println(value);
                    } else {
                        System.out.println("EpicId указан некорректно.");
                        h.sendResponseHeaders(403, 0);
                        h.getResponseBody();
                    }
                    break;
                case "DELETE":
                    loadFromServer(key);
                    if (checkForContains(h, id).equals("Epic")) {

                        manager.deleteEpicById(id);
                        saveOnServer(key, h);
                        String value = "Epic с id " + id + " и все его subTask'и успешно удалены";

                        h.sendResponseHeaders(200, 0);
                        h.getResponseBody().write(value.getBytes());
                        System.out.println(value);
                    } else {
                        System.out.println("epicId указан некорректно.");
                        h.sendResponseHeaders(403, 0);
                        h.getResponseBody();
                    }
                    break;
                default:
                    System.out.println("Получен некорректный запрос: " + method +
                            ". Ожидаются только GET, POST или DELETE запросы");

                    h.sendResponseHeaders(405, 0);
                    h.getResponseBody();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            h.close();
        }
        endOfEndpoint();
    }

    private void subTaskHandler(HttpExchange h) {

        String method = h.getRequestMethod();
        String param = h.getRequestURI().getRawQuery();//получаем параметры запроса
        try {
            int id = -1;
            int key = -1;
            if (param != null && (param.contains("id=") & param.contains("&"))) {
                key = chekForKey(h, param);
                id = chekForId(h, param);
            } else {
                System.out.println("Некорректный запрос.");
                h.sendResponseHeaders(405, 0);
                h.getResponseBody();
            }
            if (key == -1 && id == -1) {
                System.out.println("Некорректный запрос.");
                h.sendResponseHeaders(405, 0);
                h.getResponseBody();
                h.close();
                return;
            }

            switch (method) {
                case "GET":
                    loadFromServer(key);
                    if (checkForContains(h, id).equals("SubTask")) {
                        String value = ("id,type,name,status,description,duration,startTime,endTime,epic\n" +
                                manager.getSubTaskById(id)).replaceAll("[\\[\\]]", "");

                        h.sendResponseHeaders(200, 0);
                        h.getResponseBody().write(value.getBytes());
                        System.out.println("subTask успешно отправлен!");
                    } else {
                        System.out.println("subTaskId указан некорректно.");
                        h.sendResponseHeaders(403, 0);
                        h.getResponseBody();
                    }
                    break;
                case "POST":
                    loadFromServer(key);
                    String result = new BufferedReader(new InputStreamReader(h.getRequestBody()))
                            .lines().collect(Collectors.joining("\n"));
                    JsonElement jsonElement = JsonParser.parseString(result);

                    SubTask task = gson.fromJson(jsonElement, SubTask.class);

                    if (id != -11 && checkForContains(h, id).equals("SubTask")) {

                        manager.updateSubTask(task);
                        saveOnServer(key, h);

                        String value = "SubTask с id " + id + " успешно обновлен";

                        h.sendResponseHeaders(200, 0);
                        h.getResponseBody().write(value.getBytes());
                        System.out.println(value);
                    } else if (id == -11) {
                        int listSize = manager.getSubTaskList().size();

                        manager.addSubTask(task);

                        if (manager.getSubTaskList().size() == listSize) {
                            String value = "Задача пересекается с одной из существующих.";

                            h.sendResponseHeaders(402, 0);
                            h.getResponseBody().write(value.getBytes());
                            System.out.println(value);
                            break;
                        }
                        int newId = manager.getLastId() - 1;
                        String value = "SubTask с id " + newId + " успешно добавлен";
                        saveOnServer(key, h);

                        h.sendResponseHeaders(200, 0);
                        h.getResponseBody().write(value.getBytes());
                        System.out.println(value);
                    } else {
                        System.out.println("SubTaskId указан некорректно.");
                        h.sendResponseHeaders(403, 0);
                        h.getResponseBody();
                    }
                    break;

                case "DELETE":
                    loadFromServer(key);
                    if (checkForContains(h, id).equals("SubTask")) {

                        manager.deleteSubTaskById(id);
                        saveOnServer(key, h);

                        String value = "SubTask с id " + id + " успешно удален";

                        h.sendResponseHeaders(200, 0);
                        h.getResponseBody().write(value.getBytes());
                        System.out.println(value);
                    } else {
                        System.out.println("subTask указан некорректно.");
                        h.sendResponseHeaders(403, 0);
                        h.getResponseBody();
                    }
                    break;

                default:
                    System.out.println("Получен некорректный запрос: " + method +
                            ". Ожидаются только GET, POST или DELETE запросы");

                    h.sendResponseHeaders(405, 0);
                    h.getResponseBody();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            h.close();
        }
        endOfEndpoint();
    }

    private void historyHandler(HttpExchange h) {

        String method = h.getRequestMethod();
        String param = h.getRequestURI().getRawQuery();

        try {
            int key;
            if (param != null && param.contains("key=")) {
                key = chekForKey(h, param);
            } else {
                System.out.println("Некорректный запрос.");
                h.sendResponseHeaders(405, 0);
                h.getResponseBody();
                return;
            }
            if (key == -1) {
                System.out.println("Некорректный запрос.");
                h.sendResponseHeaders(405, 0);
                h.getResponseBody();
                h.close();
                return;
            }

            if (method.equals("GET")) {
                loadFromServer(key);
                String value = ("id,type,name,status,description,duration,startTime,endTime,epic\n" +
                        manager.getHistory()).replaceAll("[\\[\\]]", "")
                        .replaceAll("\n, ", "\n");

                h.sendResponseHeaders(200, 0);
                h.getResponseBody().write(value.getBytes());
                System.out.println("История успешно отправлена!");

            } else {
                System.out.println("/tasks/history ждёт GET-запрос, а получил: " + method);

                h.sendResponseHeaders(405, 0);
                h.getResponseBody();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            h.close();
        }
        endOfEndpoint();
    }


    private int chekForKey(HttpExchange h, String param) throws IOException {

        String keyFromRequest = param.substring(param.indexOf("key=") + 4);

        if (!keyFromRequest.isEmpty()) {
            return Integer.parseInt(keyFromRequest);//выцепляем key
        } else {
            System.out.println("key отсутствует в запросе.");
            h.sendResponseHeaders(403, 0);
            h.getResponseBody();
            h.close();
            return -1;
        }
    }

    private int chekForId(HttpExchange h, String param) throws IOException {

        String newId = param.substring(param.indexOf("id=") + 3, param.indexOf("&"));

        if (!newId.isEmpty() & !newId.equals("new")) {
            return Integer.parseInt(newId);//выцепляем id
        } else if (newId.equals("new")) {
            return -11;
        } else {
            System.out.println("taskId отсутствует в запросе.");
            h.sendResponseHeaders(403, 0);
            h.getResponseBody();
            return -1;
        }
    }

    private void saveOnServer(int key, HttpExchange h) {// сохраняет в HttpTaskManager свое состояние

        String header = "id,type,name,status,description,duration,startTime,endTime,epic\n";
        StringBuilder task = new StringBuilder();
        StringBuilder epic = new StringBuilder();
        StringBuilder subTask = new StringBuilder();
        StringBuilder history = new StringBuilder();
        List<Task> historyMan = manager.getHistory();

        for (Task thisTask : historyMan) {

            int thisId = thisTask.getTaskId();
            history.append(thisId).append(",");
        }
        writingToBuilders(task, epic, subTask, h);

        String allInString = header + task + epic + subTask + " \n" + history + "\n";
        String jsonTaskManager = gson.toJson(allInString);
        kvClient.put(key, jsonTaskManager);
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

    /**
     * ТУТ ОСТАНОВИЛСЯ
     */
    private void loadFromServer(int key, HttpExchange h) throws IOException {//грузит из HttpTaskManager его состояние
        int newId = 0;
        String keyForSend = String.valueOf(key);
        URI url = URI.create("http://localhost:8079/load?key=" + keyForSend);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String result = new BufferedReader(new InputStreamReader(h.getRequestBody()))
                        .lines().collect(Collectors.joining("\n"));
                String[] splitFirst = result.split("\n");
                for (String i : splitFirst) {
                    String[] split = i.split(",");

                    if (!"id".equals(split[0])) {

                        if (!" ".equals(split[0]) && !"".equals(split[0])) {//триггер на разделитель между тасками и историей

                            int findedId = Integer.parseInt(split[0]);//сравниваем id и пишем больший
                            if (findedId > newId) {
                                newId = findedId;
                            }

                            writingToLists(split, key, h);
                        }

                        manager.idFromFile(newId);//пишем id в менеджер
                    }
                }
            } else {
                System.out.println("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Где-то случилось непоправимое");
        }
    }

    private String checkForContains(HttpExchange h, int id) {

        String url = h.getRequestURI().getPath();
        String listToSerach = h.getRequestURI().getPath().substring(url.indexOf("/tasks/") + 7, url.indexOf("?"));
        String result = "false";
        switch (listToSerach) {
            case "task":
                List<Task> taskList = manager.getTaskList();
                for (Task t : taskList) {
                    if (t.getTaskId() == id) {
                        result = "Task";
                        break;
                    }
                }
                break;
            case "subtask":
                List<SubTask> subTasklist = manager.getSubTaskList();
                for (SubTask t : subTasklist) {
                    if (t.getTaskId() == id) {
                        result = "SubTask";
                        break;
                    }
                }
                break;
            case "epic":
                List<Epic> epiclist = manager.getEpicList();
                for (Epic t : epiclist) {
                    if (t.getTaskId() == id) {
                        result = "Epic";
                        break;
                    }
                }
                break;
        }
        return result;
    }

    void writingToLists(String[] split1, int key, HttpExchange h) {
        if (split1.length > 1) {//проверяем, более ли чем 1 запись в истории
            switch (split1[1]) {//раскидываем таски по своим мапам. Ввиду жесткой иерархии записи в
                // файл, чтение происходит по принципу таск-эпик-сабтаск и проблем с отсутствием
                // эпика при записи сабтаска не будет
                case "TASK":
                    Task task = FromString.taskFromString(split1);
                    manager.addTaskFromServer(task);
                    manager.addToSortetTasks(task);

                    break;
                case "EPIC":
                    Epic epic = FromString.epicFromString(split1);
                    manager.addEpicFromServer(epic);

                    break;
                case "SUB_TASK":
                    SubTask subTask = FromString.subTaskFromString(split1);
                    manager.addSubTaskFromServer(subTask);
                    Epic epicST = manager.getEpicById(subTask.getEpicId());
                    List<Integer> subTasks = epicST.getSubTasks();

                    manager.addToSortetTasks(subTask);
                    if (subTasks == null) {//записываем сабтаски

                        subTasks = new ArrayList<>();
                    }

                    subTasks.add(subTask.getTaskId());
                    epicST.setSubTasks(subTasks);
                    manager.rewriteEpic(epicST, subTasks, Status.NEW);
                    break;
                default: //теперь пишем историю

                    for (String s : split1) {

                        historyWrite(s, key, h);
                    }
                    break;
            }
        } else {
            historyWrite(split1[0], key, h);
        }
    }

    void historyWrite(String s, int key, HttpExchange h) {

        int j = Integer.parseInt(s);

        if (checkForContains(h, key).equals("Task")) {
            manager.historyManager.add(manager.getTaskById(j));
        } else if (checkForContains(h, key).equals("Epic")) {
            manager.historyManager.add(manager.getEpicById(j));
        } else if (checkForContains(h, key).equals("SubTask")) {
            manager.historyManager.add(manager.getSubTaskById(j));
        }
    }

    void writingToBuilders(StringBuilder task, StringBuilder epic, StringBuilder subTask, HttpExchange h) {//public чтобы
        // запускать из HttpTaskServer
        for (int i = 0; i < manager.getLastId(); i++) {

            switch (checkForContains(h, i)) {
                case "Task":
                    Task thisTask = manager.getTaskById(i);
                    task.append(thisTask.getTaskId())
                            .append(",").append(TaskType.TASK)
                            .append(",").append(thisTask.getName())
                            .append(",").append(thisTask.getStatus())
                            .append(",").append(thisTask.getDescription())
                            .append(",").append(thisTask.getDuration())
                            .append(",").append(thisTask.getStartTime())
                            .append(",\n");
                    break;
                case "Epic":

                    Epic thisEpic = manager.getEpicById(i);
                    epic.append(thisEpic.getTaskId())
                            .append(",").append(TaskType.EPIC)
                            .append(",").append(thisEpic.getName())
                            .append(",").append(thisEpic.getStatus())
                            .append(",").append(thisEpic.getDescription())
                            .append(",").append(thisEpic.getDuration())
                            .append(",").append(thisEpic.getStartTime())
                            .append(",").append(thisEpic.getEndTime())
                            .append(",\n");
                    break;
                case "SubTask":

                    SubTask thisSubTask = manager.getSubTaskById(i);
                    subTask.append(thisSubTask.getTaskId())
                            .append(",").append(TaskType.SUB_TASK)
                            .append(",").append(thisSubTask.getName())
                            .append(",").append(thisSubTask.getStatus())
                            .append(",").append(thisSubTask.getDescription())
                            .append(",").append(thisSubTask.getDuration())
                            .append(",").append(thisSubTask.getStartTime())
                            .append(",")
                            .append(",").append(thisSubTask.getEpicId())
                            .append(",\n");
                    break;
            }
        }
    }
}
