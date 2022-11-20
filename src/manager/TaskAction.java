package manager;

import modul.Task;

import java.util.ArrayList;

public class TaskAction {


    public Task createTask(String name, String description) {// как и эпик

        return new Task(name, description, "new");
    }

    public void updateTask(String status, int taskId) {// как и эпик, но еще и с проверкой на изменение статуса

        Task newTask;

        if (Manager.taskList.containsKey(taskId)) {//обновление статуса уже имеющегося таска

            newTask = Manager.taskList.get(taskId);
            newTask.setStatus(status);

            deleteTaskById(taskId);
            Manager.taskList.put(taskId, newTask);
        }
    }

    public ArrayList<String> getTaskList() {//как и эпик

        ArrayList<String> list = new ArrayList<>();

        for (int i = 0; i < Manager.id; i++) {

            Task task = getTaskById(i);

            if (task != null) {

                list.add("Task id: " + task.getTaskId() + " Task name: " + task.getName());
            }
        }
        return list;
    }

    public Task getTaskById(int i) {//как и эпик

        Task task;

        if (Manager.taskList.containsKey(i)) {

            task = Manager.taskList.get(i);

            return task;
        } else {
            return null;
        }
    }

    public void deleteTaskById(int i) {

        Manager.taskList.remove(i);
    }
}
