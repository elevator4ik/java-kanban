package manager;


import KV.KVClient;
import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
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


public class HttpTaskServer extends FileBackedTasksManager {

    private final Gson gson;
    private final HttpServer httpServer;
    private final KVClient kvClient = new KVClient();


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
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTypeAdapter())
                .create();//применяем LDT адаптер
    }

    @Override
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
                h.close();
                return;
            }
            loadFromServer(key);
            if (method.equals("GET")) {
                String value = ("id,type,name,status,description,duration,startTime,endTime,epic\n" +
                        getTaskList() +
                        getEpicList() +
                        getSubTaskList()).replaceAll("[\\[\\]]", "")
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
                h.close();
                return;
            }
            loadFromServer(key);
            switch (method) {
                case "GET":
                    if (taskList.containsKey(id)) {

                        String value = ("id,type,name,status,description,duration,startTime,endTime\n" +
                                getTaskById(id)).replaceAll("[\\[\\]]", "");

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
                    String result = new BufferedReader(new InputStreamReader(h.getRequestBody()))
                            .lines().collect(Collectors.joining("\n"));
                    JsonElement jsonElement = JsonParser.parseString(result);

                    Task task = gson.fromJson(jsonElement, Task.class);

                    if (id != -11 && taskList.containsKey(id)) {

                        updateTask(task);
                        saveOnServer(key);
                        String value = "Task с id " + id + " успешно обновлен";

                        h.sendResponseHeaders(200, 0);
                        h.getResponseBody().write(value.getBytes());
                        System.out.println(value);
                    } else if (id == -11) {
                        int listSize = taskList.size();//записываем длинну тасклиста, чтобы удостовериться, что новый
                        // таск не попал в пересечение с другими и, если таки попал, сигнализировать об ошибке
                        addTask(task);

                        if (taskList.size() == listSize) {
                            String value = "Задача пересекается с одной из существующих.";

                            h.sendResponseHeaders(402, 0);
                            h.getResponseBody().write(value.getBytes());
                            System.out.println(value);
                            break;
                        }
                        int newId = getLastId() - 1;
                        saveOnServer(key);
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
                    if (taskList.containsKey(id)) {

                        deleteTaskById(id);
                        saveOnServer(key);
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
                h.close();
                return;
            }
            loadFromServer(key);
            switch (method) {
                case "GET":
                    if (epicList.containsKey(id)) {

                        String value = ("id,type,name,status,description,duration,startTime,endTime\n" +
                                getEpicById(id)).replaceAll("[\\[\\]]", "");

                        h.sendResponseHeaders(200, 0);
                        h.getResponseBody().write(value.getBytes());
                        System.out.println("epic успешно отправлен!");
                    } else {
                        System.out.println("epicId указан некорректно.");
                        h.sendResponseHeaders(403, 0);
                        h.getResponseBody();
                    }

                case "POST":
                    String result = new BufferedReader(new InputStreamReader(h.getRequestBody()))
                            .lines().collect(Collectors.joining("\n"));
                    JsonElement jsonElement = JsonParser.parseString(result);

                    Epic task = gson.fromJson(jsonElement, Epic.class);
                    if (id != -11 && epicList.containsKey(id)) {

                        String value = "Epic с id " + id + " уже существует";

                        h.sendResponseHeaders(402, 0);
                        h.getResponseBody().write(value.getBytes());
                        System.out.println(value);
                    } else if (id == -11) {
                        int listSize = epicList.size();

                        addEpic(task);
                        if (epicList.size() == listSize) {
                            String value = "Задача пересекается с одной из существующих.";

                            h.sendResponseHeaders(402, 0);
                            h.getResponseBody().write(value.getBytes());
                            System.out.println(value);
                            break;
                        }
                        int newId = getLastId() - 1;
                        saveOnServer(key);
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
                    if (epicList.containsKey(id)) {

                        deleteEpicById(id);
                        saveOnServer(key);
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
                h.close();
                return;
            }
            loadFromServer(key);
            switch (method) {
                case "GET":
                    if (subTaskList.containsKey(id)) {
                        String value = ("id,type,name,status,description,duration,startTime,endTime,epic\n" +
                                getSubTaskById(id)).replaceAll("[\\[\\]]", "");

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
                    String result = new BufferedReader(new InputStreamReader(h.getRequestBody()))
                            .lines().collect(Collectors.joining("\n"));
                    JsonElement jsonElement = JsonParser.parseString(result);

                    SubTask task = gson.fromJson(jsonElement, SubTask.class);

                    if (id != -11 && subTaskList.containsKey(id)) {

                        updateSubTask(task);
                        saveOnServer(key);

                        String value = "SubTask с id " + id + " успешно обновлен";

                        h.sendResponseHeaders(200, 0);
                        h.getResponseBody().write(value.getBytes());
                        System.out.println(value);
                    } else if (id == -11) {
                        int listSize = subTaskList.size();

                        addSubTask(task);

                        if (subTaskList.size() == listSize) {
                            String value = "Задача пересекается с одной из существующих.";

                            h.sendResponseHeaders(402, 0);
                            h.getResponseBody().write(value.getBytes());
                            System.out.println(value);
                            break;
                        }
                        int newId = getLastId() - 1;
                        String value = "SubTask с id " + newId + " успешно добавлен";
                        saveOnServer(key);

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
                    if (subTaskList.containsKey(id)) {

                        deleteSubTaskById(id);
                        saveOnServer(key);

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
                h.close();
                return;
            }
            loadFromServer(key);
            if (method.equals("GET")) {
                String value = ("id,type,name,status,description,duration,startTime,endTime,epic\n" +
                        getHistory()).replaceAll("[\\[\\]]", "")
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
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);

    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);

    }

    @Override
    public void addSubTask(SubTask subTask) {
        super.addSubTask(subTask);

    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);

    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);

    }

    @Override
    public void deleteTaskById(int i) {
        super.deleteTaskById(i);

    }

    @Override
    public void deleteSubTaskById(int i) {
        super.deleteSubTaskById(i);

    }

    @Override
    public void deleteEpicById(int i) {
        super.deleteEpicById(i);

    }

    @Override
    public void deleteEpicList() {
        super.deleteEpicList();

    }

    @Override
    public void deleteTaskList() {
        super.deleteTaskList();

    }

    @Override
    public void deleteSubTaskList() {
        super.deleteSubTaskList();

    }

    @Override
    public List<SubTask> getSubTaskList() {
        return super.getSubTaskList();

    }

    @Override
    public List<Task> getTaskList() {
        return super.getTaskList();
    }

    @Override
    public List<Epic> getEpicList() {
        return super.getEpicList();
    }

    @Override
    public SubTask getSubTaskById(int i) {
        return super.getSubTaskById(i);
    }

    @Override
    public Task getTaskById(int i) {
        return super.getTaskById(i);
    }

    @Override
    public Epic getEpicById(int i) {
        return super.getEpicById(i);
    }

    @Override
    public List<SubTask> getEpicSubTasks(int i) {
        return super.getEpicSubTasks(i);
    }

    @Override
    public List<Task> getHistory() {
        return super.getHistory();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);

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

    /**
     * Пока идут тесты - будет паблик, дальше должен стать приват
     */
    public void saveOnServer(int key) {

        String header = "id,type,name,status,description,duration,startTime,endTime,epic\n";
        StringBuilder task = new StringBuilder();
        StringBuilder epic = new StringBuilder();
        StringBuilder subTask = new StringBuilder();
        StringBuilder history = new StringBuilder();
        List<Task> historyMan = historyManager.getHistory();

        for (Task thisTask : historyMan) {

            int thisId = thisTask.getTaskId();
            history.append(thisId).append(",");
        }
        writingToBuilders(task, epic, subTask);

        String allInString = header + task + epic + subTask + " \n" + history + "\n";
        String jsonTaskManager = gson.toJson(allInString);
        kvClient.put(key, jsonTaskManager);
    }

    private void loadFromServer(int key) throws IOException {
        int newId = 0;//чтобы записать id в менеджер
        String keyForClient = String.valueOf(key);
        String result = kvClient.load(keyForClient);

        String[] splitFirst = result.split("\n");
        for (String i : splitFirst) {
            String[] split = i.split(",");

            if (!"id".equals(split[0])) {

                    if (!" ".equals(split[0]) && !"".equals(split[0])) {//триггер на разделитель между тасками и историей

                        int findedId = Integer.parseInt(split[0]);//сравниваем id и пишем больший
                        if (findedId > newId) {
                            newId = findedId;
                        }

                        writingToLists(split);
                    }

                super.idFromFile(newId);//пишем id в менеджер
            }
        }
    }

    /**
     * ДЛЯ ТЕСТОВ ИЗ МЭЙНА!!!! УДАЛИТЬ, КОГДА НАЧТУ ПИСАТЬ ЮНИТ-ТЕСТЫ!!!
     */
    @Override
    public void readFromFile() {
        super.readFromFile();
        saveOnServer(1234);
    }
}
