package domen.infrastructure.database;

import domen.domain.TaskRepository;
import domen.domain.TaskService;
import domen.domain.model.Task;
import domen.domain.model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TaskRepositorySQLTest {
    @Mock
    private TaskRepository taskRepository;
    @Mock
    Connection mockConnection;
    @Mock
    PreparedStatement mockPreparedStatement;
    @InjectMocks
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldSaveTaskToDatabase() {
        TaskRepositorySQL database = new TaskRepositorySQL();
        Task task = taskService.create("Title", "Description");
        database.save(task);
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

        //when
        taskRepository.save(task);

        // then
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> paramCaptor = ArgumentCaptor.forClass(Object.class);

        verify(mockConnection).prepareStatement(sqlCaptor.capture());
        verify(mockPreparedStatement, times(6)).setObject(anyInt(), paramCaptor.capture());
        verify(mockPreparedStatement).executeUpdate();

        assertEquals("INSERT INTO task (id, title, description, status, start_date_time, finish_date_time)" +
                " VALUES (?::uuid,?,?,?::task_status,?::timestamp,?::timestamp)", sqlCaptor.getValue());

        List<Object> params = paramCaptor.getAllValues();


        assertEquals(UUID.fromString(task.id()), params.get(0));
        assertEquals(task.title(), params.get(1));
        assertEquals(task.description(), params.get(2));
        assertEquals(task.status(), params.get(3));
        assertEquals(task.startDateTime(), params.get(4));
        assertEquals(task.finishDateTime(), params.get(5));


    }
}
