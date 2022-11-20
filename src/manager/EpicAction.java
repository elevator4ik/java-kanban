package manager;

import modul.Epic;

import java.util.ArrayList;
import java.util.HashMap;

public class EpicAction {

    public Epic createEpic(String name, String description) {// создание эпика, в соответствии с ТЗ

        return new Epic(name, description, "new");
    }

    public ArrayList<String> getEpicList() {//получение списка эпиков

        ArrayList<String> list = new ArrayList<>();

        for (int i = 0; i < Manager.id; i++) {

            Epic epic = getEpicById(i);

            if (epic != null) {

                list.add("Epic id: " + epic.getTaskId() + " Epic name: " + epic.getName());
            }
        }
        return list;
    }

    public Epic getEpicById(int i) {//достаем из мапы конкретный эпик

        Epic epic;

        if (Manager.epicList.containsKey(i)) {//смотрим, есть ли такой ключ по каждому id

            epic = Manager.epicList.get(i);

            return epic;
        } else {
            return null;
        }
    }

    public void deleteEpicById(int i) {//удаление по id из мапы и всех сабтасков к нему относящихся

        Epic epic = Manager.epicList.get(i);
        HashMap<Integer, String> subTasks = epic.getSubTasks();
        for (int j = 0; j <= Manager.id; j++) {//ищем все сабтаски эпика и удаляем их
            if (subTasks.containsKey(j)) {
                Manager.subTaskList.remove(j);
            }
        }
        Manager.epicList.remove(i);
    }
}
