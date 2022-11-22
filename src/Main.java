import manager.Manager;
import modul.Epic;
import modul.SubTask;
import modul.Task;

public class Main {

    public static void main(String[] args) {

        Manager manager = new Manager();
        SubTask subTask;
        Task task;
        Epic epic;

        task = new Task("First", "First one task", "NEW");
        task.setTaskId(manager.newId());

        manager.addTask(task);

        task = new Task("Second", "Second one task", "NEW");
        task.setTaskId(manager.newId());

        manager.addTask(task);

        epic = new Epic("First_Epic", "First one Epic", "NEW");
        epic.setTaskId(manager.newId());

        manager.addEpic(epic);

        subTask = new SubTask("under_first", "first of First", "NEW", 2);
        subTask.setTaskId(manager.newId());

        manager.updateSubTask(subTask);

        subTask = new SubTask("under_first", "second of First", "NEW", 4);
        subTask.setTaskId(manager.newId());

        manager.updateSubTask(subTask);

        epic = new Epic("Second_Epic", "Second oneEpic", "NEW");
        epic.setTaskId(manager.newId());

        manager.addEpic(epic);

        subTask = new SubTask("under_second", "first of Second", "NEW", 8);
        subTask.setTaskId(manager.newId());

        manager.updateSubTask(subTask);

        System.out.println("эпики \n" + manager.getEpicList() + "\n" +
                "Сабтаски  \n" + manager.getSubTaskList() + "\n" +
                "Таски \n" + manager.getTaskList() + "\n");

        subTask = new SubTask("under_first", "first of First", "DONE", 6);
        subTask.setTaskId(manager.newId());

        manager.updateSubTask(subTask);

        subTask = new SubTask("under_first", "second of First", "IN_PROGRESS", 12);
        subTask.setTaskId(manager.newId());

        manager.updateSubTask(subTask);

        subTask = new SubTask("under_second", "first of Second", "DONE", 10);
        subTask.setTaskId(manager.newId());

        manager.updateSubTask(subTask);

        task = new Task("First", "First one task", "IN_PROGRESS");
        task.setTaskId(0);

        manager.addTask(task);
        System.out.println("эпики \n" + manager.getEpicList() + "\n" +
                "Сабтаски  \n" + manager.getSubTaskList() + "\n" +
                "Таски \n" + manager.getTaskList() + "\n");
        manager.deleteTaskById(0);
        manager.deleteSubTaskById(16);
        manager.deleteEpicById(18);
        System.out.println("эпики \n" + manager.getEpicList() + "\n" +
                "Сабтаски  \n" + manager.getSubTaskList() + "\n" +
                "Таски \n" + manager.getTaskList() + "\n");
        manager.deleteEpicList();
        manager.deleteTaskList();
        manager.deleteSubTaskList();
        System.out.println("эпики \n" + manager.getEpicList() + "\n" +
                "Сабтаски  \n" + manager.getSubTaskList() + "\n" +
                "Таски \n" + manager.getTaskList());
    }
}
