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
        return taskId + "," + name + "," + status + "," + description + "," + duration + "," + startTime + "," +
                getEndTime() + "," + epicId + ",\n";
    }

    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if ((o != null) && (getClass() == o.getClass())) {
            SubTask newS = (SubTask) o;

            if (super.equals(newS)) {
                result = getEpicId() == newS.getEpicId();
            }
        }
        return result;
    }
}
