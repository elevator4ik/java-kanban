package manager;


import KV.KVClient;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import modul.Epic;
import modul.LocalDateTypeAdapter;
import modul.SubTask;
import modul.Task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Collectors;

public class HttpTaskManager extends FileBackedTasksManager {
    private final Gson gson;
    private final KVClient kvClient;
    private String key;

    public HttpTaskManager() {

        kvClient = new KVClient();
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTypeAdapter())
                .create();//применяем LDT адаптер
    }

    @Override
    public void idFromSource(int keyFromSource) {// idFromSource() у родителя принимает int, поэтому и передаем int
        key = String.valueOf(keyFromSource);
    }

    @Override
    public void readFromSource() {

        JsonElement json = JsonParser.parseString(kvClient.load(key));

        if (!json.toString().equals("\"blank\"")) {

            JsonObject jsonObject = json.getAsJsonObject();
            ArrayList<Task> tasks = gson.fromJson(jsonObject.get("tasks"), new TypeToken<ArrayList<Task>>() {
            }.getType());
            ArrayList<Epic> epics = gson.fromJson(jsonObject.get("epics"), new TypeToken<ArrayList<Epic>>() {
            }.getType());
            ArrayList<SubTask> subtasks = gson.fromJson(jsonObject.get("subtasks"), new TypeToken<ArrayList<SubTask>>() {
            }.getType());
            ArrayList<Integer> history = gson.fromJson(jsonObject.get("history"), new TypeToken<ArrayList<Integer>>() {
            }.getType());

            if (!tasks.isEmpty()) {
                for (Task task : tasks) {
                    taskList.put(task.getTaskId(), task);
                    if (task.getTaskId() >= id) {
                        super.idFromSource(task.getTaskId());
                    }
                }
            }
            if (!epics.isEmpty()) {
                for (Epic task : epics) {
                    epicList.put(task.getTaskId(), task);
                    if (task.getTaskId() >= id) {
                        super.idFromSource(task.getTaskId());
                    }
                }
            }
            if (!subtasks.isEmpty()) {
                for (SubTask task : subtasks) {
                    subTaskList.put(task.getTaskId(), task);
                    if (task.getTaskId() >= id) {
                        super.idFromSource(task.getTaskId());
                    }
                }
            }
            if (!history.isEmpty()) {
                for (int i : history) {
                    if (taskList.containsKey(i)) {
                        historyManager.add(taskList.get(i));
                    } else if (epicList.containsKey(i)) {
                        historyManager.add(epicList.get(i));
                    } else if (subTaskList.containsKey(i)) {
                        historyManager.add(subTaskList.get(i));
                    }
                }
            }
        }
    }

    @Override
    public void save() {

        HashMap<String, Collection> toJson = new HashMap<>();
        toJson.put("tasks", taskList.values());
        toJson.put("subtasks", subTaskList.values());
        toJson.put("epics", epicList.values());
        toJson.put("history", historyManager.getHistory().stream().map(Task::getTaskId).
                collect(Collectors.toList()));

        String json = gson.toJson(toJson);
        kvClient.save(key, json);
    }
}