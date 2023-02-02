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

}
