package history;

import modul.Task;


import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    static final int MAX_LENGTH = 10;
    List<Task> history = new ArrayList<>();


    @Override
    public void add(Task task) {

        if (history.size() < MAX_LENGTH) {
            history.add(task);
        } else {

            history.subList(0, 1).clear();
            history.add(task);// можно попробовать так - удаление через саблист, не уверен быстрее ли будет, но это еще
            // один из вариантов. Есть еще вариант с постоянным временем удаления первого элемента, а именно - метод
            // removeFirst()(в гугле меня пока не забанили), но с LinkedList я пока не готов разбираться, дедлайны
            // сгорели еще неделю назад
        }
    }

    @Override
    public List<Task> getHistory() {

        return history;
    }
}
