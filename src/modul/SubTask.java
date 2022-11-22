package modul;

public class SubTask extends Task {

    protected int epicId;

    public SubTask(String name, String description, String status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "SubTask{" + "name= '" + name + "', \n" +
                "description= '" + description + "', \n" +
                "taskId= '" + taskId + "', \n" +
                "status= '" + status + "', \n" +
                "epicId= '" + epicId + "'}";
    }
}
