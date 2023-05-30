package KV;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;
import modul.Epic;
import modul.LocalDateTypeAdapter;
import modul.SubTask;
import modul.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


public class HttpTaskServer {

    TaskManager manager;
    private final Gson gson;
    private final HttpServer httpServer;

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

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        manager = Managers.getDefault();
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTypeAdapter())
                .create();//применяем LDT адаптер
    }

    public void stopIt() {
        httpServer.stop(0);
    }

    private void getAllTasks(HttpExchange h) {

        String method = h.getRequestMethod();
        String param = h.getRequestURI().getRawQuery();//получаем параметры запроса

        try {
            String key;
            if (param != null && param.contains("key=")) {
                key = chekForKey(h, param);
            } else {
                System.out.println("Некорректный запрос.");
                h.sendResponseHeaders(405, 0);
                h.getResponseBody();
                h.close();
                return;
            }
            if (key.equals("false")) {
                System.out.println("Некорректный запрос.");
                h.sendResponseHeaders(405, 0);
                h.getResponseBody();
                h.close();
                return;
            }
            if (method.equals("GET")) {
                manager.idFromSource(Integer.parseInt(key));
                String value = ("tasks\n"+manager.getTaskList() + "epics\n" + manager.getEpicList() + "subtasks\n" +
                        manager.getSubTaskList());

                sendingResp(h, value);
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
    }

    private void taskHandler(HttpExchange h) {

        String method = h.getRequestMethod();
        String param = h.getRequestURI().getRawQuery();//получаем параметры запроса
        try {
            int id;
            String key;
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
            if (key.equals("false") && id == -1) {
                System.out.println("Некорректный запрос.");
                h.sendResponseHeaders(405, 0);
                h.getResponseBody();
                h.close();
                return;
            }

            switch (method) {
                case "GET":
                    manager.idFromSource(Integer.parseInt(key));
                    if (checkForContains(h, id).equals("task")) {
                        String value = String.valueOf(manager.getTaskById(id));
                        sendingResp(h, value);
                        System.out.println("task успешно отправлен!");
                    } else {
                        System.out.println("taskId указан некорректно.");
                        h.sendResponseHeaders(403, 0);
                        h.getResponseBody();
                    }
                    break;
                case "POST":
                    manager.idFromSource(Integer.parseInt(key));
                    String result = new BufferedReader(new InputStreamReader(h.getRequestBody()))
                            .lines().collect(Collectors.joining("\n"));
                    JsonElement jsonElement = JsonParser.parseString(result);

                    Task task = gson.fromJson(jsonElement, Task.class);

                    if (checkForContains(h, id).equals("task")) {
                        task.setTaskId(id);
                        manager.updateTask(task);

                        String value = "Task с id " + id + " успешно обновлен";
                        sendingResp(h, value);
                        System.out.println(value);
                    } else if (checkForType(h).equals("task")) {
                        int listSize = manager.getTaskList().size();//записываем длинну тасклиста, чтобы удостовериться,
                        //что новый таск не попал в пересечение с другими и, если таки попал, сигнализировать об ошибке
                        manager.addTask(task);
                        if (manager.getTaskList().size() == listSize) {
                            String value = "Задача пересекается с одной из существующих.";
                            String json = gson.toJson(value);
                            h.sendResponseHeaders(402, 0);
                            h.getResponseBody().write(json.getBytes());
                            System.out.println(value);
                            break;
                        }
                        int newId = manager.getLastId() - 1;
                        String value = "Task с id " + newId + " успешно добавлен";
                        sendingResp(h, value);
                        System.out.println(value);
                    } else {
                        System.out.println("taskId указан некорректно.");
                        h.sendResponseHeaders(403, 0);
                        h.getResponseBody();
                    }
                    break;
                case "DELETE":
                    manager.idFromSource(Integer.parseInt(key));
                    if (checkForContains(h, id).equals("task")) {
                        manager.deleteTaskById(id);
                        String value = "Task с id " + id + " успешно удален";

                        sendingResp(h, value);
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
    }

    private void epicHandler(HttpExchange h) {

        String method = h.getRequestMethod();
        String param = h.getRequestURI().getRawQuery();//получаем параметры запроса
        try {
            int id = -1;
            String key = "false";
            if (param != null && (param.contains("id=") & param.contains("&"))) {
                key = chekForKey(h, param);
                id = chekForId(h, param);
            } else {
                System.out.println("Некорректный запрос.");
                h.sendResponseHeaders(405, 0);
                h.getResponseBody();
            }
            if (key.equals("false") && id == -1) {
                System.out.println("Некорректный запрос.");
                h.sendResponseHeaders(405, 0);
                h.getResponseBody();
                h.close();
                return;
            }

            switch (method) {
                case "GET":
                    manager.idFromSource(Integer.parseInt(key));
                    if (checkForContains(h, id).equals("epic")) {
                        String value = String.valueOf(manager.getEpicById(id));
                        sendingResp(h, value);
                        System.out.println("epic успешно отправлен!");
                    } else {
                        System.out.println("epicId указан некорректно.");
                        h.sendResponseHeaders(403, 0);
                        h.getResponseBody();
                    }

                case "POST":
                    manager.idFromSource(Integer.parseInt(key));
                    String result = new BufferedReader(new InputStreamReader(h.getRequestBody()))
                            .lines().collect(Collectors.joining("\n"));
                    JsonElement jsonElement = JsonParser.parseString(result);
                    Epic task = gson.fromJson(jsonElement, Epic.class);
                    if (checkForContains(h, id).equals("epic")) {

                        String value = "Epic с id " + id + " уже существует";
                        String json = gson.toJson(value);
                        h.sendResponseHeaders(402, 0);
                        h.getResponseBody().write(json.getBytes());
                        System.out.println(value);
                    } else if (checkForType(h).equals("epic")) {
                        int listSize = manager.getEpicList().size();
                        manager.addEpic(task);
                        if (manager.getEpicList().size() == listSize) {
                            String value = "Задача пересекается с одной из существующих.";
                            sendingResp(h, value);
                            System.out.println(value);
                            break;
                        }
                        int newId = manager.getLastId() - 1;
                        String value = "Epic с id " + newId + " успешно добавлен";

                        sendingResp(h, value);
                        System.out.println(value);
                    } else {
                        System.out.println("EpicId указан некорректно.");
                        h.sendResponseHeaders(403, 0);
                        h.getResponseBody();
                    }
                    break;
                case "DELETE":
                    manager.idFromSource(Integer.parseInt(key));
                    if (checkForContains(h, id).equals("epic")) {
                        manager.deleteEpicById(id);
                        String value = "Epic с id " + id + " и все его subTask'и успешно удалены";

                        sendingResp(h, value);
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
    }

    private void subTaskHandler(HttpExchange h) {

        String method = h.getRequestMethod();
        String param = h.getRequestURI().getRawQuery();//получаем параметры запроса
        try {
            int id = -1;
            String key = "false";
            if (param != null && (param.contains("id=") & param.contains("&"))) {
                key = chekForKey(h, param);
                id = chekForId(h, param);
            } else {
                System.out.println("Некорректный запрос.");
                h.sendResponseHeaders(405, 0);
                h.getResponseBody();
            }
            if (key.equals("false") && id == -1) {
                System.out.println("Некорректный запрос.");
                h.sendResponseHeaders(405, 0);
                h.getResponseBody();
                h.close();
                return;
            }

            switch (method) {
                case "GET":
                    manager.idFromSource(Integer.parseInt(key));
                    if (checkForContains(h, id).equals("subtask")) {
                        String value = String.valueOf(manager.getSubTaskById(id));

                        sendingResp(h, value);
                        System.out.println("subTask успешно отправлен!");
                    } else {
                        System.out.println("subTaskId указан некорректно.");
                        h.sendResponseHeaders(403, 0);
                        h.getResponseBody();
                    }
                    break;
                case "POST":
                    manager.idFromSource(Integer.parseInt(key));
                    String result = new BufferedReader(new InputStreamReader(h.getRequestBody()))
                            .lines().collect(Collectors.joining("\n"));
                    JsonElement jsonElement = JsonParser.parseString(result);

                    SubTask task = gson.fromJson(jsonElement, SubTask.class);

                    if (checkForContains(h, id).equals("subtask")) {
                        task.setTaskId(id);
                        manager.updateSubTask(task);

                        String value = "SubTask с id " + id + " успешно обновлен";

                        sendingResp(h, value);
                        System.out.println(value);
                    } else if (checkForType(h).equals("subtask")) {
                        int listSize = manager.getSubTaskList().size();

                        manager.addSubTask(task);

                        if (manager.getSubTaskList().size() == listSize) {
                            String value = "Задача пересекается с одной из существующих.";
                            String json = gson.toJson(value);
                            h.sendResponseHeaders(402, 0);
                            h.getResponseBody().write(json.getBytes());
                            System.out.println(value);
                            break;
                        }
                        int newId = manager.getLastId() - 1;
                        String value = "SubTask с id " + newId + " успешно добавлен";

                        sendingResp(h, value);
                        System.out.println(value);
                    } else {
                        System.out.println("SubTaskId указан некорректно.");
                        h.sendResponseHeaders(403, 0);
                        h.getResponseBody();
                    }
                    break;

                case "DELETE":
                    manager.idFromSource(Integer.parseInt(key));
                    if (checkForContains(h, id).equals("subtask")) {
                        manager.deleteSubTaskById(id);
                        String value = "SubTask с id " + id + " успешно удален";

                        sendingResp(h, value);
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
    }

    private void historyHandler(HttpExchange h) {

        String method = h.getRequestMethod();
        String param = h.getRequestURI().getRawQuery();

        try {
            String key;
            if (param != null && param.contains("key=")) {
                key = chekForKey(h, param);
            } else {
                System.out.println("Некорректный запрос.");
                h.sendResponseHeaders(405, 0);
                h.getResponseBody();
                return;
            }
            if (key.equals("false")) {
                System.out.println("Некорректный запрос.");
                h.sendResponseHeaders(405, 0);
                h.getResponseBody();
                h.close();
                return;
            }

            if (method.equals("GET")) {
                manager.idFromSource(Integer.parseInt(key));

                String value = manager.getHistory().toString();

                sendingResp(h, value);
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
    }


    private String chekForKey(HttpExchange h, String param) throws IOException {

        String keyFromRequest = param.substring(param.indexOf("key=") + 4);

        if (!keyFromRequest.isEmpty()) {
            manager.readFromSource();//читаем данные с сервера по ключу, мало ли пришел новый ключ
            return keyFromRequest;//выцепляем key
        } else {
            System.out.println("key отсутствует в запросе.");
            h.sendResponseHeaders(403, 0);
            h.getResponseBody();
            h.close();
            return "false";
        }
    }

    private int chekForId(HttpExchange h, String param) throws IOException {

        String newId = param.substring(param.indexOf("id=") + 3, param.indexOf("&"));

        if (!newId.isEmpty()) {
            return Integer.parseInt(newId);//выцепляем id
        } else {
            System.out.println("Id отсутствует в запросе.");
            h.sendResponseHeaders(403, 0);
            h.getResponseBody();
            return -1;
        }
    }

    private String checkForType(HttpExchange h) {

        String url = h.getRequestURI().getPath();
        String listToSerach = h.getRequestURI().getPath().substring(url.indexOf("/tasks/") + 7);
        String result = "false";
        switch (listToSerach) {
            case "task":

                result = "task";

                break;
            case "subtask":

                result = "subtask";

                break;
            case "epic":

                result = "epic";

                break;
        }
        return result;
    }

    private String checkForContains(HttpExchange h, int id) {

        String type = checkForType(h);
        String result = "false";
        switch (type) {
            case "task":
                List<Task> taskList = manager.getTaskList();
                for (Task t : taskList) {
                    if (t.getTaskId() == id) {
                        result = "task";
                        break;
                    }
                }
                break;
            case "subtask":
                List<SubTask> subTasklist = manager.getSubTaskList();
                for (SubTask t : subTasklist) {
                    if (t.getTaskId() == id) {
                        result = "subtask";
                        break;
                    }
                }
                break;
            case "epic":
                List<Epic> epiclist = manager.getEpicList();
                for (Epic t : epiclist) {
                    if (t.getTaskId() == id) {
                        result = "epic";
                        break;
                    }
                }
                break;
        }
        return result;
    }

    private void sendingResp(HttpExchange h, String value) {
        String json = gson.toJson(value);
        try {
            h.sendResponseHeaders(200, 0);
            h.getResponseBody().write(json.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
