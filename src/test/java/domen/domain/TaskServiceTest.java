package domen.domain;

import domen.domain.exception.TaskNotFoundException;
import domen.domain.model.Task;
import domen.domain.model.TaskStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
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
        Task task = taskService.create(title, description, null, null);
        //then
        assertNotNull(task);
        assertEquals(title, task.title());
    }

    @Test
    void shouldNotCreateTask() {
        //given
        String invalidTitle = null;
        //when then
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () -> taskService.create(invalidTitle, "", null, null));
        assertEquals("Title cannot be null or empty", illegalArgumentException.getMessage());
    }

    @Test
    void shouldUpdateTask() {
        //given
        String id = "1";
        Task original = new Task(id, "Old title", "Old desc", TaskStatus.NEW, null, null);
        when(taskRepository.findById(id)).thenReturn(Optional.of(original));

        //when
        Task updated = taskService.update(id, "New title", "New desc", TaskStatus.IN_PROGRESS, null, null);

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
        when(taskRepository.findById(id)).thenReturn(Optional.empty());
        //when then
        TaskNotFoundException ex = assertThrows(TaskNotFoundException.class, () ->
                taskService.update(id, "New title", "New desc", TaskStatus.IN_PROGRESS, null, null)
        );
        assertEquals("Task with id " + id + " not found", ex.getMessage());

    }

    @Test
    void shouldUpdateOnlyNotNull() {
        //given
        String id = "1";
        Task original = new Task(id, "Old title", "Old desc", TaskStatus.NEW, null, null);
        when(taskRepository.findById(id)).thenReturn(Optional.of(original));

        //when
        Task updated = taskService.update(id, "New title", null, null, null, null);

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
                taskService.update(null, "New title", "Description", TaskStatus.NEW, null, null)
        );
    }

    @Test
    void shouldNotCallUpdate() {
        //given
        when(taskRepository.findById("id")).thenReturn(Optional.empty());
        //when/then
        assertThrows(TaskNotFoundException.class, () ->
                taskService.update("id", "New title", "New desc", TaskStatus.IN_PROGRESS, null, null)
        );
        verify(taskRepository, never()).update(any());
    }

    @Test
    void shouldNotCreateTaskWithStartAfterFinish() {
        //given
        LocalDateTime start = LocalDateTime.now().plusDays(2);
        LocalDateTime finish = LocalDateTime.now().plusDays(1);

        //when throws
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                taskService.create("Title", "Description", start, finish)
        );
        assertEquals("Start date cannot be after finish time", ex.getMessage());
    }

    @Test
    void shouldNotCreateTaskWithFinishBeforeCurrentTime() {
        //given
        LocalDateTime finish = LocalDateTime.now().minusDays(2);
        //when throws
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                taskService.create("Title", "Description", null, finish)
        );
        assertEquals("Finish date cannot be before current time", ex.getMessage());
    }

    @Test
    void shouldNotUpdateWithInvalidDate() {
        //given
        String id = "1";
        LocalDateTime start = LocalDateTime.now().plusDays(2);
        LocalDateTime finish = LocalDateTime.now().plusDays(1);

        Task original = new Task(id, "Old title", "Old desc", TaskStatus.NEW, null, null);
        when(taskRepository.findById(id)).thenReturn(Optional.of(original));
        //when throws
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                taskService.update(id, "Title", "Description", TaskStatus.NEW, start, finish)
        );
        assertEquals("Start date cannot be after finish time", ex.getMessage());
    }


}