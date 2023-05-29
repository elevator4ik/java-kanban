package manager;


import KV.KVClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import modul.LocalDateTypeAdapter;
import modul.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class HttpTaskManager extends FileBackedTasksManager {

    private final Gson gson;
    final HttpServer httpServer;
    private final KVClient kvClient = new KVClient();
    private int key = 0;

    public HttpTaskManager() {
        try {
            httpServer = HttpServer.create();
            httpServer.bind(new InetSocketAddress("localhost", 8079), 0);
            httpServer.createContext("/save", this::saveTaskManager);
            httpServer.createContext("/load", this::loadTaskManager);
            httpServer.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTypeAdapter())
                .create();//применяем LDT адаптер
    }

    public void stopIt() {
        httpServer.stop(0);
    }

    private void loadTaskManager(HttpExchange h) {

        String method = h.getRequestMethod();
        String param = h.getRequestURI().getRawQuery();//получаем параметры запроса

        try {
            String value;
            if (chekForKey(h, param)) {
                h.close();
                return;
            }
            if (method.equals("GET")) {
                loadFromServer(key);

                if (getTaskList().isEmpty() & getEpicList().isEmpty() & getSubTaskList().isEmpty()) {
                    value = "blank";
                } else {
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

                    value = (header + task + epic + subTask+ " \n" + history + "\n");//удаляем [] и ", " после
                    // каждой задачи из списков перед отправкой, чтобы получить однородную структуру ответа, как при записи
                    // в файл, с которой потом можно работать.
                }
                String jsonTaskManager = gson.toJson(value);
                h.sendResponseHeaders(200, 0);
                h.getResponseBody().write(jsonTaskManager.getBytes());
                System.out.println("/load Задачи успешно отправлены!");

            } else {
                System.out.println("/load ждёт GET-запрос, а получил: " + method);

                h.sendResponseHeaders(405, 0);
                h.getResponseBody();
            }
        } catch (IOException e) {
            System.out.println("Тут случилась беда");
        } finally {
            h.close();
        }
    }

    private void saveTaskManager(HttpExchange h) {

        String method = h.getRequestMethod();
        String param = h.getRequestURI().getRawQuery();//получаем параметры запроса
        try {
            if (chekForKey(h, param)) {
                h.close();
                return;
            }
            if (method.equals("POST")) {
                String firstResult = new BufferedReader(new InputStreamReader(h.getRequestBody()))
                        .lines().collect(Collectors.joining("\n"));
                JsonElement jsonElement = JsonParser.parseString(firstResult);

                String result = jsonElement.getAsString();
                loadingIncom(result);
                saveOnServer(key);

                String answer = "/save Задачи успешно отправлены!";
                h.sendResponseHeaders(200, 0);
                h.getResponseBody();
                System.out.println(answer);

            } else {
                System.out.println("/save ждёт POST-запрос, а получил: " + method);

                h.sendResponseHeaders(405, 0);
                h.getResponseBody();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            h.close();
        }
    }

    private boolean chekForKey(HttpExchange h, String param) throws IOException {

        int newKey;
        boolean result = true;
        if (param != null && param.contains("key=")) {
            String[] s = param.split("=");
            String keyFromRequest = s[1];
            if (!keyFromRequest.isEmpty()) {
                newKey = Integer.parseInt(keyFromRequest);//выцепляем key
                key = newKey;

            } else {
                System.out.println("key отсутствует в запросе.");
                h.sendResponseHeaders(403, 0);
                h.getResponseBody();
                result = false;
            }
        } else {
            System.out.println("Некорректный запрос.");
            h.sendResponseHeaders(405, 0);
            h.getResponseBody();
            result = false;
        }

        return !result;
    }

    private void saveOnServer(int key) {

        String header = "id,type,name,status,description,duration,startTime,endTime,epic\n";
        StringBuilder task = new StringBuilder();
        StringBuilder epic = new StringBuilder();
        StringBuilder subTask = new StringBuilder();
        StringBuilder history = new StringBuilder();
        List<Task> historyMan = getHistory();

        for (Task thisTask : historyMan) {

            int thisId = thisTask.getTaskId();
            history.append(thisId).append(",");
        }
        writingToBuilders(task, epic, subTask);

        String allInString = header + task + epic + subTask + " \n" + history + "\n";
        String jsonTaskManager = gson.toJson(allInString);
        kvClient.save(key, jsonTaskManager);
    }

    private void loadFromServer(int key) {
        String keyForClient = String.valueOf(key);
        JsonElement jsonElement = JsonParser.parseString(kvClient.load(keyForClient));
        String result = jsonElement.getAsString();
        loadingIncom(result);
    }

    public void loadingIncom(String result) {
        historyManager.clear();//чистим все хранилища и счётчик перед записью информации с сервера
        taskList.clear();
        subTaskList.clear();
        epicList.clear();
        sortetTasks.clear();
        id = 0;
        String value = result.replaceAll("\"", "");

        if (value.equals("blank")) {
            System.out.println("Ответ пуст");
        } else {
            int newId = 0;//чтобы записать id в менеджер

            try (BufferedReader br = new BufferedReader(new StringReader(value))) {
                while (br.ready()) {
                    String line = br.readLine();
                    if(line == null) {
                        br.close();
                        break;
                    }
                    String[] splitFirst = line.split("\n");

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
                            idFromFile(newId);//пишем id в менеджер
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}