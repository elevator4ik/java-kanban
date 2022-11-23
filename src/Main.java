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

        manager.addTask(task);

        task = new Task("Second", "Second one task", "NEW");

        manager.addTask(task);

        epic = new Epic("First_Epic", "First one Epic", "NEW");

        manager.addEpic(epic);

        subTask = new SubTask("under_first", "first of First", "NEW", 2);

        manager.addSubTask(subTask);

        subTask = new SubTask("under_first", "second of First", "NEW", 2);

        manager.addSubTask(subTask);

        epic = new Epic("Second_Epic", "Second oneEpic", "NEW");

        manager.addEpic(epic);

        subTask = new SubTask("under_second", "first of Second", "NEW", 5);

        manager.addSubTask(subTask);

        System.out.println("эпики \n" + manager.getEpicList() + "\n" +
                "\nСабтаски  \n" + manager.getSubTaskList() + "\n" +
                "\nТаски \n" + manager.getTaskList() + "\n");

        subTask = new SubTask("under_first", "first of First", "DONE", 2);

        subTask.setTaskId(3);

        manager.updateSubTask(subTask);

        subTask = new SubTask("under_first", "second of First", "IN_PROGRESS", 2);

        subTask.setTaskId(4);

        manager.updateSubTask(subTask);

        subTask = new SubTask("under_second", "first of Second", "DONE", 5);

        subTask.setTaskId(6);

        manager.updateSubTask(subTask);

        task = new Task("First", "First one task", "IN_PROGRESS");

        task.setTaskId(0);

        manager.updateTask(task);
        System.out.println("\nэпики \n" + manager.getEpicList() + "\n" +
                "\nСабтаски  \n" + manager.getSubTaskList() + "\n" +
                "\nТаски \n" + manager.getTaskList() + "\n");
        manager.deleteTaskById(0);
        manager.deleteSubTaskById(4);
        manager.deleteEpicById(5);
        System.out.println("\nэпики \n" + manager.getEpicList() + "\n" +
                "\nСабтаски  \n" + manager.getSubTaskList() + "\n" +
                "\nТаски \n" + manager.getTaskList() + "\n");
        manager.deleteEpicList();
        manager.deleteTaskList();
        manager.deleteSubTaskList();
        System.out.println("эпики \n" + manager.getEpicList() + "\n" +
                "Сабтаски  \n" + manager.getSubTaskList() + "\n" +
                "Таски \n" + manager.getTaskList());
    }
}
