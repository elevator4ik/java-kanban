package modul;

import java.util.HashMap;

public class Epic extends Task {

    protected HashMap<Integer, String> subTasks = new HashMap<>();

    public Epic(String name, String description, int taskId, String status) {
        super(name, description, taskId, status);
    }

    public HashMap<Integer, String> getSubTasks() {

        return subTasks;
    }

    public void setSubTasks(HashMap<Integer, String> i) {

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
        return result + printSubTasks + "}";
    }

}
