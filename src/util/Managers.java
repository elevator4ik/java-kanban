package util;

import history.InMemoryHistoryManager;
import manager.FileBackedTasksManager;
import manager.InMemoryTaskManager;
import manager.TaskManager;


public class Managers {

    public static TaskManager getDefault() {

        return new InMemoryTaskManager();
    }

    public static InMemoryHistoryManager getDefaultHistory() {

        return new InMemoryHistoryManager();
    }
    public static TaskManager getFileBackedTasksManager() {

        return new FileBackedTasksManager();
    }
}
