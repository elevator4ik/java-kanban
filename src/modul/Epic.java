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

    @Override
    public String toString() {

        String printSubTasks;
        String result = "Epic{" + "name= '" + name + "', \n" +
                "description= '" + description + "', \n" +
                "taskId= '" + taskId + "', \n" +
                "status= '" + status + "', \n" +
                "SubTasks= ";

        if (subTasks != null) {//проверяем на null

            printSubTasks = subTasks.toString();

        } else {

            printSubTasks = "null";
        }
        return result + printSubTasks + "}\n";
    }

}
