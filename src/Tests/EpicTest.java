package Tests;

import manager.FileBackedTasksManager;
import manager.TaskManager;
import modul.Epic;
import modul.SubTask;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.List;
import static modul.Status.*;

class EpicTest {//обновление статуса происходит через менеджера, так что в этих тестах покрывается требование проверки
    // расчёта статуса эпика для менеджеров.

    Epic epic = new Epic("Test addEpic", "Test addEpic description", NEW);
    TaskManager taskManager;

    @BeforeEach
    void start(){
        taskManager = new FileBackedTasksManager(0);//для записи данных о сабтасках в эпик
        epic.setTaskId(0);
        taskManager.addEpic(epic);
    }

    @Test
    void voidSubTaskListStatus(){
        final List<Integer> subTaskList = epic.getSubTasks();

        assertNull(subTaskList, "Список не пуст");
        assertEquals(NEW,epic.getStatus(), "Статус неверный");
    }

    @Test
    void subTasksStatusNew() {

        final SubTask subTask = new SubTask("Test addSubTask", "Test addSubTask description", NEW, 10,
                LocalDateTime.of(2023, 1, 1, 12, 0), 0);
        subTask.setTaskId(1);

        final SubTask subTask2 = new SubTask("Test addSubTask", "Test addSubTask description", NEW, 10,
                LocalDateTime.of(2023, 1, 1, 11, 0), 0);
        subTask2.setTaskId(2);

        taskManager.addSubTask(subTask);
        taskManager.addSubTask(subTask2);

        assertEquals(NEW,epic.getStatus(), "Статус неверный");

    }

    @Test
    void subTasksStatusDone() {
        final SubTask subTask = new SubTask("Test addSubTask", "Test addSubTask description", DONE, 10,
                LocalDateTime.of(2023, 1, 1, 12, 0), 0);
        subTask.setTaskId(1);

        final SubTask subTask2 = new SubTask("Test addSubTask", "Test addSubTask description", DONE, 10,
                LocalDateTime.of(2023, 1, 1, 11, 0), 0);
        subTask2.setTaskId(2);

        taskManager.addSubTask(subTask);
        taskManager.addSubTask(subTask2);

        assertEquals(DONE,epic.getStatus(), "Статус неверный");
    }

    @Test
    void subTasksStatusInProgerss() {

        final SubTask subTask = new SubTask("Test addSubTask", "Test addSubTask description", IN_PROGRESS, 10,
                LocalDateTime.of(2023, 1, 1, 12, 0), 0);
        subTask.setTaskId(1);

        final SubTask subTask2 = new SubTask("Test addSubTask", "Test addSubTask description", IN_PROGRESS, 10,
                LocalDateTime.of(2023, 1, 1, 11, 0), 0);
        subTask2.setTaskId(2);

        taskManager.addSubTask(subTask);
        taskManager.addSubTask(subTask2);

        assertEquals(IN_PROGRESS,epic.getStatus(), "Статус неверный");
    }

    @Test
    void subTasksStatusDoneAndNew() {

        final SubTask subTask = new SubTask("Test addSubTask", "Test addSubTask description", NEW, 10,
                LocalDateTime.of(2023, 1, 1, 12, 0), 0);
        subTask.setTaskId(1);

        final SubTask subTask2 = new SubTask("Test addSubTask", "Test addSubTask description", DONE, 10,
                LocalDateTime.of(2023, 1, 1, 11, 0), 0);
        subTask2.setTaskId(2);

        taskManager.addSubTask(subTask);
        taskManager.addSubTask(subTask2);

        assertEquals(IN_PROGRESS,epic.getStatus(), "Статус неверный");
    }

    @Test
    void durationTest() {

        final SubTask subTask = new SubTask("Test addSubTask", "Test addSubTask description", NEW, 10,
                LocalDateTime.of(2023, 1, 1, 12, 0), 0);
        subTask.setTaskId(1);

        final SubTask subTask2 = new SubTask("Test addSubTask", "Test addSubTask description", DONE, 10,
                LocalDateTime.of(2023, 1, 1, 11, 0), 0);
        subTask2.setTaskId(2);

        taskManager.addSubTask(subTask);
        taskManager.addSubTask(subTask2);

        assertEquals(20,epic.getDuration(), "Продолжительность неверная");
    }

    @Test
    void startTimeTest() {

        final SubTask subTask = new SubTask("Test addSubTask", "Test addSubTask description", NEW, 10,
                LocalDateTime.of(2023, 1, 1, 12, 0), 0);
        subTask.setTaskId(1);

        final SubTask subTask2 = new SubTask("Test addSubTask", "Test addSubTask description", DONE, 10,
                LocalDateTime.of(2023, 1, 1, 11, 0), 0);
        subTask2.setTaskId(2);

        taskManager.addSubTask(subTask);
        taskManager.addSubTask(subTask2);

        assertEquals( subTask2.getStartTime(),epic.getStartTime(),
                "Время начала неверное");
    }
    @Test
    void endTimeTest() {

        final SubTask subTask = new SubTask("Test addSubTask", "Test addSubTask description", NEW, 10,
                LocalDateTime.of(2023, 1, 1, 12, 0), 0);
        subTask.setTaskId(1);

        final SubTask subTask2 = new SubTask("Test addSubTask", "Test addSubTask description", DONE, 10,
                LocalDateTime.of(2023, 1, 1, 11, 0), 0);
        subTask2.setTaskId(2);

        taskManager.addSubTask(subTask);
        taskManager.addSubTask(subTask2);

        assertEquals( subTask.getEndTime(),epic.getEndTime(), "Время окончания неверное");
    }
}