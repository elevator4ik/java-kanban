package manager;

import history.HistoryManager;
import modul.*;

import java.time.LocalDateTime;
import java.util.*;


public class InMemoryTaskManager implements TaskManager {

    HistoryManager historyManager = Managers.getDefaultHistory();
    HashMap<Integer, Epic> epicList = new HashMap<>();
    HashMap<Integer, Task> taskList = new HashMap<>();
    HashMap<Integer, SubTask> subTaskList = new HashMap<>();

    TreeSet<Task> sortetTasks = new TreeSet<>();
    Status status;

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

    }

    @Override
    public void addTask(Task task) {// то же что и эпик

        if (checkCrossing(task)) {
            System.out.println("Задачи пересекаются");
        } else {
            task.setTaskId(newId());
            taskList.put(task.getTaskId(), task);
            addToSortetTasks(task);
        }
    }

    @Override
    public void addSubTask(SubTask subTask) {// то же что и эпик, только пишем еще и в эпик

        if (checkCrossing(subTask)) {
            System.out.println("Задачи пересекаются");
        } else {
            subTask.setTaskId(newId());
            updatingSubTask(subTask);
        }
    }

    @Override
    public void updateTask(Task task) {//обновляем таск

        if (taskList.containsKey(task.getTaskId())) {
            if (checkCrossing(task)) {
                System.out.println("Задачи пересекаются");
            } else {
                addToSortetTasks(task);
                taskList.put(task.getTaskId(), task);
            }
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) {//обновление сабтаска соправождается обновлением эпика

        if (subTaskList.containsKey(subTask.getTaskId())) {
            if (checkCrossing(subTask)) {
                System.out.println("Задачи пересекаются");
            } else {
                updatingSubTask(subTask);
            }
        }
    }

    public void updatingSubTask(SubTask subTask) {//чтобы не производить проверку пересечения при вызове updateSubTask
        // из addSubTask, вынес тело в отдельный метод.

        Epic epic = getEpicById(subTask.getEpicId());
        List<Integer> subTasks = epic.getSubTasks();


        if (subTasks != null) {
            if (subTasks.contains(subTask.getTaskId())) {//проверяем, есть ли в сабтасках эпика уже такой сабтаск
                subTasks.remove((Integer) subTask.getTaskId());//удаляем, чтобы не было дублей
                epic.minusDuration(subTaskList, subTask);//уменьшаем продолжительность эпика
            }
            putNewSubTask(subTask, subTasks);

            status = checkStatus(subTasks);

        } else {

            subTasks = new ArrayList<>();

            putNewSubTask(subTask, subTasks);

            status = subTask.getStatus();

        }
        rewriteEpic(epic, subTasks, status);
        epic.plusDuration(subTask);//увеличиваем продолжительность эпика
    }


    @Override
    public void deleteSubTaskById(int i) {//удаление по id из мапы, принцип тот же, что и при добавлении.

        SubTask subTask = getSubTaskById(i);
        Epic epic = getEpicById(subTask.getEpicId());
        List<Integer> subTasks = epic.getSubTasks();
        historyManager.remove(i);

        if (subTasks != null) {

            subTaskList.remove(i);
            subTasks.remove((Integer) i);

            status = checkStatus(subTasks);

            rewriteEpic(epic, subTasks, status);
            epic.minusDuration(subTaskList, subTask);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epicList.containsKey(epic.getTaskId())) {
            List<Integer> thisSubTasks = epic.getSubTasks();
            rewriteEpic(epic, thisSubTasks, checkStatus(thisSubTasks));
        }
    }

    @Override
    public void deleteEpicList() {//удаление целиком мапы

        if (subTaskList != null) {
            for (int i : subTaskList.keySet()) {

                SubTask s = subTaskList.get(i);
                removewFromSortetTasks(s);
                historyManager.remove(i);
            }
            subTaskList.clear();//если удаляем все эпики, то и сабтаски с ними, проверять статусы не у чего.
        }
        if (!historyManager.getHistory().isEmpty()) {
            for (int i : epicList.keySet()) {
                historyManager.remove(i);
            }
        }
        epicList.clear();
    }

    @Override
    public void deleteTaskList() {//удаление целиком мапы

        if (!historyManager.getHistory().isEmpty()) {
            for (int i : taskList.keySet()) {

                deleteTaskById(i);
            }
        }
        taskList.clear();
    }

    @Override
    public void deleteSubTaskList() {//Удаление целиком мапы

        if (!historyManager.getHistory().isEmpty()) {
            for (int i : subTaskList.keySet()) {

                SubTask s = subTaskList.get(i);
                Epic e = epicList.get(s.getEpicId());
                e.minusDuration(subTaskList, s);
                removewFromSortetTasks(s);
                historyManager.remove(i);
            }
        }
        subTaskList.clear();

        for (int i : epicList.keySet()) {//идем по id эпиков и меняем статус на NEW
            Epic epic = epicList.get(i);
            if (epic == null) continue;
            rewriteEpic(epic, null, Status.NEW);//вместо списка сабтасков - null, как будто только объявили эпик
        }
    }

    @Override
    public void deleteEpicById(int i) {//удаление эпика по id из мапы и всех сабтасков к нему относящихся.

        Epic epic = epicList.get(i);

        historyManager.remove(i);

        List<Integer> subTasks = epic.getSubTasks();
        if (subTasks != null) {//проверяем, есть ли сабтаски у эпика
            for (int j : subTasks) {//ищем все сабтаски эпика и удаляем их

                SubTask s = subTaskList.get(j);
                removewFromSortetTasks(s);
                subTaskList.remove(j);
                historyManager.remove(j);
            }
        }
        epicList.remove(i);
    }

    @Override
    public void deleteTaskById(int i) {//удаление таска по id из мапы

        Task s = taskList.get(i);
        removewFromSortetTasks(s);
        taskList.remove(i);
        historyManager.remove(i);

    }

    @Override
    public List<SubTask> getSubTaskList() {//вывод списка сабтасков

        Collection<SubTask> values = subTaskList.values();
        List<SubTask> list;
        list = new ArrayList<>(values);

        return list;
    }

    @Override
    public List<Task> getTaskList() {//выводим все таски

        Collection<Task> values = taskList.values();
        List<Task> list;
        list = new ArrayList<>(values);

        return list;
    }

    @Override
    public List<Epic> getEpicList() {//получение списка эпиков

        Collection<Epic> values = epicList.values();
        List<Epic> list;
        list = new ArrayList<>(values);

        return list;
    }

    @Override
    public SubTask getSubTaskById(int i) {//сабтаск по id

        SubTask subTask = subTaskList.get(i);
        historyManager.add(subTask);
        return subTask;
    }

    @Override
    public Task getTaskById(int i) {//таск по id

        Task task = taskList.get(i);
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpicById(int i) {//эпик по id

        Epic epic = epicList.get(i);
        historyManager.add(epic);
        return epic;
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

    @Override
    public void readFromFile() {
        throw new ManagerSaveException();
    }


    @Override
    public void idFromFile(int newId) {//читаем из файла id и делаем на 1 больше
        id = newId + 1;
    }

    @Override
    public int getLastId() {//нужен для записи в файл
        return this.id;
    }

    @Override
    public String getPrioritizedTasks() {//оставил его для вывода списком упорядоченных задач
        StringBuilder s = new StringBuilder();
        for (Task t : sortetTasks) {
            s.append(t.toString())
                    .append("\n");
        }
        return s.toString();
    }

    @Override
    public String printFile() throws ManagerSaveException {
        throw new ManagerSaveException();
    }

    private void addToSortetTasks(Task task) {//добавление в сортированный список тасков и сабтасков
        //т.к. конструктор таски и сабтаски не дает возможности создавать их без старттйма и длительности - при
        // добавлении в список они сразу сортируются и нет необходимости добавлять в конец списка задачи без старттайма
        if (sortetTasks != null && !sortetTasks.isEmpty()) {
            removewFromSortetTasks(task);//код тот же, только в конце мы записываем новый таск/сабтаск
            sortetTasks.add(task);
        }
        sortetTasks.add(task);//игнорируем предупреждение о возможном NullPointerException, т.к. мы ожидаем первую запись
        // в только созданный трисэт и она точно произойдёт.
    }

    private void removewFromSortetTasks(Task task) {//удаление из сортированного списка тасков и сабтасков

        sortetTasks.removeIf(t -> t.getTaskId() == task.getTaskId());//т.к. поля могут меняться, то проверка на
        // идентичность через hashcode или equals может не сработать, поэтому сравниваем перебором по id
    }

    private boolean checkCrossing(Task task) {//проверяем пересечение задач

        LocalDateTime startT = task.getStartTime();
        LocalDateTime endT = task.getEndTime();
        boolean b = false;

        if (sortetTasks != null && !sortetTasks.isEmpty()) {
            for (Task t : sortetTasks) {
                if (t.getTaskId() != task.getTaskId() &&//если не переписываем уже существующую задачу
                        (endT.isAfter(t.getStartTime()) & endT.isBefore(t.getEndTime())) |//завершение попало в пересечение
                                (startT.isBefore(t.getStartTime()) & endT.isAfter(t.getEndTime())) |//существующий таск перекрыт
                                (startT.isAfter(t.getStartTime()) & startT.isBefore(t.getEndTime()))//начало попало в пересечение
                ) {
                    b = true;
                }
            }
        }
        return b;
    }

    private void putNewSubTask(SubTask subTask, List<Integer> subTasks) {// добавляем сабтаск в общую мапу и в
        // список сабтасков эпика

        subTasks.add(subTask.getTaskId());
        subTaskList.remove(subTask.getTaskId());
        subTaskList.put(subTask.getTaskId(), subTask);
        addToSortetTasks(subTask);
    }

    private void rewriteEpic(Epic epic, List<Integer> subTasks, Status statusNew) {
        epic.setStatus(statusNew);
        epic.setSubTasks(subTasks);
        epicList.put(epic.getTaskId(), epic);
    }

    private Status checkStatus(List<Integer> subTasks) { //проверяем статус эпика и сабтасков после обновления


        int statNew = 0;//каунтеры для сабтасков с опреденными статусами
        int statDone = 0;

        if (subTasks != null && !subTasks.isEmpty()) {
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

                status = Status.DONE;
            } else if (statNew == subTasks.size()) {

                status = Status.NEW;
            } else {//каунтер и проверки на IN_PROGRESS излишни

                status = Status.IN_PROGRESS;
            }
        } else {// если сабтаски отсутствуют у эпика — статус меняется на NEW

            status = Status.NEW;
        }
        return status;
    }
}