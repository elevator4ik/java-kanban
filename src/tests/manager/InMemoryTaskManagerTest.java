package tests.manager;

import manager.InMemoryTaskManager;
import org.junit.jupiter.api.*;

class InMemoryTaskManagerTest extends TaskManagerTest{

    @BeforeEach
    void creating() {//перед каждым тестом создаём чистый менеджер

        taskManager = new InMemoryTaskManager();//передаем значение как флаг

    }
}