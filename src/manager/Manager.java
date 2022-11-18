package manager;

import modul.Epic;
import modul.SubTask;
import modul.Task;

import java.util.HashMap;

public class Manager {
    // В ТЗ сказано, что менеджер хранит в себе информацию по всем типам задач. Статики для постоянного хранения, пока
// программа работает
    HashMap<Integer, Epic> epicList = new HashMap<>();
    HashMap<Integer, Task> taskList = new HashMap<>();
    HashMap<Integer, SubTask> subTaskList = new HashMap<>();
    static int id = 0;// счетчик для id, постоянно увеличивается на 1, где бы не создавался id
    HashMap<Integer, String> newSubTasks;
    Epic newEpic;
    SubTask subTask;
    SubTask newSubTask;
    String statusNew = "";
    int old = 0;//переменная старого эпика, чтобы удалить его, когда перезаписываем его с новыми данными

    public static int newId() {//метод увеличения счетчика

        int newId = id;

        id++;
        return newId;
    }

    public Epic createEpic(String name, String description) {// создание эпика, в соответствии с ТЗ

        return new Epic(name, description, Manager.newId(), "new");
    }

    public Task createTask(String name, String description) {// как и эпик

        return new Task(name, description, Manager.newId(), "new");
    }

    public SubTask createSubTask(String name, String description, String status, int epicId) {// как и эпик

        return new SubTask(name, description, Manager.newId(), status, epicId);
    }

    public void updateEpicList(String name, String description) {//занесение в мапу, где он хранится

        Epic epic = createEpic(name, description);

        epicList.put(epic.getTaskId(), epic);
    }

    public void updateTask(String name, String description, String status, int taskId) {// как и эпик, но еще и с
        // проверкой на изменение статуса

        Task task;
        Task newTask;

        if (taskList.containsKey(taskId)) {//обновление статуса уже имеющегося таска

            task = taskList.get(taskId);
            newTask = createTask(task.getName(), task.getDescription());
            newTask.setStatus(status);

            deleteTaskById(taskId);
            taskList.put(newTask.getTaskId(), newTask);
        } else if (taskId == -1) {//передаем -1, если это новый таск

            task = createTask(name, description);

            taskList.put(task.getTaskId(), task);
        }
    }

    public void updateSubTask(String name, String description, String status, int epicId) {

        getWork(name, description, status, epicId);
    }

    public String getEpicList() {//получение списка эпиков

        StringBuilder result = new StringBuilder();//StringBuilder, в отличие от String, модифицируемый, что удобнее

        for (int i = 0; i < id; i++) {// счетчик id всегда актуален и обновляется, проверяем по всем созданным id

            Epic epic = getEpicById(i);

            if (epic != null) {

                result.append("id: ").append(i).append(", ").append(epic).append("\n");
            }
        }
        return result.toString();
    }

    public String getTaskList() {//как и эпик

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < id; i++) {

            Task task = getTaskById(i);

            if (task != null) {

                result.append("id: ").append(i).append(", ").append(task).append("\n");
            }
        }
        return result.toString();
    }

    public String getSubTaskList() {//как и эпик

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < id; i++) {

            SubTask subTask = getSubTaskById(i);

            if (subTask != null) {

                result.append("id: ").append(i).append(", ").append(subTask).append("\n");
            }
        }
        return result.toString();
    }

    public Epic getEpicById(int i) {//достаем из мапы конкретный эпик

        Epic epic;

        if (epicList.containsKey(i)) {//смотрим, есть ли такой ключ по каждому id

            epic = epicList.get(i);

            return epic;
        } else {
            return null;
        }
    }

    public Task getTaskById(int i) {//как и эпик

        Task task;

        if (taskList.containsKey(i)) {

            task = taskList.get(i);

            return task;
        } else {
            return null;
        }
    }

    public SubTask getSubTaskById(int i) {//как и эпик

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

    public void deleteEpicList() {//удаление целиком мапы

        epicList.clear();
    }

    public void deleteTaskList() {

        taskList.clear();
    }

    public void deleteSubTaskList() {

        subTaskList.clear();
    }

    public void deleteEpicById(int i) {//удаление по id из мапы

        epicList.remove(i);
    }

    public void deleteTaskById(int i) {

        taskList.remove(i);
    }

    public void deleteSubTaskById(int i) {

        subTaskList.remove(i);
    }

    public void getWork(String name, String description, String status, int epicId) {

        for (int i = 0; i < id; i++) {//идем по всем id и ищем эпики
            HashMap<Integer, String> subTasks;
            Epic epic;
            epic = getEpicById(i);
            subTasks = getSubTasksByEpic(i);
            if (epic != null && epic.getTaskId() == epicId) {

                findEpic(epic, subTasks);
            }
        }
        deleteEpicById(old);//удалили старый, чтобы не захламлял мапу хранилище, новый сейчас в newEpic

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

    public void findEpic(Epic epic, HashMap<Integer, String> subTasks) {//ищем переданный эпик

        newEpic = epic;
        newSubTasks = subTasks;
        old = epic.getTaskId();
    }

    public void findSubTasks(String name, String description, String status) {

        subTask = createSubTask(name, description, status, newEpic.getTaskId());
        statusNew = subTask.getStatus();

        for (int j = 0; j < id; j++) {//перебираем все id

            if (newSubTasks.containsKey(j)) {//раз этот метод работает, то мы
                // получили обновление в эпик, значит надо везде обновить его id, как и у сабтасков из-за этого
                SubTask updatingSubTask = subTaskList.get(j);
                if (old == updatingSubTask.getEpicId()) {
                    updateSubTasks(j);
                }
            } else if (subTask.getTaskId() == j) {//если сабтаски есть, но это не один из них - записываем

                putNewSubTasks();
            }
        }
    }

    public void putNewSubTasks() {

        newSubTasks.put(subTask.getTaskId(), subTask.getName());
        subTaskList.put(subTask.getTaskId(), subTask);

    }

    public void updateSubTasks(int j) {

        SubTask thisSubTask = getSubTaskById(j);//тащим из общего хранилища, ссылки одинаковые

        if (subTask.getDescription().equals(thisSubTask.getDescription())) {//сравниваем описание сабтаска с тем, что
            // лежал в эпике, после чего перезаписываем, так же обновляем все, что связано (название у задачи может
            // быть одинаковым и это вызовет баг, например "подмести" и "подмести", а вот описание - это уточнение и
            // оно уже будет более раскрытым, например "подмести в комнате" и "подмести на кухне", хеш и
            // переопределенный eqauls не помогут, т.к. они будет составляться из уникального для каждого объекта id)

            newSubTask = subTask;

        } else {

            newSubTask = new SubTask(thisSubTask.getName(), thisSubTask.getDescription(), Manager.newId(),
                    thisSubTask.getStatus(), newEpic.getTaskId()); //скопировал все, дал новый id и закинул актуальный
            // id эпика
        }

        newSubTasks.put(newSubTask.getTaskId(), newSubTask.getName());//обновил в мапе для эпика и общей мапе
        newSubTasks.remove(j);
        subTaskList.put(newSubTask.getTaskId(), newSubTask);
        deleteSubTaskById(j);
    }

    public void checkStatus() {

        for (int k = 0; k < id; k++) {//проверка статусов после обновления по всем id

            if (newSubTasks != null && newSubTasks.containsKey(k)) {// ищем те сабтаски, что есть в обновленном эпике

                subTask = getSubTaskById(k);//т.к. перезапись идет сразу и в общаке, цепляем оттуда сабтаск

                if (subTask.getStatus().equals("IN_PROGRESS")) {//если хотя бы 1 сабтаск в процессе - эпик будет в процессе
                    statusNew = "IN_PROGRESS";

                } else if (subTask.getStatus().equals("DONE")) {//как только все сабтаски вернут done - епик будет выполнен
                    statusNew = "DONE";
                }
            }
        }
    }

    public void rewriteEpic() {

        newEpic.setStatus(statusNew);
        newEpic.setSubTasks(newSubTasks);
        epicList.put(newEpic.getTaskId(), newEpic);
        deleteEpicById(old);
    }
}