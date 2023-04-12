package manager;

import history.InMemoryHistoryManager;



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
