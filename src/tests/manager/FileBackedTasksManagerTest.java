package tests.manager;

import manager.FileBackedTasksManager;
import modul.Epic;
import modul.SubTask;
import modul.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import static modul.Status.IN_PROGRESS;
import static modul.Status.NEW;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class FileBackedTasksManagerTest extends TaskManagerTest {

    @BeforeEach
    void creating() {//перед каждым тестом создаём чистый менеджер

        taskManager = new FileBackedTasksManager(0);//передаем значение как флаг

    }

    @Test
    void savingTest() {
        final Task task = new Task("Test save", "Test save description", NEW, 10,
                LocalDateTime.of(2023, 1, 1, 12, 0));
        task.setTaskId(0);
        taskManager.addTask(task);
        final Epic epic = new Epic("Test save", "Test save description", NEW);
        epic.setTaskId(1);
        taskManager.addEpic(epic);
        final SubTask subTask = new SubTask("Test save", "Test save description", NEW, 10,
                LocalDateTime.of(2023, 1, 1, 11, 0), 1);
        subTask.setTaskId(2);
        taskManager.addSubTask(subTask);
        final Task newTask = new Task("Test updateTask", "Test updateTask new description", IN_PROGRESS,
                10, LocalDateTime.of(2023, 1, 2, 13, 10));
        task.setTaskId(0);
        taskManager.updateTask(newTask);

        assertEquals("id,type,name,status,description,duration,startTime,endTime,epic\n" +
                "0,TASK,Test updateTask,IN_PROGRESS,Test updateTask new description,10,2023-01-02T13:10,\n" +
                "1,EPIC,Test save,NEW,Test save description,10,2023-01-01T11:00,2023-01-01T11:10,\n" +
                "2,SUB_TASK,Test save,NEW,Test save description,10,2023-01-01T11:00,,1,\n" +
                " \n" +
                "1,\n", taskManager.printFile(), "Неверная запись в файл.");
    }

    @Test
    void readingFromFileTest() {

        String fileDir = System.getProperty("user.dir") +
                File.separator +
                "src" +
                File.separator +
                "tests" +
                File.separator +
                "files" +
                File.separator +
                "testStorage.csv";
        try {
            Path path = Paths.get(fileDir);
            if (!Files.exists(path)) {

                Files.createFile(path);
            }
            Files.write(path, ("id,type,name,status,description,duration,startTime,endTime,epic\n" +
                    "0,TASK,Test updateTask,IN_PROGRESS,Test updateTask new description,10,2023-01-02T13:10,\n" +
                    "1,EPIC,Test save,NEW,Test save description,10,2023-01-01T11:10,2023-01-01T11:10,\n" +
                    "2,SUB_TASK,Test save,NEW,Test save description,10,2023-01-01T11:00,,1,\n" +
                    " \n" +
                    "1,\n").getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        taskManager.readFromFile();

        assertEquals("[0,Test updateTask,IN_PROGRESS,Test updateTask new description,10,2023-01-02T13:10," +
                        "2023-01-02T13:20,\n]",
                taskManager.getTaskList().toString(), "Неверное чтение тасков из файла.");
        assertEquals("[1,Test save,NEW,Test save description,10,2023-01-01T11:10,2023-01-01T11:10,\n]",
                taskManager.getEpicList().toString(), "Неверное чтение эпиков из файла.");
        assertEquals("[2,Test save,NEW,Test save description,10,2023-01-01T11:00,2023-01-01T11:10,1,\n" +
                        "]",
                taskManager.getSubTaskList().toString(), "Неверное чтение сабтасков из файла.");
        assertEquals("[1,Test save,NEW,Test save description,10,2023-01-01T11:10,2023-01-01T11:10,\n]",
                taskManager.getHistory().toString(), "Неверное чтение истории из файла.");
        assertEquals("2,Test save,NEW,Test save description,10,2023-01-01T11:00,2023-01-01T11:10,1,\n\n" +
                        "0,Test updateTask,IN_PROGRESS,Test updateTask new description,10,2023-01-02T13:10,2023-01-02T13:20,\n\n",
                taskManager.getPrioritizedTasks(), "Неверное чтение упорядоченного списка из файла.");
    }

    @Test
    void readingFromFileEpicWithoutSubTasksTest() {

        String fileDir = System.getProperty("user.dir") +
                File.separator +
                "src" +
                File.separator +
                "tests" +
                File.separator +
                "files" +
                File.separator +
                "testStorage.csv";

        try {
            Path path = Paths.get(fileDir);

            if (!Files.exists(path)) {

                Files.createFile(path);
            }
            Files.write(path, ("id,type,name,status,description,duration,startTime,endTime,epic\n" +
                    "1,EPIC,Test save,NEW,Test save description,0,null,null,\n" +
                    " \n" +
                    "1,\n").getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        taskManager.readFromFile();

        assertEquals("[1,Test save,NEW,Test save description,0,null,null,\n]",
                taskManager.getEpicList().toString(), "Неверное чтение эпиков из файла.");
        assertEquals("[1,Test save,NEW,Test save description,0,null,null,\n]",
                taskManager.getHistory().toString(), "Неверное чтение истории из файла.");

    }
    @Test
    void readingFromFileVoidListsAndHistoryTest() {

        String fileDir = System.getProperty("user.dir") +
                File.separator +
                "src" +
                File.separator +
                "tests" +
                File.separator +
                "files" +
                File.separator +
                "testStorage.csv";
        try {
            Path path = Paths.get(fileDir);
            if (!Files.exists(path)) {

                Files.createFile(path);
            }
            Files.write(path, ("id,type,name,status,description,duration,startTime,endTime,epic\n" +
                    " \n" +
                    "\n").getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        taskManager.readFromFile();

        assertEquals("[]",
                taskManager.getTaskList().toString(), "Неверное чтение тасков из файла.");
        assertEquals("[]",
                taskManager.getEpicList().toString(), "Неверное чтение эпиков из файла.");
        assertEquals("[]",
                taskManager.getSubTaskList().toString(), "Неверное чтение сабтасков из файла.");
        assertEquals("[]",
                taskManager.getHistory().toString(), "Неверное чтение истории из файла.");
    }
}
