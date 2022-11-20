package manager;

import modul.Epic;
import modul.SubTask;


import java.util.ArrayList;
import java.util.HashMap;

public class SubTaskAction {

    HashMap<Integer, String> newSubTasks;
    Epic newEpic;
    SubTask subTask;
    SubTask newSubTask;
    String statusNew = "";
    int old = 0;//переменная старого эпика, чтобы удалить его, когда перезаписываем его с новыми данными
    EpicAction epicAction = new EpicAction();

    public SubTask createSubTask(String name, String description, String status, int epicId) {// как и эпик

        SubTask subTaskNew = new SubTask(name, description, status, epicId);
        subTaskNew.setTaskId(Manager.newId());

        return subTaskNew;
    }

    public void updateSubTask(String name, String description, String status, int epicId) {

        getWork(name, description, status, epicId);
    }

    public ArrayList<String> getSubTaskList() {//печать списка сабтасков

        ArrayList<String> list = new ArrayList<>();

        for (int i = 0; i < Manager.id; i++) {

            SubTask subTask = getSubTaskById(i);

            if (subTask != null) {

                list.add("SubTask id: " + subTask.getTaskId() + " SubTask name: " + subTask.getName());
            }
        }
        return list;
    }

    public SubTask getSubTaskById(int i) {//как и эпик

        SubTask subTask;

        if (Manager.subTaskList.containsKey(i)) {

            subTask = Manager.subTaskList.get(i);

            return subTask;
        } else {
            return null;
        }
    }

    public HashMap<Integer, String> getSubTasksByEpic(int i) {// достаем список сабтасков конкретного эпика

        Epic epic;
        HashMap<Integer, String> subTasks;

        if (Manager.epicList.containsKey(i)) {

            epic = Manager.epicList.get(i);
            subTasks = epic.getSubTasks();

            return subTasks;
        } else {
            return null;
        }
    }

    public void getWork(String name, String description, String status, int epicId) {

        for (int i = 0; i < Manager.id; i++) {//идем по всем id и ищем эпики
            HashMap<Integer, String> subTasks = getSubTasksByEpic(i);
            Epic epic = epicAction.getEpicById(i);


            if (epic != null && epic.getTaskId() == epicId) {

                findEpic(epic, subTasks);
            }
        }
        Manager.epicList.remove(old);//удалили старый, чтобы не захламлял мапу хранилище, новый сейчас в newEpic

        if (!newSubTasks.isEmpty()) {//смотрим, передались ли сабтаски(есть ли они у эпика)

            newEpic.setTaskId(Manager.newId());//тут сразу задаем новый id эпику, чтобы он писался 1 на все сабтакси
            findSubTasks(name, description, status);
            checkStatus();//проверяем статус после обновления
            rewriteEpic();//переписываем эпик(новый закидываем, старый - удаляем)
        } else if (newEpic != null) {//если эпик есть, а сабтасков нет - это его первый сабтаск

            newEpic.setTaskId(Manager.newId());
            subTask = createSubTask(name, description, status, newEpic.getTaskId());
            statusNew = subTask.getStatus();
            putNewSubTasks();
            rewriteEpic();
        }

    }

    public void findSubTasks(String name, String description, String status) {

        subTask = createSubTask(name, description, status, newEpic.getTaskId());
        statusNew = subTask.getStatus();

        for (int j = 0; j < Manager.id; j++) {//перебираем все id

            if (newSubTasks.containsKey(j)) {//раз этот метод работает, то мы
                // получили обновление в эпик, значит надо везде обновить его id, как и у сабтасков из-за этого
                SubTask updatingSubTask = Manager.subTaskList.get(j);
                if (old == updatingSubTask.getEpicId()) {
                    updateSubTasks(j);
                }
            } else if (subTask.getTaskId() == j) {//если сабтаски есть, но это не один из них - записываем

                putNewSubTasks();
            }
        }
    }

    public void updateSubTasks(int j) {

        SubTask thisSubTask = getSubTaskById(j);//тащим из общего хранилища, ссылки одинаковые

        if (subTask.getDescription().equals(thisSubTask.getDescription())) {//сравниваем описание сабтаска с тем, что
            // лежал в эпике, после чего перезаписываем, так же обновляем все, что связано (название у задачи может
            // быть одинаковым и это вызовет баг, например "подмести" и "подмести", а вот описание - это уточнение и
            // оно уже будет более раскрытым, например "подмести в комнате" и "подмести на кухне", хеш и
            // переопределенный eqauls не помогут, т.к. они будут составляться из уникального для каждого объекта id)

            newSubTask = subTask;

        } else {

            newSubTask = new SubTask(thisSubTask.getName(), thisSubTask.getDescription(),
                    thisSubTask.getStatus(), newEpic.getTaskId()); //скопировал все, дал новый id и закинул актуальный
            // id эпика
            newSubTask.setTaskId(Manager.newId());
        }

        newSubTasks.put(newSubTask.getTaskId(), newSubTask.getName());//обновил в мапе для эпика и общей мапе
        newSubTasks.remove(j);
        Manager.subTaskList.put(newSubTask.getTaskId(), newSubTask);
        Manager.subTaskList.remove(j);
    }

    public void checkStatus() {

        for (int k = 0; k < Manager.id; k++) {//проверка статусов после обновления по всем id

            if (newSubTasks != null && newSubTasks.containsKey(k)) {// ищем те сабтаски, что есть в обновленном эпике

                subTask = getSubTaskById(k);//т.к. перезапись идет сразу и в общаке, цепляем оттуда сабтаск

                if (subTask.getStatus().equals("IN_PROGRESS")) {//если хотя бы 1 сабтаск в процессе - эпик будет в процессе
                    statusNew = "IN_PROGRESS";

                } else if (subTask.getStatus().equals("DONE")) {//как только все сабтаски вернут done - епик будет выполнен
                    statusNew = "DONE";
                }
            } else {
                statusNew = "NEW";
            }
        }
    }

    public void findEpic(Epic epic, HashMap<Integer, String> subTasks) {//ищем переданный эпик
//разбить на вывод эпика и отдельно его списка сабтасков
        newEpic = epic;
        newSubTasks = subTasks;
        old = epic.getTaskId();
    }

    public void putNewSubTasks() {

        newSubTasks.put(subTask.getTaskId(), subTask.getName());
        Manager.subTaskList.put(subTask.getTaskId(), subTask);

    }

    public void rewriteEpic() {

        newEpic.setStatus(statusNew);
        newEpic.setSubTasks(newSubTasks);
        Manager.epicList.put(newEpic.getTaskId(), newEpic);
        Manager.epicList.remove(old);
    }

    public void deleteSubTaskById(int i) {//вернись после переработки эпика

        subTask = Manager.subTaskList.get(i);
        HashMap<Integer, String> subTasks = getSubTasksByEpic(subTask.getEpicId());
        Epic epic = epicAction.getEpicById(subTask.getEpicId());

        findEpic(epic, subTasks);
        Manager.epicList.remove(old);
        if (!newSubTasks.isEmpty()) {

            newEpic.setTaskId(Manager.newId());

            for (int j = 0; j < Manager.id; j++) {//перебираем все id

                if (newSubTasks.containsKey(j)) {

                    SubTask updatingSubTask = Manager.subTaskList.get(j);

                    if (old == updatingSubTask.getEpicId()) {//как в updateSubTasks, только удаляем

                        SubTask thisSubTask = getSubTaskById(j);

                        if (subTask.getDescription().equals(thisSubTask.getDescription())) {

                            newSubTasks.remove(j);
                            Manager.subTaskList.remove(j);
                        } else {

                            newSubTask = new SubTask(thisSubTask.getName(), thisSubTask.getDescription(),
                                    thisSubTask.getStatus(), newEpic.getTaskId());
                            newSubTask.setTaskId(Manager.newId());
                        }
                        newSubTasks.put(newSubTask.getTaskId(), newSubTask.getName());
                        newSubTasks.remove(j);
                        Manager.subTaskList.put(newSubTask.getTaskId(), newSubTask);
                        Manager.subTaskList.remove(j);
                    }
                }
            }
        }
        checkStatus();
        rewriteEpic();
    }
}

