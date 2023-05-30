package manager;


import KV.KVClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import modul.LocalDateTypeAdapter;
import modul.ManagerSaveException;
import modul.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDateTime;
import java.util.List;

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
    public void readFromSource() {//
        JsonElement json = JsonParser.parseString(kvClient.load(key));
        String response = gson.fromJson(json, String.class);

        if (!response.equals("blank")) {
            try (BufferedReader br = new BufferedReader(new StringReader(response))) {
                if (br.readLine() == null) {
                    System.out.println("Данные не вернулись");
                } else {
                    readingFromServer(br);
                }
            } catch (IOException e) {
                throw new ManagerSaveException();
            }
        }
    }

    private void readingFromServer(BufferedReader br) throws IOException {
        int newId = 0;//чтобы записать id в менеджер
        while (br.ready()) {
            String line = br.readLine();
            if (!(line == null)) {
                String[] split = line.split("\n");
                if (!"id".equals(split[0])) {

                    for (String value : split) {
                        String[] split1 = value.split(",");

                        if (!" ".equals(split1[0]) && !"".equals(split1[0])) {//триггер на разделитель между тасками и историей

                            int findedId = Integer.parseInt(split1[0]);//сравниваем id и пишем больший
                            if (findedId > newId) {
                                newId = findedId;
                            }

                            writingToLists(split1);
                        }
                    }
                }
            } else {
                break;
            }
            idFromSource(newId);//пишем id в менеджер
        }
    }

    @Override
    public void save() {
        String header = "id,type,name,status,description,duration,startTime,endTime,epic\n";
        StringBuilder task = new StringBuilder();
        StringBuilder epic = new StringBuilder();
        StringBuilder subTask = new StringBuilder();
        StringBuilder history = new StringBuilder();
        List<Task> historyMan = historyManager.getHistory();

        for (Task thisTask : historyMan) {//идем по истории и сразу пишем ее в билдер

            int thisId = thisTask.getTaskId();
            history.append(thisId).append(",");
        }
        writingToBuilders(task, epic, subTask);
        String allInString = header + task + epic + subTask + " \n" + history + "\n";
        String jsonTaskManager = gson.toJson(allInString);
        kvClient.save(key, jsonTaskManager);
    }
}
