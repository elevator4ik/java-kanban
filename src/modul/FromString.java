package modul;

public class FromString {//преобразование тасков из строк фала вынесено в отдельный класс
    public static Task taskFromString(String[] newTask) {

        int id = Integer.parseInt(newTask[0]);
        String taskName = newTask[2];
        String s = newTask[3];
        String taskDescription = newTask[4];
        Status taskStatus;

        if (s.equals("DONE")) {
            taskStatus = Status.DONE;
        } else if (s.equals("NEW")) {
            taskStatus = Status.NEW;
        } else {
            taskStatus = Status.IN_PROGRESS;
        }

        Task task = new Task(taskName, taskDescription, taskStatus);
        task.taskId = id;
        return task;
    }

    public static SubTask subTaskFromString(String[] newTask) {

        int id = Integer.parseInt(newTask[0]);
        int epicId = Integer.parseInt(newTask[5]);
        String taskName = newTask[2];
        String s = newTask[3];
        String taskDescription = newTask[4];
        Status taskStatus;

        if (s.equals("DONE")) {
            taskStatus = Status.DONE;
        } else if (s.equals("NEW")) {
            taskStatus = Status.NEW;
        } else {
            taskStatus = Status.IN_PROGRESS;
        }

        SubTask task = new SubTask(taskName, taskDescription, taskStatus, epicId);
        task.taskId = id;
        return task;
    }

    public static Epic epicFromString(String[] newTask) {//возвращает те же поля, что и таск, но важно чтобы это был эпик

        Task task1 = taskFromString(newTask);
        Epic epic = new Epic(task1.getName(), task1.getDescription(), task1.getStatus());
        epic.setTaskId(task1.getTaskId());
        return epic;
    }
}
