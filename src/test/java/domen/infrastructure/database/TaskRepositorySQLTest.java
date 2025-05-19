package domen.infrastructure.database;
import domen.domain.SubtaskRepository;
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
    @Mock
    private SubtaskRepository subtaskRepository;

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
        TaskRepositorySQL database = new TaskRepositorySQL(subtaskRepository,mockConnection);
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
        Task task = new Task(
                UUID.randomUUID().toString(),
                "Test Title",
                "Test Description",
                TaskStatus.NEW,
                LocalDateTime.of(2024, 4, 26, 12, 0),
                LocalDateTime.of(2024, 4, 26, 14, 0),
                new LinkedHashSet<>()
        );
        Connection connection = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);
        when(connection.prepareStatement(any(String.class))).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getObject("id", UUID.class)).thenReturn(UUID.fromString(task.id()));
        when(rs.getString("title")).thenReturn(task.title());
        when(rs.getString("description")).thenReturn(task.description());
        when(rs.getString("status")).thenReturn(task.status().toString());
        when(rs.getTimestamp("start_date_time")).thenReturn(Timestamp.valueOf(task.startDateTime()));
        when(rs.getTimestamp("finish_date_time")).thenReturn(Timestamp.valueOf(task.finishDateTime()));
        TaskRepositorySQL database = new TaskRepositorySQL(subtaskRepository,mockConnection);
        //when
        Optional<Task> result = database.findById(task.id());
        //then
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(connection).prepareStatement(sqlCaptor.capture());
        assertEquals("SELECT id,title,description,status,start_date_time,finish_date_time FROM task WHERE id = ?::uuid", sqlCaptor.getValue());
        assertTrue(result.isPresent());
        Task taskFromDatabase = result.get();
        assertEquals(task.title(), taskFromDatabase.title());
        assertEquals(task.description(), taskFromDatabase.description());
        assertEquals(task.status(), taskFromDatabase.status());
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
        TaskRepositorySQL database = new TaskRepositorySQL(subtaskRepository,mockConnection);
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
