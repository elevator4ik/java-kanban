package manager;

import history.HistoryManager;
import modul.Epic;
import modul.Status;
import modul.SubTask;
import modul.Task;
import util.Managers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;


public class InMemoryTaskManager implements TaskManager {

    HistoryManager historyManager = Managers.getDefaultHistory();// в соответствии с ТЗ
    HashMap<Integer, Epic> epicList = new HashMap<>();
    HashMap<Integer, Task> taskList = new HashMap<>();
    HashMap<Integer, SubTask> subTaskList = new HashMap<>();
    private int id = 0;// счетчик для id, постоянно увеличивается на 1, где бы не создавался id.


    private int newId() {//метод увеличения счетчика

        int newId = id;

        id++;
        return newId;
    }

    @Override
    public void addEpic(Epic epic) {//добавляем полученному из вне эпику id и пишем в мапу

        epic.setTaskId(newId());

        epicList.put(epic.getTaskId(), epic);
        historyManager.add(epic);
    }

    @Override
    public void addTask(Task task) {// то же что и эпик

        task.setTaskId(newId());

        taskList.put(task.getTaskId(), task);
        historyManager.add(task);
    }

    @Override
    public void addSubTask(SubTask subTask) {// то же что и эпик, только пишем еще и в эпик

        subTask.setTaskId(newId());

        updateSubTask(subTask);
    }

    @Override
    public void updateTask(Task task) {//обновляем таск


        if (taskList.containsKey(task.getTaskId())) {

            taskList.put(task.getTaskId(), task);
            historyManager.add(task);
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) {//обновление сабтаска соправождается обновлением эпика

        Status status;
        Epic epic = getEpicById(subTask.getEpicId());
        List<Integer> subTasks = epic.getSubTasks();

        historyManager.add(epic);//не могу дочухать сам: стоит ли оставить просмотр эпика в очереди первым или же
        // лучше сначала записать просмотр сабтаска? По сути, логично же, что сначала открывают эпик, а потом уже в него
        // добавляют сабтаск?
        historyManager.add(subTask);
        if (subTasks != null) {
            if (subTasks.contains(subTask.getTaskId())) {//проверяем, есть ли в сабтасках эпика уже такой сабтаск
                subTasks.remove((Integer) subTask.getTaskId());//удаляем, чтобы не было дублей
            }
            putNewSubTask(subTask, subTasks);

            status = Status.valueOf(checkStatus(subTasks));

            updateEpic(epic, subTasks, status);

        } else {

            subTasks = new ArrayList<>();

            putNewSubTask(subTask, subTasks);

            status = subTask.getStatus();

            updateEpic(epic, subTasks, status);
        }

    }

    @Override
    public void deleteSubTaskById(int i) {//удаление по id из мапы, принцип тот же, что и при добавлении.

        Status status;
        SubTask subTask = getSubTaskById(i);
        Epic epic = getEpicById(subTask.getEpicId());
        List<Integer> subTasks = epic.getSubTasks();

        historyManager.add(epic);
        historyManager.add(subTask);//при удалении так же смотрим задачи, поэтому записываем их в историю.

        if (subTasks != null) {

            subTaskList.remove(i);
            subTasks.remove((Integer) i);

            status = Status.valueOf(checkStatus(subTasks));

            updateEpic(epic, subTasks, status);
        }
    }

    private void putNewSubTask(SubTask subTask, List<Integer> subTasks) {// добавляем сабтаск в общую мапу и в
        // список сабтасков эпика

        subTasks.add(subTask.getTaskId());
        subTaskList.remove(subTask.getTaskId());
        subTaskList.put(subTask.getTaskId(), subTask);
    }

    @Override
    public void updateEpic(Epic epic, List<Integer> subTasks, Status statusNew) { //переписываем эпик, НО тут не
        // записываем просмотр эпика, т.к. всегда перед вызовом этого метода он просматривается заранее.

        epic.setStatus(statusNew);
        epic.setSubTasks(subTasks);
        epicList.put(epic.getTaskId(), epic);
    }

    private String checkStatus(List<Integer> subTasks) { //проверяем статус эпика и сабтасков после обновления

        String status;
        int statNew = 0;//каунтеры для сабтасков с опреденными статусами
        int statDone = 0;

        if (!subTasks.isEmpty()) {
            for (int i : subTasks) {

                SubTask subTask = getSubTaskById(i);
                Status subStatus = subTask.getStatus();

                if (subStatus == Status.DONE) {//считаем все DONE

                    statDone++;
                } else if (subStatus == Status.NEW) {//считаем все NEW

                    statNew++;
                }
            }
            if (statDone == subTasks.size()) {

                status = "DONE";
            } else if (statNew == subTasks.size()) {

                status = "NEW";
            } else {//каунтер и проверки на IN_PROGRESS излишни

                status = "IN_PROGRESS";
            }
        } else {// если сабтаски отсутствуют у эпика — статус меняется на NEW

            status = "NEW";
        }
        return status;
    }

    @Override
    public void deleteEpicList() {//удаление целиком мапы

        epicList.clear();
        subTaskList.clear();//если удаляем все эпики, то и сабтаски с ними, проверять статусы не у чего.
    }

    @Override
    public void deleteTaskList() {//удаление целиком мапы

        taskList.clear();
    }

    @Override
    public void deleteSubTaskList() {//Удаление целиком мапы
        // Комментарий услышан, но есть возражение: зачем создавать целую новую мапу и пускать лишний цикл, если мы
        // удаляем совсем все сабтаски? Раз нет сабтасков то и все эпики должны быть NEW. Я считаю, что тут мы избегаем
        // избыточных действий и бесполезной загрузки памяти, но проверку на null добавил. В контексте данной программы,
        // это конечно смешно звучит, но в контексте реальных задач может ускорить быстродействие.

        subTaskList.clear();

        for (int i : epicList.keySet()) {//идем по id эпиков и меняем статус на NEW
            Epic epic = epicList.get(i);
            if (epic == null) continue;
            updateEpic(epic, null, Status.NEW);//вместо списка сабтасков - null, как будто только объявили эпик
        }
    }

    @Override
    public void deleteEpicById(int i) {//удаление эпика по id из мапы и всех сабтасков к нему относящихся.

        Epic epic = epicList.get(i);
        historyManager.add(epic);

        List<Integer> subTasks = epic.getSubTasks();
        for (int j : subTasks) {//ищем все сабтаски эпика и удаляем их

            subTaskList.remove(j);
        }
        epicList.remove(i);
    }

    @Override
    public void deleteTaskById(int i) {//удаление таска по id из мапы

        Task task = taskList.get(i);//достаем, чтобы записать в историю просмотров
        historyManager.add(task);
        taskList.remove(i);
    }

    @Override
    public ArrayList<SubTask> getSubTaskList() {//вывод списка сабтасков

        Collection<SubTask> values = subTaskList.values();
        ArrayList<SubTask> list;
        list = new ArrayList<>(values);

        return list;
    }

    @Override
    public ArrayList<Task> getTaskList() {//выводим все таски

        Collection<Task> values = taskList.values();
        ArrayList<Task> list;
        list = new ArrayList<>(values);

        return list;
    }

    @Override
    public ArrayList<Epic> getEpicList() {//получение списка эпиков

        Collection<Epic> values = epicList.values();
        ArrayList<Epic> list;
        list = new ArrayList<>(values);

        return list;
    }

    @Override
    public SubTask getSubTaskById(int i) {//сабтаск по id

        return subTaskList.get(i);
    }

    @Override
    public Task getTaskById(int i) {//таск по id

        return taskList.get(i);
    }

    @Override
    public Epic getEpicById(int i) {//эпик по id

        return epicList.get(i);
    }

    @Override
    public List<SubTask> getEpicSubTasks(int i) {//список сабтасков эпика, не используется, но нужен по ТЗ

        Epic epic = getEpicById(i);
        List<Integer> subTasks = epic.getSubTasks();
        List<SubTask> list = new ArrayList<>();

        for (int j : subTasks) {//пишем все сабтаски в список, который возвращается методом

            SubTask subTask = getSubTaskById(j);
            list.add(subTask);
        }

        return list;
    }

    public List<Task> getHistory() {

        return historyManager.getHistory();
    }
}