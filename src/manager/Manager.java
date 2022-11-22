package manager;

import modul.Epic;
import modul.SubTask;
import modul.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class Manager {

    HashMap<Integer, Epic> epicList = new HashMap<>();
    HashMap<Integer, Task> taskList = new HashMap<>();
    HashMap<Integer, SubTask> subTaskList = new HashMap<>();
    int id = 0;// счетчик для id, постоянно увеличивается на 1, где бы не создавался id
    HashMap<Integer, String> newSubTasks;
    Epic newEpic;

    SubTask newSubTask;
    String statusNew = "";
    int old = 0;//переменная старого эпика, чтобы удалить его, когда перезаписываем его с новыми данными

    public int newId() {//метод увеличения счетчика

        int newId = id;

        id++;
        return newId;
    }

    // методов создания нет - они реализуются через update; эпик создается единожды и обновляется только с сабтасками
    public void addEpic(Epic epic) {//метод нужен только ради того, чтобы мапа хранения не была статик

        epicList.put(epic.getTaskId(), epic);
    }

    public void addTask(Task task) {//метод нужен только ради того, чтобы мапа хранения не была статик

        taskList.put(task.getTaskId(), task);

    }

    public void updateSubTask(SubTask subTask) {//обновление сабтасков в мапах хранения и эпиков; записываются в мапу
        // они тоже тут, т.к. сабтаск не может существовать без эпика

        for (int i = 0; i < id; i++) {//идем по всем id и ищем эпики

            Epic epic = getEpicById(i);
            HashMap<Integer, String> subTasks = getSubTasksByEpic(i);

            if (epic != null && epic.getTaskId() == subTask.getEpicId()) {// эпик id соответствует записанному в сабтаске

                writeNewEpic(epic, subTasks);//запоминаем эпик, который нашли
            }
        }
        epicList.remove(old);//удалили старый, чтобы не захламлял мапу-хранилище, новый сейчас в newEpic

        if (!newSubTasks.isEmpty()) {//смотрим, есть ли записанные сабтаски у эпика

            newEpic.setTaskId(newId());//тут сразу задаем новый id эпику, чтобы он писался на все сабтакси

            subTask.setEpicId(newEpic.getTaskId());//обнавляем эпик id в сабтаске, которую передали из вне

            for (int j = 0; j <= id; j++) {//перебираем все id

                if (newSubTasks.containsKey(j) && subTask.getTaskId() != j) {//раз этот метод работает, то мы
                    // получили обновление в эпик, значит надо везде обновить его id, так же проверяем, не последний ли
                    // созданный это сабтаск, если так - значит он уже перезаписан

                    SubTask thisSubTask = getSubTaskById(j);//тащим из общего хранилища, ссылки одинаковые

                    if (subTask.getDescription().equals(thisSubTask.getDescription())) {//сравниваем описание сабтаска
                        // с тем, что лежал в эпике, после чего перезаписываем (названия у задач могут быть одинаковыми
                        // и проверка по ним вызовет баг, например "подмести" и "подмести", а вот описание - уникально
                        // хеш и eqauls не помогут, т.к. они будут составляться из уникального id)

                        removeOldSubTasks(j);
                        newSubTask = subTask;// пишем обновленный сабтаск во временный объект

                    } else if (thisSubTask.getEpicId() == old) {//сравниваем эпик id сабтаска и записанный старый id
                        // эпика, если меняем не заявленный сабтаск, а остальные сабтаски эпика

                        removeOldSubTasks(j);
                        newSubTask = thisSubTask;
                        newSubTask.setEpicId(newEpic.getTaskId());
                        newSubTask.setTaskId(newId());//скопировали все, дали новый id и закинули актуальный id эпика
                    }

                    putNewSubTasks(newSubTask);//обновили в мапе для эпика и общей мапе

                } else if (subTask.getTaskId() == j) {//если сабтаска нет в списке и это передаваемый сабтаск -
                    // записываем, т.к. это новый сабтаск эпика

                    putNewSubTasks(subTask);
                }
            }
            checkStatus();
            rewriteEpic();
        } else if (newEpic != null) {//если эпик есть, а сабтасков нет - это его первый сабтаск

            newEpic.setTaskId(newId());
            subTask.setEpicId(newEpic.getTaskId());
            statusNew = subTask.getStatus();
            putNewSubTasks(subTask);
            rewriteEpic();
        }
    }

    public void deleteEpicList() {//удаление целиком мапы

        epicList.clear();
    }

    public void deleteTaskList() {//удаление целиком мапы

        taskList.clear();
    }

    public void deleteSubTaskList() {//удаление целиком мапы

        subTaskList.clear();

        for (int j = 0; j <= id; j++) {//ищем все эпики и даем статус нового, только если он не был уже завершен
            if (epicList.containsKey(j)) {

                Epic epic = epicList.get(j);

                if (!epic.getStatus().equals("DONE")) {

                    writeNewEpic(epic, epic.getSubTasks());

                    newSubTasks.clear();
                    statusNew = "NEW";

                    rewriteEpic();
                }
            }
        }
    }

    public void deleteEpicById(int i) {//удаление эпика по id из мапы и всех сабтасков к нему относящихся

        Epic epic = epicList.get(i);
        HashMap<Integer, String> subTasks = epic.getSubTasks();
        for (int j = 0; j <= id; j++) {//ищем все сабтаски эпика и удаляем их
            if (subTasks.containsKey(j)) {
                subTaskList.remove(j);
            }
        }
        epicList.remove(i); //обновлять статус у эпика при удалении сабтаска не нужно, т.к. он все равно тоже удаляется
    }

    public void deleteTaskById(int i) {//удаление таска по id из мапы

        taskList.remove(i);
    }

    public ArrayList<String> getSubTaskList() {//печать списка сабтасков

        ArrayList<String> list = new ArrayList<>();

        for (int i = 0; i < id; i++) {

            SubTask subTask = getSubTaskById(i);

            if (subTask != null) {

                list.add("SubTask id: " + subTask.getTaskId() + " SubTask name: " + subTask.getName());
            }
        }
        return list;
    }

    public SubTask getSubTaskById(int i) {//тащим сабтаск из общего списка

        SubTask subTask;

        if (subTaskList.containsKey(i)) {

            subTask = subTaskList.get(i);

            return subTask;
        } else {
            return null;
        }
    }

    public HashMap<Integer, String> getSubTasksByEpic(int i) {// достаем список сабтасков конкретного эпика

        Epic epic;
        HashMap<Integer, String> subTasks;

        if (epicList.containsKey(i)) {

            epic = epicList.get(i);
            subTasks = epic.getSubTasks();

            return subTasks;
        } else {
            return null;
        }
    }

    public void checkStatus() { //проверяем статус эпика и сабтасков после обновления

        for (int k = 0; k < id; k++) {

            if (newSubTasks != null && newSubTasks.containsKey(k)) {// ищем те сабтаски, что есть в обновленном эпике

                SubTask subTask = getSubTaskById(k);

                if (subTask.getStatus().equals("IN_PROGRESS")) {//если хоть 1 сабтаск в процессе - эпик в процессе
                    statusNew = "IN_PROGRESS";

                } else if (subTask.getStatus().equals("DONE")) {//если все сабтаски вернут done - эпик будет выполнен
                    statusNew = "DONE";
                }
            } else {// если все сабтаски не прошли или вовсе отсутствуют у эпика - статус меняется на NEW
                statusNew = "NEW";
            }
        }
    }

    public void writeNewEpic(Epic epic, HashMap<Integer, String> subTasks) {//пишем эпик и его сабтаск лист
        newEpic = epic;
        newSubTasks = subTasks;
        old = epic.getTaskId();
    }

    public void putNewSubTasks(SubTask subTask) {// доавляем сабтаск в список эпика и общую мапу-хранилище

        newSubTasks.put(subTask.getTaskId(), subTask.getName());
        subTaskList.put(subTask.getTaskId(), subTask);

    }

    public void removeOldSubTasks(int i) {//удаляем старый сабтаск из списка эпика и из общего хранилища сабтасков

        newSubTasks.remove(i);
        subTaskList.remove(i);
    }

    public void rewriteEpic() { //переписываем эпик

        newEpic.setStatus(statusNew);
        newEpic.setSubTasks(newSubTasks);
        epicList.put(newEpic.getTaskId(), newEpic);
        epicList.remove(old);
    }

    public void deleteSubTaskById(int i) {//удаление по id из мапы, принцип тот же, что и при добавлении

        SubTask subTask = subTaskList.get(i);
        Epic epic = getEpicById(subTask.getEpicId());
        HashMap<Integer, String> subTasks = getSubTasksByEpic(subTask.getEpicId());

        writeNewEpic(epic, subTasks);
        epicList.remove(old);
        if (!newSubTasks.isEmpty()) {

            newEpic.setTaskId(newId());

            for (int j = 0; j <= id; j++) {

                if (newSubTasks.containsKey(j)) {

                    SubTask updatingSubTask = subTaskList.get(j);

                    if (old == updatingSubTask.getEpicId()) {

                        SubTask thisSubTask = getSubTaskById(j);

                        if (subTask.getDescription().equals(thisSubTask.getDescription())) {

                            removeOldSubTasks(j);
                        }
                        putNewSubTasks(subTask);
                    }
                }
            }
        }
        checkStatus();
        rewriteEpic();
    }

    public ArrayList<String> getEpicList() {//получение списка эпиков

        ArrayList<String> list = new ArrayList<>();

        for (int i = 0; i < id; i++) {

            Epic epic = getEpicById(i);

            if (epic != null) {

                list.add("Epic id: " + epic.getTaskId() + " Epic name: " + epic.getName());
            }
        }
        return list;
    }

    public Epic getEpicById(int i) {//достаем из мапы конкретный эпик

        Epic epic;

        if (epicList.containsKey(i)) {

            epic = epicList.get(i);

            return epic;
        } else {
            return null;
        }
    }

    public ArrayList<String> getTaskList() {//выводим все таски

        ArrayList<String> list = new ArrayList<>();

        for (int i = 0; i < id; i++) {

            Task task = getTaskById(i);

            if (task != null) {

                list.add("Task id: " + task.getTaskId() + " Task name: " + task.getName());
            }
        }
        return list;
    }

    public Task getTaskById(int i) {//достаем конкретный таск

        Task task;

        if (taskList.containsKey(i)) {

            task = taskList.get(i);

            return task;
        } else {
            return null;
        }
    }
}