package tests.history;


import history.HistoryManager;
import history.InMemoryHistoryManager;
import modul.SubTask;
import modul.Task;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static modul.Status.NEW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InMemoryHistoryManagerTest {
    HistoryManager inMemoryHistoryManager;
    static Task task;
    static SubTask subTask;

    @BeforeAll
    static void start() {
        task = new Task("Testing addTask", "Test addTask description", NEW, 10,
                LocalDateTime.of(2023, 1, 1, 22, 0));
        task.setTaskId(0);

        subTask = new SubTask("Test addSubTask", "Test addSubTask description", NEW, 10,
                LocalDateTime.of(2023, 1, 1, 22, 15), 20);
        subTask.setTaskId(1);
    }

    @BeforeEach
    void creating() {
        inMemoryHistoryManager = new InMemoryHistoryManager();

    }

    @Test
    void getHistoryOfVoidList() {

        List<Task> list = inMemoryHistoryManager.getHistory();

        assertNotNull(list, "история не возвращаются.");
        assertEquals(0, list.size(), "Неверное количество задач.");
        assertEquals("[]", list.toString(), "Неверный вывод задач.");
    }

    @Test
    void addInVoidList_GetHistoryTest_AddToNotEmptyListAndRemoveDoublicate() {//Проверяем сразу добавление в пустой
        // список и уже с записью, работу метода getHistory() в нормальных условиях и т.к. при добавлении дубликата в
        // список, должен оставаться только последний — сразу проверяем перезапись(удаление старого)

        inMemoryHistoryManager.add(task);
        List<Task> list = inMemoryHistoryManager.getHistory();
        Task savedTask = list.get(0);

        assertNotNull(list, "история не возвращаются.");
        assertEquals(1, list.size(), "Неверное количество задач.");
        assertEquals("[0,Testing addTask,NEW,Test addTask description,10,2023-01-01T22:00,\n]",
                list.toString(), "Неверный вывод задач.");
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        Task secondTask = new Task("Testing addTask", "Test addTask description", NEW, 10,
                LocalDateTime.of(2023, 1, 1, 23, 0));
        secondTask.setTaskId(0);

        inMemoryHistoryManager.add(secondTask);
        List<Task> newList = inMemoryHistoryManager.getHistory();
        Task savedSecondTask = newList.get(0);

        assertNotNull(newList, "история не возвращаются.");
        assertEquals(1, newList.size(), "Неверное количество задач.");
        assertEquals("[0,Testing addTask,NEW,Test addTask description,10,2023-01-01T23:00,\n]",
                newList.toString(), "Неверный вывод задач.");
        assertNotNull(savedSecondTask, "Задача не найдена.");
        assertEquals(secondTask, savedSecondTask, "Задачи не совпадают.");
    }

    @Test
    void removeFromBegin_Middle_EndOfList() {
        inMemoryHistoryManager.add(task);
        inMemoryHistoryManager.add(subTask);

        Task secondTask = new Task("Testing addTask", "Test addTask description", NEW, 10,
                LocalDateTime.of(2023, 1, 1, 23, 0));
        secondTask.setTaskId(2);
        inMemoryHistoryManager.add(secondTask);

        SubTask secondSubTask = new SubTask("Testing addSubTask", "Test addSubTask description", NEW, 10,
                LocalDateTime.of(2023, 1, 1, 22, 30), 20);
        secondSubTask.setTaskId(3);
        inMemoryHistoryManager.add(secondSubTask);

        inMemoryHistoryManager.remove(0);//удаление из начала списка

        List<Task> startList = inMemoryHistoryManager.getHistory();

        assertNotNull(startList, "история не возвращаются.");
        assertEquals(3, startList.size(), "Неверное количество задач.");
        assertEquals("[1,Test addSubTask,NEW,Test addSubTask description,10,2023-01-01T22:15,20,\n" +
                        ", 2,Testing addTask,NEW,Test addTask description,10,2023-01-01T23:00,\n" +
                        ", 3,Testing addSubTask,NEW,Test addSubTask description,10,2023-01-01T22:30,20,\n]",
                startList.toString(), "Неверный вывод задач.");

        inMemoryHistoryManager.remove(2);//удаление из середины списка

        List<Task> middleList = inMemoryHistoryManager.getHistory();

        assertNotNull(middleList, "история не возвращаются.");
        assertEquals(2, middleList.size(), "Неверное количество задач.");
        assertEquals("[1,Test addSubTask,NEW,Test addSubTask description,10,2023-01-01T22:15,20,\n" +
                        ", 3,Testing addSubTask,NEW,Test addSubTask description,10,2023-01-01T22:30,20,\n]",
                middleList.toString(), "Неверный вывод задач.");

        inMemoryHistoryManager.remove(3);//удаление из конца списка

        List<Task> endList = inMemoryHistoryManager.getHistory();

        assertNotNull(endList, "история не возвращаются.");
        assertEquals(1, endList.size(), "Неверное количество задач.");
        assertEquals("[1,Test addSubTask,NEW,Test addSubTask description,10,2023-01-01T22:15,20,\n]",
                endList.toString(), "Неверный вывод задач.");
    }
}