package modul;

import java.time.LocalDateTime;

public class FromString {//преобразование тасков из строк фала вынесено в отдельный класс, под каждый тип таски свой
    // метод, т.к. возвращаться должна таска конкретного типа


    public static Task taskFromString(String[] newTask) {

        int id = Integer.parseInt(newTask[0]);
        String taskName = newTask[2];
        String s = newTask[3];
        String taskDescription = newTask[4];
        Status taskStatus;
        int duration= Integer.parseInt(newTask[5]);
        LocalDateTime startTime;

        if (!newTask[6].equals("null")) {
            startTime = LocalDateTime.parse(newTask[6]);
        } else {
            startTime = null;
        }

        if (s.equals("DONE")) {
            taskStatus = Status.DONE;
        } else if (s.equals("NEW")) {
            taskStatus = Status.NEW;
        } else {
            taskStatus = Status.IN_PROGRESS;
        }

        Task task = new Task(taskName, taskDescription, taskStatus, duration, startTime);
        task.taskId = id;
        return task;
    }

    public static SubTask subTaskFromString(String[] newTask) {

        Task task1 = taskFromString(newTask);
        SubTask task = new SubTask(task1.getName(),
                task1.getDescription(),
                task1.getStatus(), task1.getDuration(), task1.getStartTime(), Integer.parseInt(newTask[8]));
        task.taskId = task1.getTaskId();

        return task;
    }

    public static Epic epicFromString(String[] newTask) {

        Task task1 = taskFromString(newTask);
        Epic epic = new Epic(task1.getName(),
                task1.getDescription(),
                task1.getStatus());
        epic.duration = task1.getDuration();
        epic.startTime = task1.getStartTime();
        epic.taskId = task1.getTaskId();
        if (!newTask[7].equals("null")) {
            epic.endTime = LocalDateTime.parse(newTask[7]);
        } else {
            epic.endTime =null;
        }
        return epic;
    }
}
