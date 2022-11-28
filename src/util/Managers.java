package util;

import history.InMemoryHistoryManager;
import manager.InMemoryTaskManager;
import manager.TaskManager;


public class Managers {

    public static TaskManager getDefault() {

        return new InMemoryTaskManager();
    }

    public static InMemoryHistoryManager getDefaultHistory() {// в соответствии с ТЗ метод статичный, чтоюы вызвать
        // его в таскменеджере и там хранить хисторименеджера

        return new InMemoryHistoryManager();
    }

}
