package domen.infrastructure.database;

import domen.domain.SubtaskRepository;
import domen.domain.model.Subtask;
import domen.domain.model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
        SubtaskRepository subtaskRepository = new SubtaskRepositorySQL(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        String title = "Title";
        String description = "Description";
        String id = UUID.randomUUID().toString();
        TaskStatus status = TaskStatus.IN_PROGRESS;
        Subtask subtask = new Subtask(id, title, description, status);
        //when
        subtaskRepository.update(subtask);
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
        SubtaskRepository subtaskRepository = new SubtaskRepositorySQL(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        String subtaskId = UUID.randomUUID().toString();
        String title = "Title";
        String description = "Description";
        TaskStatus status = TaskStatus.IN_PROGRESS;
        String taskId = UUID.randomUUID().toString();
        Subtask subtask = new Subtask(subtaskId, title, description, status);
        //when
        subtaskRepository.save(subtask, taskId);
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
        SubtaskRepository subtaskRepository = new SubtaskRepositorySQL(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        String subtaskId = UUID.randomUUID().toString();
        String title = "Title";
        String description = "Description";
        TaskStatus status = TaskStatus.IN_PROGRESS;
        Subtask subtask = new Subtask(subtaskId, title, description, status);
        //when
        subtaskRepository.delete(subtask);
        //then
        verify(mockConnection).prepareStatement("DELETE FROM subtask WHERE id = ?");
        verify(mockPreparedStatement).setObject(1, UUID.fromString(subtaskId));
        verify(mockPreparedStatement).executeUpdate();
        verify(mockPreparedStatement).close();
    }

    @Test
    void shouldMoveSubtask() throws SQLException {
        //given
        String subtaskId = UUID.randomUUID().toString();
        String newTaskId = UUID.randomUUID().toString();
        SubtaskRepository subtaskRepository = new SubtaskRepositorySQL(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        //when
        subtaskRepository.move(subtaskId, newTaskId);
        verify(mockConnection).prepareStatement("UPDATE subtask SET task_id = ?::uuid WHERE id = ?::uuid");
        verify(mockPreparedStatement).setObject(1, UUID.fromString(newTaskId));
        verify(mockPreparedStatement).setObject(2, UUID.fromString(subtaskId));
        verify(mockPreparedStatement).executeUpdate();
        verify(mockPreparedStatement).close();
    }
}