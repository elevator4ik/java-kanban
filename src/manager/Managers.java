package manager;

import com.google.gson.GsonBuilder;
import history.InMemoryHistoryManager;

import java.time.LocalDateTime;


public class Managers {

    public static TaskManager getDefault() {

        return getHttpTaskServer();
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

    public static TaskManager getHttpTaskServer() {

        return new HttpTaskServer();
    }
}
