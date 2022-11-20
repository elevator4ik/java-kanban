package manager;

import modul.Epic;
import modul.SubTask;
import modul.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class Manager {
    // В ТЗ сказано, что менеджер хранит в себе информацию по всем типам задач. Статики для постоянного хранения, пока
// программа работает
    static HashMap<Integer, Epic> epicList = new HashMap<>();
    static HashMap<Integer, Task> taskList = new HashMap<>();
    static HashMap<Integer, SubTask> subTaskList = new HashMap<>();
    static int id = 0;// счетчик для id, постоянно увеличивается на 1, где бы не создавался id
    TaskAction taskAction = new TaskAction();
    SubTaskAction subTaskAction = new SubTaskAction();
    EpicAction epicAction = new EpicAction();

    public static int newId() {//метод увеличения счетчика

        int newId = id;

        id++;
        return newId;
    }

    // метода создания сабтаска нет - он реализуется через update, т.к. там происходит обновление еще и эпика

    public void createEpic(String name, String description) {

        Epic epic = epicAction.createEpic(name, description);
        epic.setTaskId(newId());

        epicList.put(epic.getTaskId(), epic);
    }

    public void createTask(String name, String description) {

        Task task = taskAction.createTask(name, description);
        task.setTaskId(newId());

        taskList.put(task.getTaskId(), task);
    }

    public void updateSubTask(String name, String description, String status, int epicId) {
        subTaskAction.updateSubTask(name, description, status, epicId);
    }

    public void updateTask(String status, int taskId) {

        taskAction.updateTask(status, taskId);
    }

    public void deleteSubTaskList() {//удаление целиком мапы

        subTaskList.clear();
    }

    public void deleteEpicList() {//удаление целиком мапы

        epicList.clear();
    }

    public void deleteTaskList() {//удаление целиком мапы

        taskList.clear();
    }

    public void deleteSubTaskById(int i) {//удаление по id из мапы

        subTaskAction.deleteSubTaskById(i);
    }

    public void deleteEpicById(int i) {//удаление по id из мапы

        epicAction.deleteEpicById(i);
    }

    public void deleteTaskById(int i) {//удаление по id из мапы

        taskAction.deleteTaskById(i);
    }

    //print
    public ArrayList<String> getSubTaskList() {
        return subTaskAction.getSubTaskList();
    }

    public ArrayList<String> getEpicList() {
        return epicAction.getEpicList();
    }

    public ArrayList<String> getTaskList() {
        return taskAction.getTaskList();
    }
}