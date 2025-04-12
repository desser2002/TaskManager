package domen.domain;

import domen.domain.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;


class TaskServiceTest {
    @Mock
    private TaskRepository taskRepository;
    @InjectMocks
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateTask() {
        //given
        String title = "title";

        //when
        Task task = taskService.create(title);
        //then
        assertNotNull(task);
        assertEquals(title, task.title());
    }

    @Test
    void shouldNotCreateTask() {
        //given
        String invalidTitle = null;
        //when then
        IllegalArgumentException illegalArgumentException =
                assertThrows(IllegalArgumentException.class, () -> taskService.create(invalidTitle));
        assertEquals ("Title cannot be null or empty", illegalArgumentException.getMessage());
    }
}