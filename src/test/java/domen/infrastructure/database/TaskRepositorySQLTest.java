package domen.infrastructure.database;

import domen.domain.model.Task;
import domen.domain.model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
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
}
