import manager.Manager;

//раскидал по пакам классы для удобства
public class Main {
    //в соответствии с ТЗ, в мэйне только заглушки для проверки работоспособности остального кода
    public static void main(String[] args) {

        Manager manager = new Manager();

        manager.updateTask("First", "First one task", "NEW", -1);
        manager.updateTask("Second", "Second one task", "NEW", -1);
        manager.updateEpicList("First", "First one Epic");
        manager.updateSubTask("under_first", "first of First", "NEW", 2);
        manager.updateSubTask("under_first", "second of First", "NEW", 3);
        manager.updateEpicList("Second", "Second oneEpic");
        manager.updateSubTask("under_second", "first of Second", "NEW", 8);
        System.out.println("эпики \n" + manager.getEpicList() + "\n" +
                "Сабтаски  \n" + manager.getSubTaskList() + "\n" +
                "Таски \n" + manager.getTaskList());
        manager.updateSubTask("under_first", "first of First", "DONE", 5);
        manager.updateSubTask("under_first", "second of First", "DONE", 11);
        manager.updateSubTask("under_second", "first of Second", "IN_PROGRESS", 9);
        manager.updateTask("First", "First one task", "IN_PROGRESS", 0);
        System.out.println("эпики \n" + manager.getEpicList() + "\n" +
                "Сабтаски  \n" + manager.getSubTaskList() + "\n" +
                "Таски \n" + manager.getTaskList());
        System.out.println("Методы удаления задач всех видов по id используются в методах обновления списков \n" +
                "задач, ниже проверяются методы полного удаления информации из списков задач всех видов.\n");
        manager.deleteEpicList();
        manager.deleteTaskList();
        manager.deleteSubTaskList();
        System.out.println("эпики \n" + manager.getEpicList() + "\n" +
                "Сабтаски  \n" + manager.getSubTaskList() + "\n" +
                "Таски \n" + manager.getTaskList());
    }
}
