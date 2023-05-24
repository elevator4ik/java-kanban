
package manager;


import modul.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class FileBackedTasksManager extends InMemoryTaskManager {

    public Path path;
    private static String fileDir = System.getProperty("user.dir") +
            File.separator +
            "src" +
            File.separator +
            "files" +
            File.separator +
            "storage.csv";//путь к файлу прописываем при сощдании менеджера.

    public FileBackedTasksManager(int test) {//конструктор для тестов, передаем любое целочисленное значение как флаг

        fileDir = System.getProperty("user.dir") +
                File.separator +
                "src" +
                File.separator +
                "tests" +
                File.separator +
                "files" +
                File.separator +
                "testStorage.csv";
        try {
            this.path = Paths.get(fileDir);
            if (!Files.exists(path)) {

                Files.createFile(path);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public FileBackedTasksManager() {//конструктор для проверки и создания файла, если он не существует
        fileDir = System.getProperty("user.dir") +
                File.separator +
                "src" +
                File.separator +
                "files" +
                File.separator +
                "storage.csv";
        try {
            this.path = Paths.get(fileDir);
            if (!Files.exists(path)) {

                Files.createFile(path);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addEpic(Epic epic) {

        super.addEpic(epic);

        save();

    }

    @Override
    public void addTask(Task task) {

        super.addTask(task);

        save();
    }

    @Override
    public void addSubTask(SubTask subTask) {

        super.addSubTask(subTask);

        save();
    }

    @Override
    public void updateTask(Task task) {

        super.updateTask(task);

        save();

    }

    @Override
    public void updateSubTask(SubTask subTask) {

        super.updateSubTask(subTask);

        save();
    }

    @Override
    public void deleteSubTaskById(int i) {

        super.deleteSubTaskById(i);

        save();
    }

    @Override
    public void updateEpic(Epic epic) {

        super.updateEpic(epic);

        save();
    }

    @Override
    public void deleteEpicList() {

        super.deleteEpicList();

        save();
    }

    @Override
    public void deleteTaskList() {

        super.deleteTaskList();

        save();
    }

    @Override
    public void deleteSubTaskList() {

        super.deleteSubTaskList();

        save();
    }

    @Override
    public void deleteEpicById(int i) {

        super.deleteEpicById(i);

        save();

    }

    @Override
    public void deleteTaskById(int i) {

        super.deleteTaskById(i);

        save();

    }

    private void save() {//перезапись файла

        String header = "id,type,name,status,description,duration,startTime,endTime,epic\n";//пишем шапку файла,
        // а дальше все таски и историю.
        // Задаём жёсткую иерархию записи, дабы упростить последующее считывание файла и избежать ошибок.
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

        try {

            Files.write(path, (header + task + epic + subTask+ " \n" + history + "\n").getBytes());//пишем в файл
        } catch (IOException e) {// если ловим IOException, то выбрасываем своё исколючение
            throw new ManagerSaveException();
        }
    }

    @Override
    public void readFromFile() {
        StringBuilder wtfReader = new StringBuilder();//билдер чтобы восстановить в конце чтения данные обратно, если не
        // использовать после этого метода save(), то файл накрывается
        String header = "id,type,name,status,description,duration,startTime,endTime,epic\n";
        wtfReader.append(header);
        try (BufferedReader br = new BufferedReader(new FileReader(fileDir))) {
            if (br.readLine() == null) {
                System.out.println("Файл истории пуст");
            } else {
                fileReading(br, wtfReader);
            }
            Files.write(path, Collections.singleton(wtfReader));//восстанавливаем данные в файле
        } catch (IOException e) {
            throw new ManagerSaveException();
        }
    }


    void getSortTasksFromFile(Task task) {

        sortetTasks.add(task);
    }

    @Override
    public String printFile() {//метод для тестов
        String content = "";
        try {
            content = Files.readString(Paths.get(fileDir));

        } catch (IOException e) {
            e.printStackTrace();
        }

        return content;
    }

    void writingToBuilders(StringBuilder task, StringBuilder epic, StringBuilder subTask) {//public чтобы
        // запускать из HttpTaskServer
        for (int i = 0; i < super.getLastId(); i++) {
            if (taskList.containsKey(i)) {//сверяем со списками и пишем в интересующий билдер
                Task thisTask = taskList.get(i);
                task.append(thisTask.getTaskId())
                        .append(",").append(TaskType.TASK)
                        .append(",").append(thisTask.getName())
                        .append(",").append(thisTask.getStatus())
                        .append(",").append(thisTask.getDescription())
                        .append(",").append(thisTask.getDuration())
                        .append(",").append(thisTask.getStartTime())
                        .append(",\n");
            } else if (epicList.containsKey(i)) {

                Epic thisEpic = epicList.get(i);
                epic.append(thisEpic.getTaskId())
                        .append(",").append(TaskType.EPIC)
                        .append(",").append(thisEpic.getName())
                        .append(",").append(thisEpic.getStatus())
                        .append(",").append(thisEpic.getDescription())
                        .append(",").append(thisEpic.getDuration())
                        .append(",").append(thisEpic.getStartTime())
                        .append(",").append(thisEpic.getEndTime())
                        .append(",\n");
            } else if (subTaskList.containsKey(i)) {

                SubTask thisSubTask = subTaskList.get(i);
                subTask.append(thisSubTask.getTaskId())
                        .append(",").append(TaskType.SUB_TASK)
                        .append(",").append(thisSubTask.getName())
                        .append(",").append(thisSubTask.getStatus())
                        .append(",").append(thisSubTask.getDescription())
                        .append(",").append(thisSubTask.getDuration())
                        .append(",").append(thisSubTask.getStartTime())
                        .append(",")//ничего непишем, т.к. здесь должен быть эндтайм, а он только у эпика
                        .append(",").append(thisSubTask.getEpicId())
                        .append(",\n");
            }
        }
    }

    private void fileReading(BufferedReader br, StringBuilder wtfReader) throws IOException {
        int newId = 0;//чтобы записать id в менеджер
        while (br.ready()) {
            String line = br.readLine();
            wtfReader.append(line).append("\n");//пишем в билдер данные из файла
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
            super.idFromFile(newId);//пишем id в менеджер
        }
    }

    void writingToLists(String[] split1) {
        if (split1.length>1) {//проверяем, более ли чем 1 запись в истории
            switch (split1[1]) {//раскидываем таски по своим мапам. Ввиду жесткой иерархии записи в
                // файл, чтение происходит по принципу таск-эпик-сабтаск и проблем с отсутствием
                // эпика при записи сабтаска не будет
                case "TASK":
                    Task task = FromString.taskFromString(split1);
                    taskList.put(task.getTaskId(), task);
                    getSortTasksFromFile(task);

                    break;
                case "EPIC":
                    Epic epic = FromString.epicFromString(split1);
                    epicList.put(epic.getTaskId(), epic);

                    break;
                case "SUB_TASK":
                    SubTask subTask = FromString.subTaskFromString(split1);
                    subTaskList.put(subTask.getTaskId(), subTask);
                    Epic epicST = getEpicById(subTask.getEpicId());
                    List<Integer> subTasks = epicST.getSubTasks();

                    getSortTasksFromFile(subTask);
                    if (subTasks == null) {//записываем сабтаски

                        subTasks = new ArrayList<>();
                    }

                    subTasks.add(subTask.getTaskId());
                    epicST.setSubTasks(subTasks);

                    break;
                default: //теперь пишем историю

                    for (String s : split1) {

                        historyWrite(s);
                    }
                    break;
            }
        } else {
            historyWrite(split1[0]);
        }
    }
    void historyWrite(String s){

        int j = Integer.parseInt(s);

        if (taskList.containsKey(j)) {

            historyManager.add(taskList.get(j));
        } else if (epicList.containsKey(j)) {

            historyManager.add(epicList.get(j));
        } else if (subTaskList.containsKey(j)) {

            historyManager.add(subTaskList.get(j));
        }

    }
}