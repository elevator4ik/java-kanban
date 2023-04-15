package modul;

import java.time.LocalDateTime;

public class Task {
    protected String name;
    protected String description;
    protected int taskId;
    protected Status status;
    protected int duration;
    protected LocalDateTime startTime;

    public Task() {
    }//конструктор для эпика

    public Task(String name, String description, Status status, int duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
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

    public int getDuration() {

        return duration;
    }

    public LocalDateTime getStartTime() {

        return startTime;
    }

    public LocalDateTime getEndTime() {

        return startTime.plusMinutes(duration);
    }

    public String toString() {
        return taskId + "," + name + "," + status + "," + description + "," + duration + "," + startTime + ",\n";
    }

    @Override
    public boolean equals(Object o) {
        if ((o != null) && (getClass() == o.getClass())) {
            Task newT = (Task) o;
            return taskId == newT.getTaskId()
                    & name.equals(newT.getName())
                    & description.equals(newT.getDescription())
                    & status.equals(newT.getStatus())
                    & duration == newT.getDuration()
                    & startTime.isEqual(newT.getStartTime());
        } else {
            return false;
        }
    }
}
