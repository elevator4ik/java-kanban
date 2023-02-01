package modul;

public class Task {
    protected String name;
    protected String description;
    protected int taskId;
    protected Status status;

    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public int getTaskId() {

        return taskId;
    }

    public void setTaskId(int taskId) {

        this.taskId = taskId;
    }

    public Status getStatus() {

        return status;
    }

    public void setStatus(Status status) {

        this.status = status;
    }
    public String getName() {

        return name;
    }
    public String getDescription() {

        return description;
    }

    public String toString() {
        return taskId + "," + name + "," + status + "," + description + ",\n";
    }

    public static Task fromString(String value) {
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

        Task task = new Task(taskName, taskDescription, taskStatus);
        task.taskId = id;
        return task;
    }

}
