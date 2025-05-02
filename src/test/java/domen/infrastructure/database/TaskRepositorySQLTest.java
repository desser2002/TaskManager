package domen.infrastructure.database;
import domen.domain.TaskRepository;
import domen.domain.model.Task;
import domen.domain.model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TaskRepositorySQLTest {
    @Mock
    private Connection mockConnection;
    @Mock
    private PreparedStatement mockPreparedStatement;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldSaveTaskCorrectly() throws Exception {
        //given
        Task task = new Task(
                UUID.randomUUID().toString(),
                "Test Title",
                "Test Description",
                TaskStatus.NEW,
                LocalDateTime.of(2024, 4, 26, 12, 0),
                LocalDateTime.of(2024, 4, 26, 14, 0),
                new LinkedHashSet<>()
        );
        when(mockConnection.prepareStatement(any(String.class))).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        TaskRepositorySQL database = new TaskRepositorySQL(mockConnection);
        //when
        database.save(task);
        // then
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockConnection).prepareStatement(sqlCaptor.capture());
        verify(mockPreparedStatement).executeUpdate();
        assertEquals("INSERT INTO task (id, title, description, status, start_date_time, finish_date_time)" +
                " VALUES (?::uuid,?,?,?::task_status,?::timestamp,?::timestamp)", sqlCaptor.getValue());
        verify(mockPreparedStatement).setObject(eq(1), any(UUID.class));
        verify(mockPreparedStatement).setString(eq(2), eq(task.title()));
        verify(mockPreparedStatement).setString(eq(3), eq(task.description()));
        verify(mockPreparedStatement).setString(eq(4), eq(task.status().toString()));
        verify(mockPreparedStatement).setTimestamp(eq(5), eq(Timestamp.valueOf(task.startDateTime())));
        verify(mockPreparedStatement).setTimestamp(eq(6), eq(Timestamp.valueOf(task.finishDateTime())));
    }

    @Test
    void shouldReturnTaskWhenFoundById() throws SQLException {
        // given
        String taskId = UUID.randomUUID().toString();
        Connection connection = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);
        when(connection.prepareStatement(any(String.class))).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getObject("id", UUID.class)).thenReturn(UUID.fromString(taskId));
        when(rs.getString("title")).thenReturn("Test Title");
        when(rs.getString("description")).thenReturn("Test Description");
        when(rs.getString("status")).thenReturn("NEW");
        when(rs.getTimestamp("start_date_time")).thenReturn(Timestamp.valueOf(LocalDateTime.of(2024, 4, 26, 12, 0)));
        when(rs.getTimestamp("finish_date_time")).thenReturn(Timestamp.valueOf(LocalDateTime.of(2024, 4, 26, 14, 0)));
        TaskRepositorySQL database = new TaskRepositorySQL(connection);
        //when
        Optional<Task> result = database.findById(taskId);
        //then
        assertTrue(result.isPresent());
        Task task = result.get();
        assertEquals("Test Title", task.title());
        assertEquals("Test Description", task.description());
        assertEquals(TaskStatus.NEW, task.status());
    }

    @Test
    void shouldUpdateTasksCorrectly() throws SQLException {
        //given
        Task task = new Task(
                UUID.randomUUID().toString(),
                "Updated Title",
                "Updated Description",
                TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2024, 4, 27, 10, 0),
                LocalDateTime.of(2024, 4, 27, 12, 0),
                new LinkedHashSet<>()
        );
        when(mockConnection.prepareStatement(any(String.class))).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        TaskRepositorySQL database = new TaskRepositorySQL(mockConnection);
        //when
        database.update(task);
        //then
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockConnection).prepareStatement(sqlCaptor.capture());
        assertEquals("UPDATE task SET title=?, description=?, status=?, start_date_time=?,finish_date_time=? WHERE id=?", sqlCaptor.getValue());
        verify(mockPreparedStatement).setString(eq(1), eq(task.title()));
        verify(mockPreparedStatement).setString(eq(2), eq(task.description()));
        verify(mockPreparedStatement).setString(eq(3), eq(task.status().toString()));
        verify(mockPreparedStatement).setTimestamp(eq(4), eq(Timestamp.valueOf(task.startDateTime())));
        verify(mockPreparedStatement).setTimestamp(eq(5), eq(Timestamp.valueOf(task.finishDateTime())));
        verify(mockPreparedStatement).setObject(eq(6), eq(UUID.fromString(task.id())));
        verify(mockPreparedStatement).executeUpdate();
    }


}
