import manager.TaskManager;
import modul.Epic;
import modul.Status;
import modul.SubTask;
import modul.Task;
import util.Managers;

public class Main {

    public static void main(String[] args) {

        TaskManager manager = Managers.getDefault();

        Task task;//объявляем 1 таск и перезаписываем его, а потом, при передаче в taskManager, приводим к нужному классу.

        task = new Task("First", "First one task", Status.NEW);

        manager.addTask(task);

        task = new Task("Second", "Second one task", Status.NEW);

        manager.addTask(task);

        task = new Epic("First_Epic", "First one Epic", Status.NEW);

        manager.addEpic((Epic) task);//приводим к нужному классу и тд по коду

        task = new SubTask("under_first", "first of First", Status.NEW, 2);

        manager.addSubTask((SubTask) task);
        System.out.println(manager.getHistory() + "\n");

        task = new SubTask("under_first", "second of First", Status.NEW, 2);

        manager.addSubTask((SubTask) task);

        task = new Epic("Second_Epic", "Second oneEpic", Status.NEW);

        manager.addEpic((Epic) task);
        System.out.println(manager.getHistory() + "\n");

        task = new SubTask("under_second", "first of Second", Status.NEW, 5);

        manager.addSubTask((SubTask) task);

        task = new SubTask("under_first", "first of First", Status.DONE, 2);

        task.setTaskId(3);

        manager.updateSubTask((SubTask) task);

        task = new SubTask("under_first", "second of First", Status.IN_PROGRESS, 2);

        task.setTaskId(4);

        manager.updateSubTask((SubTask) task);

        task = new SubTask("under_second", "first of Second", Status.DONE, 5);

        task.setTaskId(6);

        manager.updateSubTask((SubTask) task);

        task = new Task("First", "First one task", Status.IN_PROGRESS);

        task.setTaskId(0);

        manager.updateTask(task);
        manager.deleteTaskById(0);
        System.out.println(manager.getHistory() + "\n");
    }
}
