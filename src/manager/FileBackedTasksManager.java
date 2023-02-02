
package manager;

import modul.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;


public class FileBackedTasksManager extends manager.InMemoryTaskManager {

    FromString fromString = new FromString();//вынес в отдельный класс
    Path path;
    private static final String fileDir = System.getProperty("user.dir") +
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
    public void rewriteEpic(Epic epic, List<Integer> subTasks, Status statusNew) {

        super.rewriteEpic(epic, subTasks, statusNew);

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

    private void save() throws ManagerSaveException {//перезапись файла

        String header = "id,type,name,status,description,epic\n";//пишем шапку файла, а дальше все такси и историю.
        // Задаём жёсткую иерархию записи, дабы упростить последующее считывание файла и избежать.
        StringBuilder task = new StringBuilder();
        StringBuilder epic = new StringBuilder();
        StringBuilder subTask = new StringBuilder();
        StringBuilder history = new StringBuilder();
        List<Task> historyMan = historyManager.getHistory();
        //System.out.println(historyMan);
        for (Task thisTask : historyMan) {//идем по истории и сразу пишем ее в билдер

            int thisId = thisTask.getTaskId();
            history.append(thisId).append(",");
        }
        for (int i = 0; i < super.getLastId(); i++) {
            if (taskList.containsKey(i)) {//сверяем со списками и пишем в интересующий билдер
                Task thisTask = taskList.get(i);
                task.append(thisTask.getTaskId())
                        .append(",").append(TaskType.TASK)
                        .append(",").append(thisTask.getName())
                        .append(",").append(thisTask.getStatus())
                        .append(",").append(thisTask.getDescription())
                        .append(",\n");
            } else if (epicList.containsKey(i)) {//если не сделать эпик эпиком - не пишет в файл

                Epic thisEpic = epicList.get(i);
                epic.append(thisEpic.getTaskId())
                        .append(",").append(TaskType.EPIC)
                        .append(",").append(thisEpic.getName())
                        .append(",").append(thisEpic.getStatus())
                        .append(",").append(thisEpic.getDescription())
                        .append(",\n");
            } else if (subTaskList.containsKey(i)) {

                SubTask thisSubTask = subTaskList.get(i);//приводм к типу сабтаск, т.к. пишем уникальные для типа данные
                subTask.append(thisSubTask.getTaskId())
                        .append(",").append(TaskType.SUB_TASK)
                        .append(",").append(thisSubTask.getName())
                        .append(",").append(thisSubTask.getStatus())
                        .append(",").append(thisSubTask.getDescription())
                        .append(",").append(thisSubTask.getEpicId())
                        .append(",\n");
            }
        }

        try {
            //System.out.println(task +"\n"+ epic +"\n"+ subTask +"\n"+ history +"\n");
            Files.write(path, (header + task + epic + subTask + " \n" + history + "\n").getBytes());//пишем в файл
        } catch (IOException e) {//ловим IOException и выбрасываем свое
            throw new ManagerSaveException();
        }
    }

    @Override//переписать с нуля
    public void readFromFile() {
        StringBuilder wtfReader = new StringBuilder();//билдер чтобы восстановить в конце чтения данные обратно, ибо я
        // не понял, что происходит с файлом, но в нем остается первая строка и история обработки сабтасок. Если не
        // использовать после этого метода save(), то файл накрывается медным агрегатом, менее
        try (BufferedReader br = new BufferedReader(new FileReader(fileDir))) {
            if (br.readLine() == null) {
                System.out.println("Файл истории пуст");
            } else {
                int newId = 0;//чтобы записать id в менеджер
                while (br.ready()) {
                    String line = br.readLine();
                    wtfReader.append(line).append("\n");//пишем в билдер данные из файла
                    String[] split = line.split("\n");
                    if (!split[0].equals("id")) {

                        for (String value : split) {
                            String[] split1 = value.split(",");

                            if (!split1[0].equals(" ")) {//триггер на разделитель между тасками и историей

                                int ttId = Integer.parseInt(split1[0]);//сравниваем id и пишем больший
                                if (ttId > newId) {
                                    newId = ttId;
                                }
                                switch (split1[1]) {//раскидываем таски по своим мапам. Ввиду жесткой иерархии записи в
                                    // файл, чтение происходит по принципу таск-эпик-сабтаск и проблем с отсутствием
                                    // эпикоа при записи сабтаска не будет
                                    case "TASK":
                                        Task task = fromString.taskFromString(split1);
                                        taskList.put(task.getTaskId(), task);

                                        break;
                                    case "EPIC":
                                        Epic epic = fromString.epicFromString(split1);
                                        epicList.put(epic.getTaskId(), epic);

                                        break;
                                    case "SUB_TASK":
                                        SubTask subTask = fromString.subTaskFromString(split1);
                                        subTaskList.put(subTask.getTaskId(), subTask);
                                        super.updateSubTask(subTask);//обноввляем сабтасклист эпика при каждом чтении сабтаска

                                        break;
                                    default: //теперь пишем историю

                                        for (String s : split1) {

                                            int j = Integer.parseInt(s);

                                            if (taskList.containsKey(j)) {

                                                historyManager.add(taskList.get(j));
                                            } else if (epicList.containsKey(j)) {

                                                historyManager.add(epicList.get(j));
                                            } else if (subTaskList.containsKey(j)) {

                                                historyManager.add(subTaskList.get(j));
                                            }
                                        }
                                        break;
                                }
                            }
                        }
                    }
                }
                super.idFromFile(newId);//пишем id в менеджер
            }
            Files.write(path, Collections.singleton(wtfReader));//восстанавливаем данные в файле
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void printFile() {//метод для тестов
        try {
            String content = Files.readString(Paths.get(fileDir));
            System.out.println(content);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}