package util;

import history.HistoryManager;
import history.InMemoryHistoryManager;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import modul.Epic;
import modul.Status;
import modul.SubTask;
import modul.Task;

public class Managers {

    TaskManager taskManager = getDefault();
    HistoryManager historyManager = getDefaultHistory();
    Task task;//объявляем 1 таск и перезаписываем его, а потом, при передаче в taskManager, приводим к нужному классу.


    public void work() {


        task = new Task("First", "First one task", Status.NEW);

        taskManager.addTask(task);
        historyManager.add(taskManager.getTaskById(0));
        System.out.println("\nИстория: \n" + historyManager.getHistory() + "\n");

        task = new Task("Second", "Second one task", Status.NEW);

        taskManager.addTask(task);
        historyManager.add(taskManager.getTaskById(1));
        historyManager.getHistory();

        task = new Epic("First_Epic", "First one Epic", Status.NEW);

        taskManager.addEpic((Epic) task);//приводим к нужному классу и тд по коду
        historyManager.add(taskManager.getEpicById(2));
        historyManager.getHistory();

        task = new SubTask("under_first", "first of First", Status.NEW, 2);

        taskManager.addSubTask((SubTask) task);
        historyManager.add(taskManager.getSubTaskById(3));
        historyManager.getHistory();

        task = new SubTask("under_first", "second of First", Status.NEW, 2);

        taskManager.addSubTask((SubTask) task);
        historyManager.add(taskManager.getSubTaskById(4));
        historyManager.getHistory();

        task = new Epic("Second_Epic", "Second oneEpic", Status.NEW);

        taskManager.addEpic((Epic) task);
        historyManager.add(taskManager.getEpicById(5));
        historyManager.getHistory();

        task = new SubTask("under_second", "first of Second", Status.NEW, 5);

        taskManager.addSubTask((SubTask) task);
        historyManager.add(taskManager.getSubTaskById(6));
        historyManager.getHistory();

        task = new SubTask("under_first", "first of First", Status.DONE, 2);

        task.setTaskId(3);

        taskManager.updateSubTask((SubTask) task);
        historyManager.add(taskManager.getSubTaskById(3));
        System.out.println("\nИстория: \n" + historyManager.getHistory() + "\n");

        task = new SubTask("under_first", "second of First", Status.IN_PROGRESS, 2);

        task.setTaskId(4);

        taskManager.updateSubTask((SubTask) task);
        historyManager.add(taskManager.getSubTaskById(4));
        historyManager.getHistory();

        task = new SubTask("under_second", "first of Second", Status.DONE, 5);

        task.setTaskId(6);

        taskManager.updateSubTask((SubTask) task);
        historyManager.add(taskManager.getSubTaskById(6));
        historyManager.getHistory();

        task = new Task("First", "First one task", Status.IN_PROGRESS);

        task.setTaskId(0);

        taskManager.updateTask(task);
        historyManager.add(taskManager.getTaskById(0));
        System.out.println("\nИстория: \n" + historyManager.getHistory() + "\n");

        taskManager.deleteTaskById(0);//проверка на перезапись, дожно быть null В конце, т.к. такс удален
        historyManager.add(taskManager.getTaskById(0));
        System.out.println("\nИстория: \n" + historyManager.getHistory() + "\n");

    }

    static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    static InMemoryHistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

}
