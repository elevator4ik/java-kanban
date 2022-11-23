package manager;

import modul.Epic;
import modul.SubTask;
import modul.Task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class Manager {

    HashMap<Integer, Epic> epicList = new HashMap<>();
    HashMap<Integer, Task> taskList = new HashMap<>();
    HashMap<Integer, SubTask> subTaskList = new HashMap<>();
    int id = 0;// счетчик для id, постоянно увеличивается на 1, где бы не создавался id

    private int newId() {//метод увеличения счетчика

        int newId = id;

        id++;
        return newId;
    }

    public void addEpic(Epic epic) {//добавляем полученному из вне эпику id и пишем в мапу

        epic.setTaskId(newId());

        epicList.put(epic.getTaskId(), epic);
    }

    public void addTask(Task task) {// то же что и эпик

        task.setTaskId(newId());

        taskList.put(task.getTaskId(), task);

    }

    public void addSubTask(SubTask subTask) {// то же что и эпик, только пишем еще и в эпик

        subTask.setTaskId(newId());

        updateSubTask(subTask);
    }

    public void updateTask(Task task) {//обновляем таск

        if (taskList.containsKey(task.getTaskId())) {

            taskList.remove(task.getTaskId());
            taskList.put(task.getTaskId(), task);
        }
    }


    public void updateSubTask(SubTask subTask) {//обновление сабтаска соправождается обновлением эпика

        String status;
        Epic epic = epicList.get(subTask.getEpicId());
        ArrayList<Integer> subTasks = epic.getSubTasks();
        if (subTasks != null) {
            if (subTasks.contains(subTask.getTaskId())) {//проверяем, есть ли в сабтасках эпика уже такой сабтаск
                subTasks.remove((Integer) subTask.getTaskId());//удаляем, чтобы не было дублей
            }
            putNewSubTask(subTask, subTasks);

            status = checkStatus(subTasks);

            rewriteEpic(epic, subTasks, status);

        } else {

            subTasks = new ArrayList<>();

            putNewSubTask(subTask, subTasks);

            status = subTask.getStatus();

            rewriteEpic(epic, subTasks, status);
        }
    }

    public void deleteSubTaskById(int i) {//удаление по id из мапы, принцип тот же, что и при добавлении

        String status;
        SubTask subTask = subTaskList.get(i);
        Epic epic = epicList.get(subTask.getEpicId());
        ArrayList<Integer> subTasks = epic.getSubTasks();

        if (subTasks != null) {

            subTaskList.remove(i);
            subTasks.remove((Integer) i);

            status = checkStatus(subTasks);

            rewriteEpic(epic, subTasks, status);
        }
    }

    public void putNewSubTask(SubTask subTask, ArrayList<Integer> subTasks) {// добавляем сабтаск в общую мапу и в
        // список сабтасков эпика

        subTasks.add(subTask.getTaskId());
        subTaskList.remove(subTask.getTaskId());
        subTaskList.put(subTask.getTaskId(), subTask);
    }

    public void rewriteEpic(Epic epic, ArrayList<Integer> subTasks, String statusNew) { //переписываем эпик

        epic.setStatus(statusNew);
        epic.setSubTasks(subTasks);
        epicList.remove(epic.getTaskId());
        epicList.put(epic.getTaskId(), epic);
    }

    public String checkStatus(ArrayList<Integer> subTasks) { //проверяем статус эпика и сабтасков после обновления

        String status = "";

        if (!subTasks.isEmpty()) {
            for (int i : subTasks) {

                SubTask subTask = subTaskList.get(i);

                if (subTask.getStatus().equals("IN_PROGRESS")) {//если хоть 1 сабтаск в процессе - эпик в процессе

                    status = "IN_PROGRESS";
                } else if (subTask.getStatus().equals("DONE") && !status.equals("IN_PROGRESS")) {//если все сабтаски
                    // вернут done - эпик будет выполнен

                    status = "DONE";
                } else {//tесли все сабтаски со статусом NEW, то и эпик NEW
                    status = "NEW";
                }
            }
        } else {// если сабтаски отсутствуют у эпика - статус меняется на NEW

            status = "NEW";
        }
        return status;
    }

    public void deleteEpicList() {//удаление целиком мапы

        epicList.clear();
        subTaskList.clear();//если удаляем все эпики, то и сабтаски с ними, проверять статусы не у чего
    }

    public void deleteTaskList() {//удаление целиком мапы

        taskList.clear();
    }

    public void deleteSubTaskList() {//удаление целиком мапы

        subTaskList.clear();

        for (int i : epicList.keySet()) {//идем по id эпиков и меняем статус на NEW
            Epic epic = epicList.get(i);
            if (!epic.getStatus().equals("DONE")) {//но только если эпик еще не завершен
                String status = "NEW";
                rewriteEpic(epic, null, status);//вместо списка сабтасков - null, как будто только объявили эпик
            }
        }
    }

    public void deleteEpicById(int i) {//удаление эпика по id из мапы и всех сабтасков к нему относящихся

        Epic epic = epicList.get(i);
        ArrayList<Integer> subTasks = epic.getSubTasks();
        for (int j : subTasks) {//ищем все сабтаски эпика и удаляем их

            subTaskList.remove(j);
        }
        epicList.remove(i);
    }

    public void deleteTaskById(int i) {//удаление таска по id из мапы

        taskList.remove(i);
    }

    public ArrayList<SubTask> getSubTaskList() {//вывод списка сабтасков

        Collection<SubTask> values = subTaskList.values();
        ArrayList<SubTask> list;
        list = new ArrayList<>(values);

        return list;
    }

    public ArrayList<Task> getTaskList() {//выводим все таски

        Collection<Task> values = taskList.values();
        ArrayList<Task> list;
        list = new ArrayList<>(values);

        return list;
    }

    public ArrayList<Epic> getEpicList() {//получение списка эпиков

        Collection<Epic> values = epicList.values();
        ArrayList<Epic> list;
        list = new ArrayList<>(values);

        return list;
    }
}