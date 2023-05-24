package tests.manager;

import manager.TaskManager;
import modul.Epic;
import modul.SubTask;
import modul.Task;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static modul.Status.*;
import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest {
    TaskManager taskManager;

    void addTaskForTest() {
        Task task = new Task("Testing addTask", "Test addTask description", NEW, 10,
                LocalDateTime.of(2023, 1, 1, 22, 0));

        taskManager.addTask(task);
    }

    void addSubTaskForTest() {
        addEpicForTest();
        final SubTask subTask = new SubTask("Test addSubTask", "Test addSubTask description", NEW, 10,
                LocalDateTime.of(2023, 1, 1, 22, 0), 0);

        taskManager.addSubTask(subTask);
    }

    void addEpicForTest() {
        final Epic task = new Epic("Test addEpic", "Test addEpic description", NEW);

        taskManager.addEpic(task);
    }

    @Test
    void addTaskWithVoidTask_SubTask_EpicLists() {

        Task task = new Task("Test addTask", "Test addTask description", NEW, 10,
                LocalDateTime.of(2023, 1, 1, 12, 0));

        taskManager.addTask(task);

        final Task savedTask = taskManager.getTaskById(task.getTaskId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getTaskList();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");

    }

    @Test
    void addEpicWithVoidTask_SubTask_EpicLists() {

        final Epic task = new Epic("Test addEpic", "Test addEpic description", NEW);

        taskManager.addEpic(task);

        final Epic savedTask = taskManager.getEpicById(task.getTaskId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Epic> tasks = taskManager.getEpicList();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");

    }

    @Test
    void addSubTaskWithVoidTask_SubTask_EpicLists() {

        final Epic task = new Epic("Test updateEpicAndSubTask", "Test updateEpicAndSubTask description", NEW);

        taskManager.addEpic(task);

        final SubTask subTask = new SubTask("Test addSubTask", "Test addSubTask description", NEW, 10,
                LocalDateTime.of(2023, 1, 1, 12, 0), 0);
        subTask.setTaskId(1);

        taskManager.addSubTask(subTask);

        final SubTask savedTask = taskManager.getSubTaskById(subTask.getTaskId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(subTask, savedTask, "Задачи не совпадают.");

        final List<SubTask> tasks = taskManager.getSubTaskList();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");

    }

    @Test
    void updateEpicAndSubTaskWithVoidTask_SubTask_EpicLists() {
        final Epic task = new Epic("Test updateEpicAndSubTask", "Test updateEpicAndSubTask description", NEW);

        taskManager.addEpic(task);

        final SubTask subTask = new SubTask("Test updateEpicAndSubTask", "Test updateEpicAndSubTask description", NEW, 10,
                LocalDateTime.of(2023, 1, 1, 12, 0), 0);

        taskManager.addSubTask(subTask);


        final Epic updatedEpic = taskManager.getEpicById(task.getTaskId());//проверяем перезапись эпика
        final Epic expectedEpic = new Epic("Test updateEpicAndSubTask", "Test updateEpicAndSubTask description", NEW);
        final List<Integer> epicTasks = new ArrayList<>();

        epicTasks.add(subTask.getTaskId());
        expectedEpic.setTaskId(0);
        expectedEpic.setSubTasks(epicTasks);
        expectedEpic.setStartTime(subTask.getStartTime());
        expectedEpic.setEndTime(subTask.getEndTime());
        expectedEpic.setDuration(subTask.getDuration());

        assertNotNull(updatedEpic.getSubTasks(), "Подзадачи на возвращаются.");
        assertEquals(1, updatedEpic.getSubTasks().size(), "Неверное количество подзадач.");
        assertEquals(expectedEpic.toString(), updatedEpic.toString(), "Задачи не совпадают.");// если сравнивать
        // не .toString(), то тест фейлится, хотя при раскрытии commparsion failure пишет contents are identical, у
        // тасков и сабтасков такой проблемы нет.

        final SubTask updatedSubTask = new SubTask("Test updateEpicAndSubTask", "Test updateEpicAndSubTask description", IN_PROGRESS, 10,
                LocalDateTime.of(2023, 1, 1, 11, 10), 0);
        updatedSubTask.setTaskId(1);
        taskManager.updateSubTask(updatedSubTask);
        final SubTask updatedSavedTask = taskManager.getSubTaskById(subTask.getTaskId());

        assertNotNull(updatedSavedTask, "Задача не найдена.");
        assertEquals(updatedSubTask, updatedSavedTask, "Задачи не совпадают.");

        final List<SubTask> updatedTasks = taskManager.getSubTaskList();

        assertNotNull(updatedTasks, "Задачи на возвращаются.");
        assertEquals(1, updatedTasks.size(), "Неверное количество задач.");

    }

    @Test
    void updateTaskWithVoidTask_SubTask_EpicLists() {

        final Task task = new Task("Test updateTask", "Test updateTask description", NEW, 10,
                LocalDateTime.of(2023, 1, 1, 12, 0));

        taskManager.addTask(task);

        final Task newTask = new Task("Test updateTask", "Test updateTask new description", IN_PROGRESS,
                10, LocalDateTime.of(2023, 1, 2, 13, 10));
newTask.setTaskId(0);
        taskManager.updateTask(newTask);

        final Task savedTask = taskManager.getTaskById(task.getTaskId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(newTask, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getTaskList();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");


    }

    @Test
    void deleteSubTaskByIdWithVoidTask_SubTask_EpicLists() {

        final Epic task = new Epic("Test deleteSubTaskById", "Test deleteSubTaskById description", NEW);

        taskManager.addEpic(task);

        final SubTask subTask = new SubTask("Test deleteSubTaskById", "Test deleteSubTaskById description", NEW, 10,
                LocalDateTime.of(2023, 1, 1, 12, 0), 0);

        taskManager.addSubTask(subTask);
        taskManager.deleteSubTaskById(subTask.getTaskId());

        final List<SubTask> tasks = taskManager.getSubTaskList();

        assertEquals("[]", tasks.toString(), "Задачи возвращаются.");
    }

    @Test
    void deleteEpicListWithVoidTask_SubTask_EpicLists() {
        final Epic task = new Epic("Test deleteEpicList", "Test deleteEpicList description", NEW);

        taskManager.addEpic(task);
        taskManager.deleteEpicList();
        final List<Epic> tasks = taskManager.getEpicList();

        assertEquals("[]", tasks.toString(), "Задачи возвращаются.");
    }

    @Test
    void deleteTaskListWithVoidTask_SubTask_EpicLists() {

        final Task task = new Task("Test deleteTaskList", "Test deleteTaskList description", NEW, 10,
                LocalDateTime.of(2023, 1, 1, 12, 0));

        taskManager.addTask(task);
        taskManager.deleteTaskList();
        final List<Task> tasks = taskManager.getTaskList();

        assertEquals("[]", tasks.toString(), "Задачи возвращаются.");
    }

    @Test
    void deleteSubTaskListWithVoidTask_SubTask_EpicLists() {

        final Epic task = new Epic("Test updateEpicAndSubTask", "Test updateEpicAndSubTask description", NEW);

        taskManager.addEpic(task);

        final SubTask subTask = new SubTask("Test addSubTask", "Test addSubTask description", NEW, 10,
                LocalDateTime.of(2023, 1, 1, 12, 0), 0);

        taskManager.addSubTask(subTask);
        taskManager.deleteSubTaskList();

        final List<SubTask> tasks = taskManager.getSubTaskList();

        assertEquals("[]", tasks.toString(), "Задачи возвращаются.");
    }

    @Test
    void deleteEpicByIdWithVoidTask_SubTask_EpicLists() {

        final Epic task = new Epic("Test deleteSubTaskById", "Test deleteSubTaskById description", NEW);

        taskManager.addEpic(task);
        taskManager.deleteEpicById(task.getTaskId());

        final List<Epic> tasks = taskManager.getEpicList();

        assertEquals("[]", tasks.toString(), "Задачи возвращаются.");
    }

    @Test
    void deleteTaskByIdWithVoidTask_SubTask_EpicLists() {

        final Task task = new Task("Test deleteTaskList", "Test deleteTaskList description", NEW, 10,
                LocalDateTime.of(2023, 1, 1, 12, 0));

        taskManager.addTask(task);
        taskManager.deleteTaskById(task.getTaskId());
        final List<Task> tasks = taskManager.getTaskList();

        assertEquals("[]", tasks.toString(), "Задачи возвращаются.");
    }

    @Test
    void addTaskStandart() {//для проверки стандартного поведения добавляем в начале каждого теста в список задачу.

        addTaskForTest();

        Task task = new Task("Test addTask", "Test addTask description", NEW, 10,
                LocalDateTime.of(2023, 1, 1, 12, 0));

        taskManager.addTask(task);

        final Task savedTask = taskManager.getTaskById(task.getTaskId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getTaskList();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(2, tasks.size(), "Неверное количество задач.");

    }

    @Test
    void addEpicStandart() {

        addEpicForTest();

        final Epic task = new Epic("Test addEpic", "Test addEpic description", NEW);

        taskManager.addEpic(task);

        final Epic savedTask = taskManager.getEpicById(task.getTaskId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Epic> tasks = taskManager.getEpicList();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(2, tasks.size(), "Неверное количество задач.");

    }

    @Test
    void addSubTaskStandart() {

        final Epic task = new Epic("Test updateEpicAndSubTask", "Test updateEpicAndSubTask description", NEW);

        taskManager.addEpic(task);

        addSubTaskForTest();

        final SubTask subTask = new SubTask("Test addSubTask", "Test addSubTask description", NEW, 10,
                LocalDateTime.of(2023, 1, 1, 12, 0), 0);

        taskManager.addSubTask(subTask);

        final SubTask savedTask = taskManager.getSubTaskById(subTask.getTaskId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(subTask, savedTask, "Задачи не совпадают.");

        final List<SubTask> tasks = taskManager.getSubTaskList();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(2, tasks.size(), "Неверное количество задач.");

    }

    @Test
    void updateEpicAndSubTaskStandart() {

        final Epic task = new Epic("Test updateEpicAndSubTask", "Test updateEpicAndSubTask description", NEW);

        taskManager.addEpic(task);

        addSubTaskForTest();

        final SubTask subTask = new SubTask("Test updateEpicAndSubTask", "Test updateEpicAndSubTask description", NEW, 10,
                LocalDateTime.of(2023, 1, 1, 12, 0), 0);

        taskManager.addSubTask(subTask);//при добавлении сабтаска id присваивается автоматически, поэтому дальше ожидаем
        // id=3

        final Epic updatedEpic = taskManager.getEpicById(0);//проверяем перезапись эпика
        final Epic expectedEpic = new Epic("Test updateEpicAndSubTask", "Test updateEpicAndSubTask description", NEW);
        final List<Integer> epicTasks = new ArrayList<>();
        SubTask forExpectedEndTime = taskManager.getSubTaskById(2);

        epicTasks.add(subTask.getTaskId());
        expectedEpic.setTaskId(0);
        expectedEpic.setSubTasks(epicTasks);
        expectedEpic.setStartTime(subTask.getStartTime());
        expectedEpic.setDuration(subTask.getDuration() + forExpectedEndTime.getDuration());
        expectedEpic.setEndTime(forExpectedEndTime.getEndTime());


        assertNotNull(updatedEpic.getSubTasks(), "Подзадачи на возвращаются.");
        assertEquals(2, updatedEpic.getSubTasks().size(), "Неверное количество подзадач.");
        assertEquals(expectedEpic.toString(), updatedEpic.toString(), "Задачи не совпадают.");// если сравнивать
        // не .toString(), то тест фейлится, хотя при раскрытии commparsion failure пишет contents are identical, у
        // тасков и сабтасков такой проблемы нет.

        final SubTask updatedSubTask = new SubTask("Test updateEpicAndSubTask", "Test updateEpicAndSubTask description", IN_PROGRESS, 10,
                LocalDateTime.of(2023, 1, 1, 11, 10), 0);
        updatedSubTask.setTaskId(3);
        taskManager.updateSubTask(updatedSubTask);
        final SubTask updatedSavedTask = taskManager.getSubTaskById(3);

        assertNotNull(updatedSavedTask, "Задача не найдена.");
        assertEquals(updatedSubTask, updatedSavedTask, "Задачи не совпадают.");

        final List<SubTask> updatedTasks = taskManager.getSubTaskList();

        assertNotNull(updatedTasks, "Задачи на возвращаются.");
        assertEquals(2, updatedTasks.size(), "Неверное количество задач.");

    }

    @Test
    void updateTaskStandart() {

        addTaskForTest();

        final Task task = new Task("Test updateTask", "Test updateTask description", NEW, 10,
                LocalDateTime.of(2023, 1, 1, 12, 0));

        taskManager.addTask(task);

        final Task newTask = new Task("Test updateTask", "Test updateTask new description", IN_PROGRESS,
                10, LocalDateTime.of(2023, 1, 2, 13, 10));
        newTask.setTaskId(1);
        taskManager.updateTask(newTask);

        final Task savedTask = taskManager.getTaskById(task.getTaskId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(newTask, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getTaskList();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(2, tasks.size(), "Неверное количество задач.");


    }

    @Test
    void deleteSubTaskByIdStandart() {

        addSubTaskForTest();

        final Epic task = new Epic("Test deleteSubTaskById", "Test deleteSubTaskById description", NEW);

        taskManager.addEpic(task);

        final SubTask subTask = new SubTask("Test deleteSubTaskById", "Test deleteSubTaskById description", NEW, 10,
                LocalDateTime.of(2023, 1, 1, 12, 0), 0);

        taskManager.addSubTask(subTask);
        taskManager.deleteSubTaskById(subTask.getTaskId());

        final List<SubTask> tasks = taskManager.getSubTaskList();

        assertEquals("[1,Test addSubTask,NEW,Test addSubTask description,10,2023-01-01T22:00,2023-01-01T22:10,0,\n" +
                "]", tasks.toString(), "Ожидался другой вывод.");
    }

    @Test
    void deleteEpicListStandart() {
        addEpicForTest();
        final Epic task = new Epic("Test deleteEpicList", "Test deleteEpicList description", NEW);

        taskManager.addEpic(task);
        taskManager.deleteEpicList();
        final List<Epic> tasks = taskManager.getEpicList();

        assertEquals("[]", tasks.toString(), "Задачи возвращаются.");
    }

    @Test
    void deleteTaskListStandart() {
        addTaskForTest();
        final Task task = new Task("Test deleteTaskList", "Test deleteTaskList description", NEW, 10,
                LocalDateTime.of(2023, 1, 1, 12, 0));

        taskManager.addTask(task);
        taskManager.deleteTaskList();
        final List<Task> tasks = taskManager.getTaskList();

        assertEquals("[]", tasks.toString(), "Задачи возвращаются.");
    }

    @Test
    void deleteSubTaskListStandart() {

        Epic task = new Epic("Test updateEpicAndSubTask", "Test updateEpicAndSubTask description", NEW);

        taskManager.addEpic(task);

        final SubTask subTask = new SubTask("Test addSubTask", "Test addSubTask description", NEW, 10,
                LocalDateTime.of(2023, 1, 1, 12, 0), 0);

        taskManager.addSubTask(subTask);
        addSubTaskForTest();
        taskManager.deleteSubTaskList();

        final List<SubTask> tasks = taskManager.getSubTaskList();

        assertEquals("[]", tasks.toString(), "Задачи возвращаются.");
    }

    @Test
    void deleteEpicByIdStandart() {

        addEpicForTest();
        final Epic task = new Epic("Test deleteSubTaskById", "Test deleteSubTaskById description", NEW);
        task.setTaskId(0);
        taskManager.addEpic(task);
        taskManager.deleteEpicById(task.getTaskId());

        final List<Epic> tasks = taskManager.getEpicList();

        assertEquals("[0,Test addEpic,NEW,Test addEpic description,0,null,null,\n" +
                "]", tasks.toString(), "Ожидался другой вывод.");
    }

    @Test
    void deleteTaskByIdStandart() {

        addTaskForTest();
        final Task task = new Task("Test deleteTaskList", "Test deleteTaskList description", NEW, 10,
                LocalDateTime.of(2023, 1, 1, 12, 0));

        taskManager.addTask(task);
        taskManager.deleteTaskById(task.getTaskId());
        final List<Task> tasks = taskManager.getTaskList();

        assertEquals("[0,Testing addTask,NEW,Test addTask description,10,2023-01-01T22:00,2023-01-01T22:10,\n" +
                "]", tasks.toString(), "Ожидался другой вывод.");
    }
// Написать тесты, требуемые по ТЗ "С неверным идентификатором задачи (пустой и/или несуществующий идентификатор)" не
// представляется возможным, т.к. невозможно присвоить некорректный или пустой id исходя из принципа добавления и
// обновления задач.

}