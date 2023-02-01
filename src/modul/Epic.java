package modul;

import java.util.List;

public class Epic extends Task {

    protected List<Integer> subTasks;

    public Epic(String name, String description, Status status) {
        super(name, description, status);
    }

    public List<Integer> getSubTasks() {

        return subTasks;
    }

    public void setSubTasks(List<Integer> i) {

        this.subTasks = i;
    }
    public static Epic fromString(String value) {
        String[] newTask = value.split(",");
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

        Epic task = new Epic(taskName, taskDescription, taskStatus);
        task.taskId = id;
        return task;
    }
}
