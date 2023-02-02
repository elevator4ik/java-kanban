
import manager.FileBackedTasksManager;
import manager.TaskManager;
import modul.Epic;
import modul.Status;
import modul.SubTask;
import modul.Task;
import util.Managers;


public class Main {

    public static void main(String[] args) {

        TaskManager manager = Managers.getFileBackedTasksManager();

        Task task;//объявляем 1 таск и перезаписываем его, а потом, при передаче в taskManager, приводим к нужному классу.


        task = new Task("First", "First one task", Status.NEW);
        manager.addTask(task);


        task = new Task("Second", "Second one task", Status.NEW);
        manager.addTask(task);


        task = new Epic("First_Epic", "First one Epic", Status.NEW);
        manager.addEpic((Epic) task);//приводим к нужному классу и тд по коду


        task = new SubTask("under_first", "first of First", Status.NEW, 2);
        manager.addSubTask((SubTask) task);


        task = new SubTask("under_first", "second of First", Status.NEW, 2);
        manager.addSubTask((SubTask) task);


        task = new SubTask("under_first", "third of First", Status.NEW, 2);
        manager.addSubTask((SubTask) task);


        task = new Epic("Second_Epic", "Second one Epic", Status.NEW);
        manager.addEpic((Epic) task);


        task = new SubTask("under_first", "first of First", Status.IN_PROGRESS, 2);
        task.setTaskId(3);
        manager.updateSubTask((SubTask) task);


        task = new SubTask("under_first", "second of First", Status.DONE, 2);
        task.setTaskId(4);
        manager.updateSubTask((SubTask) task);


        task = new SubTask("under_first", "first of First", Status.DONE, 2);
        task.setTaskId(3);
        manager.updateSubTask((SubTask) task);


        task = new Task("First", "First one task", Status.IN_PROGRESS);
        task.setTaskId(0);
        manager.updateTask(task);


       task = new Task("Second", "Second one task", Status.IN_PROGRESS);
        task.setTaskId(1);
        manager.updateTask(task);

        FileBackedTasksManager.printFile();
        System.out.println(manager.getHistory());//для сравнения истории из памяти и файла
        TaskManager manager3 = Managers.getFileBackedTasksManager();
        manager3.readFromFile();//заполняем мапы
        System.out.println(manager3.getEpicList());
        System.out.println(manager3.getTaskList());
        System.out.println(manager3.getSubTaskList());
        System.out.println(manager3.getHistory());

        FileBackedTasksManager.printFile();//сделал статичным и перенес пока в мэйн
    }


}
