package Tests;

import manager.InMemoryTaskManager;
import org.junit.jupiter.api.*;

class InMemoryTaskManagerTest extends TaskManagerTest{

    @BeforeEach
    void creating() {//перед каждым тестом создаём чистый менеджер

        taskManager = new InMemoryTaskManager();//передаем значение как флаг

    }
    @Test
    void addTaskWithVoidTask_SubTask_EpicLists() {
        super.addTaskWithVoidTask_SubTask_EpicLists();
    }

    @Test
    void addEpicWithVoidTask_SubTask_EpicLists() {
        super.addEpicWithVoidTask_SubTask_EpicLists();
    }


    @Test
    void addSubTaskWithVoidTask_SubTask_EpicLists() {
        super.addSubTaskWithVoidTask_SubTask_EpicLists();
    }

    @Test
    void updateEpicAndSubTaskWithVoidTask_SubTask_EpicLists() {
        super.updateEpicAndSubTaskWithVoidTask_SubTask_EpicLists();
    }

    @Test
    void updateTaskWithVoidTask_SubTask_EpicLists() {
        super.updateTaskWithVoidTask_SubTask_EpicLists();
    }

    @Test
    void deleteSubTaskByIdWithVoidTask_SubTask_EpicLists() {
        super.deleteSubTaskByIdWithVoidTask_SubTask_EpicLists();
    }

    @Test
    void deleteEpicListWithVoidTask_SubTask_EpicLists() {
        super.deleteEpicListWithVoidTask_SubTask_EpicLists();
    }

    @Test
    void deleteTaskListWithVoidTask_SubTask_EpicLists() {
        super.deleteTaskListWithVoidTask_SubTask_EpicLists();
    }

    @Test
    void deleteSubTaskListWithVoidTask_SubTask_EpicLists() {
        super.deleteSubTaskListWithVoidTask_SubTask_EpicLists();
    }

    @Test
    void deleteEpicByIdWithVoidTask_SubTask_EpicLists() {
        super.deleteEpicByIdWithVoidTask_SubTask_EpicLists();
    }

    @Test
    void deleteTaskByIdWithVoidTask_SubTask_EpicLists() {
        super.deleteTaskByIdWithVoidTask_SubTask_EpicLists();
    }
    @Test
    void addTaskStandart() {
        super.addTaskStandart();
    }

    @Test
    void addEpicStandart() {
        super.addEpicStandart();
    }


    @Test
    void addSubTaskStandart() {
        super.addSubTaskStandart();
    }

    @Test
    void updateEpicAndSubTaskStandart() {
        super.updateEpicAndSubTaskStandart();
    }

    @Test
    void updateTaskStandart() {
        super.updateTaskStandart();
    }

    @Test
    void deleteSubTaskByIdStandart() {
        super.deleteSubTaskByIdStandart();
    }

    @Test
    void deleteEpicListStandart() {
        super.deleteEpicListStandart();
    }

    @Test
    void deleteTaskListStandart() {
        super.deleteTaskListStandart();
    }

    @Test
    void deleteSubTaskListStandart() {
        super.deleteSubTaskListStandart();
    }

    @Test
    void deleteEpicByIdStandart() {
        super.deleteEpicByIdStandart();
    }

    @Test
    void deleteTaskByIdStandart() {
        super.deleteTaskByIdStandart();
    }
}