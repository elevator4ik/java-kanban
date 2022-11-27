package history;

import modul.Task;


import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    static final int MAX_LENGTH = 10;
    List<Task> history = new ArrayList<>();

    @Override
    public void add(Task task) {

        if (history.size()<MAX_LENGTH) {
            history.add(task);
        } else {
            history.remove(0);
            history.add(task);
        }
    }

    @Override
    public List<Task> getHistory() {

        return history;
    }
}
