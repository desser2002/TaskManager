package domen.domain;
import domen.domain.exception.TaskNotFoundException;
import domen.domain.model.Subtask;
import domen.domain.model.Task;
import domen.domain.model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.*;

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
        //when then
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () -> taskService.create(null, "", null, null));
        assertEquals("Title cannot be null or empty", illegalArgumentException.getMessage());
    }

    @Test
    void shouldUpdateTask() {
        //given
        String id = "1";
        Task original = new Task(id, "Old title", "Old desc", TaskStatus.NEW, null, null, null);
        when(taskRepository.findById(id)).thenReturn(Optional.of(original));
        //when
        Task updated = taskService.update(id, "New title", "New desc", TaskStatus.IN_PROGRESS);
        //then
        assertNotNull(updated);
        assertEquals("New title", updated.title());
        assertEquals("New desc", updated.description());
        assertEquals(TaskStatus.IN_PROGRESS, updated.status());
        verify(taskRepository).update(updated);
    }

    @Test
    void shouldNotAllowToMarkTaskAsDoneIfNotAllSubtasksDone() {
        //given
        String taskId = UUID.randomUUID().toString();
        Subtask subtask1 = new Subtask(UUID.randomUUID().toString(), "Sub1", "Desc", TaskStatus.DONE);
        Subtask subtask2 = new Subtask(UUID.randomUUID().toString(), "Sub1", "Desc", TaskStatus.IN_PROGRESS);
        LinkedHashSet<Subtask> subtasks = new LinkedHashSet<>(Set.of(subtask1, subtask2));
        Task task = new Task(taskId, "Task", "Desc", TaskStatus.IN_PROGRESS, null, null, subtasks);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        //when /then
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> taskService.update(taskId, "New Title", "New Desc", TaskStatus.DONE));
        assertEquals("Cannot mark task as DONE when not all subtasks are DONE", ex.getMessage());
        verify(taskRepository, never()).update(any());
    }

    @Test
    void shouldNotUpdateNullTask() {
        //given
        String id = "missing";
        when(taskRepository.findById(id)).thenReturn(Optional.empty());
        //when then
        TaskNotFoundException ex = assertThrows(TaskNotFoundException.class, () ->
                taskService.update(id, "New title", "New desc", TaskStatus.IN_PROGRESS)
        );
        assertEquals("Task with id " + id + " not found", ex.getMessage());
    }

    @Test
    void shouldUpdateOnlyNotNull() {
        //given
        String id = "1";
        Task original = new Task(id, "Old title", "Old desc", TaskStatus.NEW, null, null, null);
        when(taskRepository.findById(id)).thenReturn(Optional.of(original));
        //when
        Task updated = taskService.update(id, "New title", null, null);
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
                taskService.update(null, "New title", "Description", TaskStatus.NEW)
        );
    }

    @Test
    void shouldNotCallUpdate() {
        //given
        when(taskRepository.findById("id")).thenReturn(Optional.empty());
        //when/then
        assertThrows(TaskNotFoundException.class, () ->
                taskService.update("id", "New title", "New desc", TaskStatus.IN_PROGRESS)
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
    void shouldAssignTime() {
        // given
        String id = "1";
        Task original = new Task(id, "Title", "Desc", TaskStatus.NEW, null, null, null);
        when(taskRepository.findById(id)).thenReturn(Optional.of(original));
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime finish = LocalDateTime.now().plusDays(2);
        // when
        Task updated = taskService.assignTime(id, start, finish);
        // then
        assertNotNull(updated);
        assertEquals(start, updated.startDateTime());
        assertEquals(finish, updated.finishDateTime());
        verify(taskRepository).update(updated);
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
        Task original = new Task(id, "Old title", "Old desc", TaskStatus.NEW, null, null, null);
        when(taskRepository.findById(id)).thenReturn(Optional.of(original));
        //when throws
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                taskService.assignTime(id, start, finish)
        );
        assertEquals("Start date cannot be after finish time", ex.getMessage());
    }

    @Test
    void shouldReturnAllTask() {
        //given
        Task task1 = new Task("1", "Title", "Desc", TaskStatus.NEW, null, null, null);
        Task task2 = new Task("2", "Title", "Desc", TaskStatus.IN_PROGRESS, null, null, null);
        Task task3 = new Task("3", "Title", "Desc", TaskStatus.DONE, null, null, null);
        Task task4 = new Task("4", "Title", "Desc", TaskStatus.NEW, null, LocalDateTime.now().minusDays(2), null);
        when(taskRepository.getAll()).thenReturn(List.of(task1, task2, task3, task4));
        //when
        List<Task> allTasks = taskService.getAllTasks();
        //then
        assertEquals(4, allTasks.size());
        assertTrue(allTasks.contains(task1));
        assertTrue(allTasks.contains(task2));
        assertTrue(allTasks.contains(task3));
        assertTrue(allTasks.contains(task4));
    }

    @Test
    void shouldReturnOnlyActiveTasks() {
        //given
        Task task1 = new Task("1", "Title", "Desc", TaskStatus.NEW, null, null, null);
        Task task2 = new Task("2", "Title", "Desc", TaskStatus.IN_PROGRESS, null, null, null);
        Task task3 = new Task("3", "Title", "Desc", TaskStatus.DONE, null, null, null);
        Task task4 = new Task("4", "Title", "Desc", TaskStatus.NEW, null, LocalDateTime.now().minusDays(2), null);
        when(taskRepository.getAll()).thenReturn(List.of(task1, task2, task3, task4));
        //when
        List<Task> activeTasks = taskService.getActiveTasks();
        //then
        assertEquals(3, activeTasks.size());
        assertTrue(activeTasks.contains(task1));
        assertTrue(activeTasks.contains(task2));
        assertFalse(activeTasks.contains(task3));
        assertTrue(activeTasks.contains(task4));
    }

    @Test
    void shouldReturnOnlyCompletedTasks() {
        //given
        Task task1 = new Task("1", "Title", "Desc", TaskStatus.NEW, null, null, null);
        Task task2 = new Task("2", "Title", "Desc", TaskStatus.IN_PROGRESS, null, null, null);
        Task task3 = new Task("3", "Title", "Desc", TaskStatus.DONE, null, null, null);
        Task task4 = new Task("4", "Title", "Desc", TaskStatus.NEW, null, LocalDateTime.now().minusDays(2), null);
        when(taskRepository.getAll()).thenReturn(List.of(task1, task2, task3, task4));
        //when
        List<Task> completedTasks = taskService.getCompletedTasks();
        //then
        assertEquals(1, completedTasks.size());
        assertFalse(completedTasks.contains(task1));
        assertFalse(completedTasks.contains(task2));
        assertTrue(completedTasks.contains(task3));
        assertFalse(completedTasks.contains(task4));
    }

    @Test
    void shouldReturnOnlyOverdueTasks() {
        //given
        Task task1 = new Task("1", "Title", "Desc", TaskStatus.NEW, null, null, null);
        Task task2 = new Task("2", "Title", "Desc", TaskStatus.IN_PROGRESS, null, null, null);
        Task task3 = new Task("3", "Title", "Desc", TaskStatus.DONE, null, null, null);
        Task task4 = new Task("4", "Title", "Desc", TaskStatus.NEW, null, LocalDateTime.now().minusDays(2), null);
        when(taskRepository.getAll()).thenReturn(List.of(task1, task2, task3, task4));
        //when
        List<Task> overdueTasks = taskService.getOverdueTasks();
        //then
        assertEquals(1, overdueTasks.size());
        assertFalse(overdueTasks.contains(task1));
        assertFalse(overdueTasks.contains(task2));
        assertFalse(overdueTasks.contains(task3));
        assertTrue(overdueTasks.contains(task4));
    }

    @Test
    void shouldCreateSubtask() {
        //given
        String taskId = "1";
        Task originaltask = new Task(taskId, "Title", "Desc", TaskStatus.NEW, null, null, new LinkedHashSet<>());
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(originaltask));
        //when
        Task updatedTask = taskService.createSubtask(taskId, "Subtask title", "Subtask desc");
        //then
        assertNotNull(updatedTask);
        assertEquals(1, updatedTask.subtasks().size());
        Subtask subtask = updatedTask.subtasks().getFirst();
        assertEquals("Subtask title", subtask.title());
        assertEquals("Subtask desc", subtask.description());
        assertEquals(TaskStatus.NEW, subtask.status());
        verify(taskRepository).update(updatedTask);
    }

    @Test
    void shouldThrowExceptionWhenSubtaskTitleIsNull() {
        String taskId = "1";
        Task task = new Task(taskId, "Task title", "Task desc", TaskStatus.NEW, null, null, new LinkedHashSet<>());
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> taskService.createSubtask(taskId, null, "Subtask desc"));
        assertEquals("Title cannot be null or empty", ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenTaskNotFound() {
        //given
        String taskId = "1";
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());
        //when then
        TaskNotFoundException ex = assertThrows(TaskNotFoundException.class, () -> taskService.createSubtask(taskId, "Subtask title", "Subtask desc"));
        assertEquals("Task with id " + taskId + " not found", ex.getMessage());
    }

    @Test
    void shouldCreateAndAddSubtaskToTask() {
        //given
        String taskId = UUID.randomUUID().toString();
        Task task = new Task(taskId, "Title", "Desc", TaskStatus.NEW, null, null, new LinkedHashSet<>());
        String subtaskTitle = "Subtask title";
        String subtaskDesc = "Subtask desc";
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        //when
        Task updateTask = taskService.createSubtask(taskId, subtaskTitle, subtaskDesc);
        //then
        assertEquals(1, updateTask.subtasks().size());
        Subtask subtask = updateTask.subtasks().getFirst();
        assertEquals(subtaskTitle, subtask.title());
        assertEquals(subtaskDesc, subtask.description());
        assertEquals(TaskStatus.NEW, subtask.status());
        verify(taskRepository).update(updateTask);
    }

    @Test
    void shouldDeleteSubtaskFromTask() {
        //given
        String taskId = UUID.randomUUID().toString();
        Subtask kept = new Subtask(UUID.randomUUID().toString(), "Kept", "Desc", TaskStatus.NEW);
        Subtask toDelete = new Subtask(UUID.randomUUID().toString(), "ToDelete", "Desc", TaskStatus.NEW);
        LinkedHashSet<Subtask> initialSubtask = new LinkedHashSet<>(Set.of(kept, toDelete));
        LinkedHashSet<Subtask> updatedSubtasks = new LinkedHashSet<>(Set.of(kept));
        Task originalTask = new Task(taskId, "Title", "Desc", TaskStatus.NEW, null, null, initialSubtask);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(originalTask));
        Task updatedTask = new Task(taskId, "Title", "Desc", TaskStatus.NEW, null, null, updatedSubtasks);
        //when
        taskService.update(taskId, updatedSubtasks);
        //then
        verify(taskRepository).update(eq(updatedTask));
    }

    @Test
    void shouldUpdateSubtask() {
        //given
        String taskId = UUID.randomUUID().toString();
        String subtaskId = UUID.randomUUID().toString();
        Subtask subtask = new Subtask(subtaskId, "TitleSub", "DescSub", TaskStatus.NEW);
        Task task = new Task(taskId, "Title", "Desc", TaskStatus.NEW, null, null, new LinkedHashSet<>(Set.of(subtask)));
        String updatedTitle = "Update title";
        String updatedDesc = "Update desc";
        TaskStatus updatedStatus = TaskStatus.DONE;
        Subtask updatedSubtask = new Subtask(subtaskId, updatedTitle, updatedDesc, updatedStatus);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        //when
        Task updatedTask = taskService.updateSubtask(task, updatedSubtask);
        //then
        assertEquals(1, updatedTask.subtasks().size());
        Subtask resultSubtask = updatedTask.subtasks().iterator().next();
        assertEquals(subtaskId, resultSubtask.id());
        assertEquals(updatedTitle, resultSubtask.title());
        assertEquals(updatedDesc, resultSubtask.description());
        assertEquals(updatedStatus, resultSubtask.status());
        verify(taskRepository).update(updatedTask);
    }
}