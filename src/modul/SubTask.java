package modul;

import java.time.LocalDateTime;

public class SubTask extends Task {

    protected int epicId;

    public SubTask(String name, String description, Status status, int duration, LocalDateTime startTime, int epicId) {
        super(name, description, status, duration, startTime);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return taskId + "," + name + "," + status + "," + description + "," + duration + "," + startTime + "," + epicId + ",\n";
    }

    public boolean equals(SubTask o) {
        boolean result;
        if (super.equals(o)) {
            result = this.getEpicId() == o.getEpicId();
        } else {
            result = false;
        }
        return result;
    }
}
