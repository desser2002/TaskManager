package domen.domain;

import domen.domain.exception.TaskNotFoundException;
import domen.domain.model.Subtask;
import domen.domain.model.Task;
import domen.domain.model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SubtaskServiceTest {
    @Mock
    private SubtaskRepository subtaskRepository;
    @Mock
    private TaskRepository taskRepository;
    private SubtaskService subtaskService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        subtaskService = new SubtaskService(subtaskRepository, taskRepository);
    }

    @Test
    void shouldGetSubtasks() {
        //given
        String taskId = UUID.randomUUID().toString();
        Set<Subtask> expectedSubtask = Set.of(new Subtask(UUID.randomUUID().toString(), "Title", "description", TaskStatus.NEW),
                new Subtask(UUID.randomUUID().toString(), "Title2", "description2", TaskStatus.IN_PROGRESS),
                new Subtask(UUID.randomUUID().toString(), "Title3", "description3", TaskStatus.DONE));
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(mock(Task.class)));
        when(subtaskService.getSubtasks(taskId)).thenReturn(expectedSubtask);
        //when
        Set<Subtask> subtasks = subtaskService.getSubtasks(taskId);
        //then
        assertEquals(expectedSubtask, subtasks);
    }

    @Test
    void shouldThrowWhenGettingSubtasksForNonexistentTask() {
        //given
        String taskId = UUID.randomUUID().toString();
        //when//then
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());
        TaskNotFoundException ex = assertThrows(TaskNotFoundException.class, () -> subtaskService.getSubtasks(taskId));
        assertEquals(taskId, ex.getMessage());
    }

    @Test
    void shouldReturnSubtaskBySubtaskId() {
        //given
        String taskId = UUID.randomUUID().toString();
        String subtaskId = UUID.randomUUID().toString();
        Subtask subtask = new Subtask(subtaskId, "Title", "description", TaskStatus.NEW);
        Subtask subtask2 = new Subtask(UUID.randomUUID().toString(), "Title2", "description2", TaskStatus.IN_PROGRESS);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(mock(Task.class)));
        when(subtaskRepository.getSubtasksByTaskId(taskId)).thenReturn(Set.of(subtask, subtask2));
        //when
        Subtask result = subtaskService.getSubtask(taskId, subtaskId);
        //then
        assertEquals(subtask, result);
    }

    @Test
    void shouldSaveSubtask() {
        //given
        String taskId = UUID.randomUUID().toString();
        Subtask subtask = new Subtask(UUID.randomUUID().toString(), "Title", "description", TaskStatus.NEW);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(mock(Task.class)));
        //when
        subtaskService.saveSubtask(taskId, subtask);
        //then
        verify(subtaskRepository).save(subtask, taskId);
    }

    @Test
    void shouldUpdateSubtask() {
        //given
        String taskId = UUID.randomUUID().toString();
        Subtask subtask = new Subtask(UUID.randomUUID().toString(), "Title", "description", TaskStatus.NEW);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(mock(Task.class)));
        when(subtaskRepository.getSubtasksByTaskId(taskId)).thenReturn(Set.of(subtask));
        //when
        subtaskService.updateSubtask(taskId, subtask);
        verify(subtaskRepository).update(subtask);
    }

    @Test
    void shouldDeleteSubtask() {
        //given
        String taskId = UUID.randomUUID().toString();
        String subtaskId = UUID.randomUUID().toString();
        Subtask subtask = new Subtask(subtaskId, "Title", "description", TaskStatus.NEW);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(mock(Task.class)));
        when(subtaskRepository.getSubtasksByTaskId(taskId)).thenReturn(Set.of(subtask));
        //when
        subtaskService.deleteSubtask(taskId, subtaskId);
        //then
        verify(subtaskRepository).delete(subtask);
    }

    @Test
    void shouldSyncSubtasks() {
        //given
        String taskId = UUID.randomUUID().toString();
        String subtaskToUpdateId = UUID.randomUUID().toString();
        Subtask old = new Subtask(subtaskToUpdateId, "Old", "Old subtask description", TaskStatus.DONE);
        Subtask updated = new Subtask(subtaskToUpdateId, "Updated", "Updated subtask description", TaskStatus.IN_PROGRESS);
        Subtask toSave = new Subtask(UUID.randomUUID().toString(), "Save", "Saved subtask description", TaskStatus.NEW);
        Set<Subtask> existing = Set.of(old);
        Set<Subtask> incoming = Set.of(updated, toSave);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(mock(Task.class)));
        when(subtaskRepository.getSubtasksByTaskId(taskId)).thenReturn(existing);
        //when
        subtaskService.syncSubtasks(taskId, incoming);
        //then
        verify(subtaskRepository).update(updated);
        verify(subtaskRepository).save(toSave, taskId);
        verify(subtaskRepository, never()).delete(any(Subtask.class));
    }

    @Test
    void shouldDetectAllSubtasksDone() {
        //given
        String taskId = UUID.randomUUID().toString();
        Subtask subtask1 = new Subtask(UUID.randomUUID().toString(), "Title", "description", TaskStatus.DONE);
        Subtask subtask2 = new Subtask(UUID.randomUUID().toString(), "Title", "description", TaskStatus.DONE);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(mock(Task.class)));
        when(subtaskRepository.getSubtasksByTaskId(taskId)).thenReturn(Set.of(subtask1, subtask2));
        //when
        boolean isDone = subtaskService.areAllSubtasksDone(taskId);
        //then
        assertTrue(isDone);
    }

    @Test
    void shouldDetectNotAllSubtasksDone() {
        //given
        String taskId = UUID.randomUUID().toString();
        Subtask subtask1 = new Subtask(UUID.randomUUID().toString(), "Title", "description", TaskStatus.IN_PROGRESS);
        Subtask subtask2 = new Subtask(UUID.randomUUID().toString(), "Title", "description", TaskStatus.DONE);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(mock(Task.class)));
        when(subtaskRepository.getSubtasksByTaskId(taskId)).thenReturn(Set.of(subtask1, subtask2));
        //when
        boolean isDone = subtaskService.areAllSubtasksDone(taskId);
        //then
        assertFalse(isDone);
    }
}