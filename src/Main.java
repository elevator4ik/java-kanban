import manager.Manager;

//раскидал по пакам классы для удобства
public class Main {
    //в соответствии с ТЗ, в мэйне только заглушки для проверки работоспособности остального кода
    public static void main(String[] args) {

        Manager manager = new Manager();

        manager.createTask("First", "First one task");
        manager.createTask("Second", "Second one task");
        manager.createEpic("First", "First one Epic");
        manager.updateSubTask("under_first", "first of First", "NEW", 2);
        manager.updateSubTask("under_first", "second of First", "NEW", 3);
        manager.createEpic("Second", "Second oneEpic");
        manager.updateSubTask("under_second", "first of Second", "NEW", 8);
        System.out.println("эпики \n" + manager.getEpicList() + "\n" +
                "Сабтаски  \n" + manager.getSubTaskList() + "\n" +
                "Таски \n" + manager.getTaskList());
        manager.updateSubTask("under_first", "first of First", "DONE", 5);
        manager.updateSubTask("under_first", "second of First", "DONE", 11);
        manager.updateSubTask("under_second", "first of Second", "IN_PROGRESS", 9);
        manager.updateTask("IN_PROGRESS", 0);
        System.out.println("эпики \n" + manager.getEpicList() + "\n" +
                "Сабтаски  \n" + manager.getSubTaskList() + "\n" +
                "Таски \n" + manager.getTaskList());
        manager.deleteTaskById(0);
        manager.deleteSubTaskById(18);
        manager.deleteEpicById(14);
        System.out.println("эпики \n" + manager.getEpicList() + "\n" +
                "Сабтаски  \n" + manager.getSubTaskList() + "\n" +
                "Таски \n" + manager.getTaskList());
        manager.deleteEpicList();
        manager.deleteTaskList();
        manager.deleteSubTaskList();
        System.out.println("эпики \n" + manager.getEpicList() + "\n" +
                "Сабтаски  \n" + manager.getSubTaskList() + "\n" +
                "Таски \n" + manager.getTaskList());
    }
}
