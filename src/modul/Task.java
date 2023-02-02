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

}
