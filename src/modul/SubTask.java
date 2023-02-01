package modul;

public class SubTask extends Task {

    protected int epicId;

    public SubTask(String name, String description, Status status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return taskId+","+name+","+status+","+description+","+epicId+",\n";
    }


    public static SubTask fromString(String value) {
        String[] newTask = value.split(",");
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
}
