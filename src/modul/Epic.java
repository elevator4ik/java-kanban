package modul;

import java.time.LocalDateTime;
import java.util.List;

public class Epic extends Task {

    protected List<Integer> subTasks;
    protected LocalDateTime endTime;


    public Epic(String name, String description, Status status) {

        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = 0;
    }

    public List<Integer> getSubTasks() {

        return subTasks;
    }

    @Override
    public LocalDateTime getEndTime() {

        return endTime;
    }

    public void setSubTasks(List<Integer> i) {

        this.subTasks = i;

    }

    public void setEndTime(LocalDateTime time) {

        endTime = time;
    }

    public void setStartTime(LocalDateTime time) {

        startTime = time;
    }

    public void setDuration(int i) {

        duration = i;
    }

    @Override
    public String toString() {
        return taskId + "," + name + "," + status + "," + description + "," +
                duration + "," + startTime + "," + endTime + ",\n";
    }

    @Override
    public boolean equals(Object o) {//полностью переопределяем метод, т.к. есть полностью вычисляемые поля
        boolean result = false;
        if ((o != null) && (getClass() == o.getClass())) {
            Epic newE = (Epic) o;

            if (taskId == newE.getTaskId()
                    & name.equals(newE.getName())
                    & description.equals(newE.getDescription())
                    & status.equals(newE.getStatus())
                    & duration == newE.getDuration()) {
                if (endTime != null & newE.getEndTime() != null) {
                    if (endTime.isEqual(newE.getEndTime())) {
                        if (startTime != null & newE.getStartTime() != null) {
                            result = startTime.isEqual(newE.getStartTime());

                        } else {
                            result = (startTime == newE.getStartTime());//если одно из значени null, то equal даст NPE
                        }
                    }
                } else {
                    result = (endTime == newE.getEndTime());
                }
            }
        }
        return result;
    }
}


