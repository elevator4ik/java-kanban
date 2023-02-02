package modul;

import java.util.List;

public class Epic extends Task {

    protected List<Integer> subTasks;

    public Epic(String name, String description, Status status) {
        super(name, description, status);
    }

    public List<Integer> getSubTasks() {

        return subTasks;
    }

    public void setSubTasks(List<Integer> i) {

        this.subTasks = i;
    }

}
