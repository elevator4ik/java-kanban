package manager;

import history.InMemoryHistoryManager;


public class Managers {

    public static TaskManager getDefault() {

        return getHttpTaskManager();
    }

    public static InMemoryHistoryManager getDefaultHistory() {

        return new InMemoryHistoryManager();
    }
    public static TaskManager getFileBackedTasksManager() {

        return new FileBackedTasksManager();
    }
    public static TaskManager getInMemoryTasksManager() {

        return new InMemoryTaskManager();
    }

    public static HttpTaskManager getHttpTaskManager() {

        return new HttpTaskManager();
    }
}
