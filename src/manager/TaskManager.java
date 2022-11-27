package manager;

import modul.Epic;
import modul.SubTask;
import modul.Task;

import java.util.ArrayList;

public interface TaskManager {

    void addEpic(Epic epic);

    void addTask(Task task);

    void addSubTask(SubTask subTask);

    void updateTask(Task task);

    void updateSubTask(SubTask subTask);

    void deleteSubTaskById(int i);

    void deleteEpicList();

    void deleteTaskList();

    void deleteSubTaskList();

    void deleteEpicById(int i);

    void deleteTaskById(int i);

    ArrayList<SubTask> getSubTaskList();

    ArrayList<Task> getTaskList();

    ArrayList<Epic> getEpicList();

    SubTask getSubTaskById(int i);

    Task getTaskById(int i);

    Epic getEpicById(int i);

}