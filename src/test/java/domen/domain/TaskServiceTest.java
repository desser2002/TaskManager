package domen.domain;

import domen.domain.exception.TaskNotFoundException;
import domen.domain.model.Task;
import domen.domain.model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


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
        String description = "Description of task";

        //when
        Task task = taskService.create(title, description);
        //then
        assertNotNull(task);
        assertEquals(title, task.title());
    }

    @Test
    void shouldNotCreateTask() {
        //given
        String invalidTitle = null;
        //when then
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () -> taskService.create(invalidTitle, ""));
        assertEquals("Title cannot be null or empty", illegalArgumentException.getMessage());
    }

    @Test
    void shouldUpdateTask() {
        //given
        String id = "1";
        Task original = new Task(id, "Old title", "Old desc", TaskStatus.NEW);
        when(taskRepository.findById(id)).thenReturn(Optional.of(original));

        //when
        Task updated = taskService.updateTask(id, "New title", "New desc", TaskStatus.IN_PROGRESS);

        //then
        assertNotNull(updated);
        assertEquals("New title", updated.title());
        assertEquals("New desc", updated.description());
        assertEquals(TaskStatus.IN_PROGRESS, updated.status());
        verify(taskRepository).update(updated);
    }

    @Test
    void shouldNotUpdateNullTask() {
        //given
        String id = "missing";
        when(taskRepository.findById(id)).thenReturn(null);
        //when then
        TaskNotFoundException ex = assertThrows(TaskNotFoundException.class, () ->
                taskService.updateTask(id, "New title", "New desc", TaskStatus.IN_PROGRESS)
        );
        assertEquals("Task with id " + id + " not found", ex.getMessage());

    }

    @Test
    void shouldUpdateOnlyNotNull() {
        //given
        String id = "1";
        Task original = new Task(id, "Old title", "Old desc", TaskStatus.NEW);
        when(taskRepository.findById(id)).thenReturn(Optional.of(original));

        //when
        Task updated = taskService.updateTask(id, "New title", null, null);

        //then
        assertNotNull(updated);
        assertEquals("New title", updated.title());
        assertEquals("Old desc", updated.description());
        assertEquals(TaskStatus.NEW, updated.status());
        verify(taskRepository).update(updated);

    }

    @Test
    void shouldNotUpdateNullId() {
        //when then
        assertThrows(IllegalArgumentException.class, () ->
                taskService.updateTask(null, "New title", "Description", TaskStatus.NEW)
        );
    }

    @Test
    void shouldNotCallUpdate() {
        //given
        when(taskRepository.findById("id")).thenReturn(null);
        //when/then
        assertThrows(TaskNotFoundException.class, () ->
                taskService.updateTask("id", "New title", "New desc", TaskStatus.IN_PROGRESS)
        );
        verify(taskRepository, never()).update(any());
    }


}