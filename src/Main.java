import modul.Epic;
import modul.Status;
import modul.SubTask;
import modul.Task;
import util.Managers;

public class Main {

    public static void main(String[] args) {

        Managers manager = new Managers();//как было сказано куратором на Q&A, реализуем общение с таскменеджером через
        // менеджера. Я вижу это так, что в мейне создается только менеджер, который уже в себе хранит таскменеджера

        Task task;//объявляем 1 таск и перезаписываем его, а потом, при передаче в taskManager, приводим к нужному классу.

        task = new Task("First", "First one task", Status.NEW);

        manager.taskManager.addTask(task);

        task = new Task("Second", "Second one task", Status.NEW);

        manager.taskManager.addTask(task);

        task = new Epic("First_Epic", "First one Epic", Status.NEW);

        manager.taskManager.addEpic((Epic) task);//приводим к нужному классу и тд по коду

        task = new SubTask("under_first", "first of First", Status.NEW, 2);

        manager.taskManager.addSubTask((SubTask) task);
        System.out.println(manager.taskManager.getHistory() + "\n");

        task = new SubTask("under_first", "second of First", Status.NEW, 2);

        manager.taskManager.addSubTask((SubTask) task);

        task = new Epic("Second_Epic", "Second oneEpic", Status.NEW);

        manager.taskManager.addEpic((Epic) task);
        System.out.println(manager.taskManager.getHistory() + "\n");

        task = new SubTask("under_second", "first of Second", Status.NEW, 5);

        manager.taskManager.addSubTask((SubTask) task);

        task = new SubTask("under_first", "first of First", Status.DONE, 2);

        task.setTaskId(3);

        manager.taskManager.updateSubTask((SubTask) task);

        task = new SubTask("under_first", "second of First", Status.IN_PROGRESS, 2);

        task.setTaskId(4);

        manager.taskManager.updateSubTask((SubTask) task);

        task = new SubTask("under_second", "first of Second", Status.DONE, 5);

        task.setTaskId(6);

        manager.taskManager.updateSubTask((SubTask) task);

        task = new Task("First", "First one task", Status.IN_PROGRESS);

        task.setTaskId(0);

        manager.taskManager.updateTask(task);
        manager.taskManager.deleteTaskById(0);
        System.out.println(manager.taskManager.getHistory() + "\n");
    }
}
