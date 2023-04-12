package modul;

import java.time.LocalDateTime;
import java.util.HashMap;
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

        return this.endTime;
    }

    public void setSubTasks(List<Integer> i) {

        this.subTasks = i;

    }

    public void setEndTime(SubTask subTask) {
        if (endTime == null || this.endTime.isBefore(subTask.getEndTime())) {
            this.endTime = subTask.getEndTime();
        }
    }

    public void setStartTime(SubTask subTask) {

        if (startTime == null || this.startTime.isAfter(subTask.getStartTime())) {
            this.startTime = subTask.startTime;
        }
    }

    @Override
    public String toString() {
        return taskId +","+ name +","+ status +","+ description +","+ duration +","+ startTime +","+ endTime +",\n";
    }
    public void plusDuration(SubTask subTask) {

        if (subTasks != null && !subTasks.isEmpty()) {

            this.duration += subTask.getDuration();//высчитываем длительность
            setStartTime(subTask);//записываем дату начала
            setEndTime(subTask);//записываем дату завершения

        } else if (this.startTime.isAfter(subTask.getStartTime())) {

            this.startTime = subTask.getStartTime();
            this.duration += subTask.getDuration();
        } else if (this.endTime.isBefore(subTask.getEndTime())) {

            this.endTime = subTask.getEndTime();
            this.duration += subTask.getDuration();
        } else {
            this.duration = subTask.getDuration();
        }
    }

    public void minusDuration(HashMap<Integer, SubTask> subTaskList, SubTask subTask) {

        this.duration -= subTask.getDuration();//высчитываем длительность

        if (this.duration == 0) {//если 0, знаичт больше нет сабтасок, старт и эндтайм трём
            this.startTime = null;
            this.endTime = null;
        } else {
            if (this.startTime.isEqual(subTask.startTime)) {//проверяем, был ли это самый ранний из сабтасок
                for (int i = 0; i < subTasks.size(); i++) {//если да - ищем новый первый в очереди сабтаск
                    SubTask epicSubTask = subTaskList.get(i);
                    if (epicSubTask != null) {
                        this.startTime = epicSubTask.startTime;//пишем новый старттайм
                        if (subTasks.contains(i + 1)) {
                            SubTask nextSubTask = subTaskList.get(subTasks.get(i + 1));
                            if (nextSubTask.startTime.isBefore(this.startTime)) {
                                this.startTime = nextSubTask.getStartTime();//переписываем старттайм, если нашли более ранний
                            }
                        }
                    }
                }
            }
            if (this.endTime.isEqual(subTask.getEndTime())) {//по аналогии со старттаймом
                for (int i = 0; i < subTasks.size(); i++) {
                    SubTask epicSubTask = subTaskList.get(i);
                    this.endTime = epicSubTask.getEndTime();
                    if (subTasks.contains(i + 1)) {
                        SubTask nextSubTask = subTaskList.get(subTasks.get(i + 1));
                        if (nextSubTask.getEndTime().isAfter(this.endTime)) {
                            this.endTime = nextSubTask.getEndTime();
                        }
                    }
                }
            }
        }
    }

    public boolean equals(Epic o) {
        if ((o != null) && (getClass() == o.getClass())) {
            return this.taskId == o.getTaskId()
                    && this.name.equals(o.getName())
                    && this.description.equals(o.getDescription())
                    && this.status.equals(o.getStatus())
                    && this.duration != o.getDuration()
                    && this.startTime.isEqual(o.getStartTime())
                    && this.endTime.isEqual(o.getEndTime());
        } else {
            return false;
        }
    }
}


