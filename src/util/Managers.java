package util;

import history.InMemoryHistoryManager;
import manager.InMemoryTaskManager;
import manager.TaskManager;


public class Managers {

    public TaskManager taskManager = getDefault();// работа таскменеджера происходит через менеджера

    //методы getDefault() и getDefaultHistory() были изначально реализованы, теперь тут только они.
    public TaskManager getDefault() {

        return new InMemoryTaskManager();
    }

    public static InMemoryHistoryManager getDefaultHistory() {// в соответствии с ТЗ метод статичный, чтоюы вызвать
        // его в таскменеджере и там хранить хисторименеджера

        return new InMemoryHistoryManager();
    }

}
