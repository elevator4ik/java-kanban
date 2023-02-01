
package manager;

import modul.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import java.nio.file.Paths;
import java.util.List;


public class FileBackedTasksManager extends manager.InMemoryTaskManager {

    Path path;
    private final String fileDir = System.getProperty("user.dir") +
            File.separator +
            "src" +
            File.separator +
            "files" +
            File.separator +
            "storage.csv";//Прописываем путь к файлу. Скорее всего дальше будет реализована возможность подгрузки
    // информации из разных файлов и в меню будет выбираться файл, который потом будет передаваться в методы чтения/
    // записи, но пока руками задаём путь.

    public FileBackedTasksManager() {//конструктор для проверки и создания файла, если он не существует
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
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void addTask(Task task) {

        super.addTask(task);
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addSubTask(SubTask subTask) {

        super.addSubTask(subTask);
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateTask(Task task) {

        super.updateTask(task);
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void updateSubTask(SubTask subTask) {

        super.updateSubTask(subTask);
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void deleteSubTaskById(int i) {

        super.deleteSubTaskById(i);
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateEpic(Epic epic) {

        super.updateEpic(epic);
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void rewriteEpic(Epic epic, List<Integer> subTasks, Status statusNew) {

        super.rewriteEpic(epic, subTasks, statusNew);
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteEpicList() {

        super.deleteEpicList();
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteTaskList() {

        super.deleteTaskList();
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteSubTaskList() {

        super.deleteSubTaskList();
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteEpicById(int i) {

        super.deleteEpicById(i);
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteTaskById(int i) {

        super.deleteTaskById(i);
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }
    }

    private void save() throws ManagerSaveException {//перезапись файла

        String header = "id,type,name,status,description,epic\n";//пишем шапку файла, а дальше все такси и историю.
        // Задаём жёсткую иерархию записи, дабы упростить последующее считывание файла и избежать.
        StringBuilder task = new StringBuilder();
        StringBuilder epic = new StringBuilder();
        StringBuilder subTask = new StringBuilder();
        StringBuilder history = new StringBuilder();

        for (Task thisTask : historyManager.getHistory()) {//идем по истории и сразу пишем ее в билдер
            history.append(thisTask.getTaskId()).append(",");
            if (taskList.containsKey(thisTask.getTaskId())) {//сверяем со списками и пишем в интересующий билдер

                task.append(thisTask.getTaskId())
                        .append(",").append(TaskType.TASK)
                        .append(",").append(thisTask.getName())
                        .append(",").append(thisTask.getStatus())
                        .append(",").append(thisTask.getDescription())
                        .append(",\n");
            } else if (epicList.containsKey(thisTask.getTaskId())) {//не приводим к типу эпик, т.к. записываемые
                // данные идентичны таску

                epic.append(thisTask.getTaskId())
                        .append(",").append(TaskType.EPIC)
                        .append(",").append(thisTask.getName())
                        .append(",").append(thisTask.getStatus())
                        .append(",").append(thisTask.getDescription())
                        .append(",\n");
            } else if (subTaskList.containsKey(thisTask.getTaskId())) {

                SubTask newT = (SubTask) thisTask;//приводм к типу сабтаск, т.к. пишем уникальные для типа данные

                subTask.append(newT.getTaskId())
                        .append(",").append(TaskType.SUB_TASK)
                        .append(",").append(newT.getName())
                        .append(",").append(newT.getStatus())
                        .append(",").append(newT.getDescription())
                        .append(",").append(newT.getEpicId())
                        .append(",\n");
            }
        }

        try {
            Files.write(path, (header + task + epic + subTask + " \n" + history + "\n").getBytes());//пишем в файл
        } catch (IOException e) {//ловим IOException и выбрасываем свое
            throw new ManagerSaveException();
        }
    }

    public void readFromFile() {//читаем из файла. Отдельные методы для тасок и истории, как говорилось в подсказке к ТЗ
        // решил не вводить, все делается последовательно в один заход

        try (BufferedReader br = new BufferedReader(new FileReader(fileDir))) {
            if (br.readLine() == null) {//проверяем, пуст ли файл, если да - пишем, что он пуст и ничего не пытаемся
                // считать из него
                System.out.println("Файл истории пуст");
            } else {
                while (br.ready()) {//считываем циклом последовательно строки из файла
                    String line = br.readLine();
                    String[] split = line.split(",");
                    String[] newSplit = new String[split.length];
                    for (int i = 0; i < newSplit.length; i++) {
                        newSplit[i] = split[i].replace(",", "");//затираем "," в конце каждого
                        // получаемого парамтера
                    }
                    if (!newSplit[0].equals(" ")) {//в разделительную строку в файле был записан " ", чтобы не пытаться
                        // записать пустоту в хистори менеджер.

                        switch (newSplit[1]) {//выясняем тип таски и пишем в ее мапу. Принцип записи в файл подразумевает
                            // жёсткий порядок task-epic-subTusk, так что проблем по причине отсутствия у сабтасков
                            // связанных эпиков не будет.
                            case "TASK":

                                taskList.put(Integer.valueOf(newSplit[0]), Task.fromString(line));

                                break;
                            case "EPIC":

                                epicList.put(Integer.valueOf(newSplit[0]), Epic.fromString(line));

                                break;
                            case "SUB_TASK":

                                subTaskList.put(Integer.valueOf(newSplit[0]), SubTask.fromString(line));

                                break;
                            default: //пишем историю

                                for (String s : newSplit) {

                                    int i = Integer.parseInt(s);

                                    if (taskList.containsKey(i)) {

                                        historyManager.add(taskList.get(i));
                                    } else if (epicList.containsKey(i)) {

                                        historyManager.add(epicList.get(i));
                                    } else {

                                        historyManager.add(subTaskList.get(i));
                                    }
                                }
                                printFile();//печатаем содержимое файла(для тестов)
                                break;
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printFile() {//метод для тестов
        try {
            String content = Files.readString(Paths.get(fileDir));
            System.out.println(content);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class ManagerSaveException extends Exception {
        public ManagerSaveException() {

        }
    }
}