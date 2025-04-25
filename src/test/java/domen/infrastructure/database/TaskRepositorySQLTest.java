package domen.infrastructure.database;

import domen.domain.TaskRepository;
import domen.domain.TaskService;
import domen.domain.model.Task;
import domen.domain.model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

class TaskRepositorySQLTest {
    @Mock
    private TaskRepository taskRepository;
    @InjectMocks
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldSaveTaskToDatabase() {
        TaskRepositorySQL database = new TaskRepositorySQL();
        Task task = taskService.create("Title","Description");
        database.save(task);
    }
}