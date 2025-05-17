package domen.infrastructure.database;
import domen.domain.SubtaskRepository;
import domen.domain.model.Subtask;
import domen.domain.model.Task;
import domen.domain.model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class SubtaskRepositorySQLTest {
    @Mock
    private Connection mockConnection;
    @Mock
    private PreparedStatement mockPreparedStatement;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldUpdateSubtask() throws SQLException {
        //given
        SubtaskRepository subtaskRepository = new SubtaskRepositorySQL();
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        String title = "Title";
        String description = "Description";
        String id = UUID.randomUUID().toString();
        TaskStatus status = TaskStatus.IN_PROGRESS;
        Subtask subtask = new Subtask(id, title, description, status);
        //when
        subtaskRepository.update(subtask, mockConnection);
        //then
        verify(mockConnection).prepareStatement("UPDATE subtask SET title = ?, description = ?, status = ? WHERE id = ?");
        verify(mockPreparedStatement).setString(1, title);
        verify(mockPreparedStatement).setString(2, description);
        verify(mockPreparedStatement).setString(3, status.toString());
        verify(mockPreparedStatement).setObject(4, UUID.fromString(id));
        verify(mockPreparedStatement).executeUpdate();
        verify(mockPreparedStatement).close();
    }

    @Test
    void shouldSaveSubtask() throws SQLException {
        SubtaskRepository subtaskRepository = new SubtaskRepositorySQL();
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        String subtaskId = UUID.randomUUID().toString();
        String title = "Title";
        String description = "Description";
        TaskStatus status = TaskStatus.IN_PROGRESS;
        String taskId = UUID.randomUUID().toString();
        Subtask subtask = new Subtask(subtaskId, title, description, status);
        //when
        subtaskRepository.save(subtask, taskId, mockConnection);
        //then
        verify(mockConnection).prepareStatement("INSERT INTO subtask (id, title, description, status,task_id) VALUES (?::uuid,?, ?, ?::task_status,?::uuid)");
        verify(mockPreparedStatement).setObject(1, UUID.fromString(subtaskId));
        verify(mockPreparedStatement).setString(2, title);
        verify(mockPreparedStatement).setString(3, description);
        verify(mockPreparedStatement).setString(4, status.toString());
        verify(mockPreparedStatement).setObject(5, UUID.fromString(taskId));
        verify(mockPreparedStatement).executeUpdate();
        verify(mockPreparedStatement).close();
    }

    @Test
    void shouldDeleteSubtask() throws SQLException {
        //given
        SubtaskRepository subtaskRepository = new SubtaskRepositorySQL();
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        String subtaskId = UUID.randomUUID().toString();
        String title = "Title";
        String description = "Description";
        TaskStatus status = TaskStatus.IN_PROGRESS;
        Subtask subtask = new Subtask(subtaskId, title, description, status);
        //when
        subtaskRepository.delete(subtask, mockConnection);
        //then
        verify(mockConnection).prepareStatement("DELETE FROM subtask WHERE id = ?");
        verify(mockPreparedStatement).setObject(1, UUID.fromString(subtaskId));
        verify(mockPreparedStatement).executeUpdate();
        verify(mockPreparedStatement).close();
    }

    @Test
    void shouldSyncSubtaskCorrectly() throws SQLException {
        String taskId = UUID.randomUUID().toString();
        String existingId = UUID.randomUUID().toString();
        String newId = UUID.randomUUID().toString();
        Subtask existingSubtask = new Subtask(existingId, "Old", "Desc", TaskStatus.NEW);
        Subtask updatedSubtask = new Subtask(existingId, "Updated", "Desc", TaskStatus.IN_PROGRESS);
        Subtask newSubtask = new Subtask(newId, "New", "Desc", TaskStatus.DONE);
        Set<Subtask> existing = new LinkedHashSet<>(Set.of(existingSubtask));
        Set<Subtask> incoming = new LinkedHashSet<>(Set.of(updatedSubtask, newSubtask));
        Task task = new Task(taskId, "Title", "Desc", TaskStatus.NEW, null, null, (LinkedHashSet<Subtask>) incoming);
        SubtaskRepository subtaskRepository = spy(new SubtaskRepositorySQL());
        doReturn(existing).when(subtaskRepository).getSubtasksByTaskId(taskId, mockConnection);
        doNothing().when(subtaskRepository).update(any(Subtask.class), eq(mockConnection));
        doNothing().when(subtaskRepository).save(any(Subtask.class), eq(taskId), eq(mockConnection));
        doNothing().when(subtaskRepository).delete(any(Subtask.class), eq(mockConnection));
        //when
        subtaskRepository.syncSubtasks(task, mockConnection);
        //then
        verify(subtaskRepository).update(eq(updatedSubtask), eq(mockConnection));
        verify(subtaskRepository).save(eq(newSubtask), eq(taskId), eq(mockConnection));
        verify(subtaskRepository, never()).delete(eq(updatedSubtask), eq(mockConnection));
        verify(subtaskRepository, never()).delete(eq(newSubtask), eq(mockConnection));
        verify(subtaskRepository, never()).delete(eq(existingSubtask), eq(mockConnection));
    }
}