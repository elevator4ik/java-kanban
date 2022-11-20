package modul;

import java.util.HashMap;

public class Epic extends Task {

    protected HashMap<Integer, String> subTasks = new HashMap<>();// хранение реализовано через мапу, т.к. при хранении
    // в списке нет возможности задать id как идентификатор. Поиск в списке возможен по индексу или элементу(.get(int index)
    // и .get(Object obj)). Если индекс и объект имеет схожий тип (в этом случае целочисленный), то поиск происходит
    // по индексу(доказано опытным путем). Можно использовать сначала поиск индекса по элементу (.indexOf(Object obj)),
    // а потом по полученному индексу цеплять элемент, но это громоздко и увеличивает шанс словить баг или ошибку.
    // Исходя из вышесказанного принято решение использовать мапу.

    public Epic(String name, String description, String status) {
        super(name, description, status);
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
