package modul;

import java.util.ArrayList;

public class Epic extends Task {

    protected ArrayList<Integer> subTasks;

    public Epic(String name, String description, String status) {
        super(name, description, status);
    }

    public ArrayList<Integer> getSubTasks() {

        return subTasks;
    }

    public void setSubTasks(ArrayList<Integer> i) {

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
