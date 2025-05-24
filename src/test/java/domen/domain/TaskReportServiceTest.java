package domen.domain;

import domen.domain.model.Task;
import domen.domain.model.TaskStatus;
import net.bytebuddy.asm.Advice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskReportServiceTest {
    @Mock
    private TaskRepository taskRepository;
    @InjectMocks
    private TaskReportService taskReportService;

    @Test
    void shouldReturnDelayedTasks() {
        //given
        List<Task> expected = new ArrayList<>(List.of(mock(Task.class), mock(Task.class), mock(Task.class)));
        when(taskRepository.getDelayedTasks()).thenReturn(expected);
        //when
        List<Task> result = taskReportService.delayedTasks();
        //then
        assertEquals(expected, result);
        verify(taskRepository).getDelayedTasks();
    }

    @Test
    void shouldReturnTasksByStatus() {
        //given
        TaskStatus status = TaskStatus.IN_PROGRESS;
        List<Task> expected = new ArrayList<>(List.of(mock(Task.class), mock(Task.class), mock(Task.class)));
        when(taskRepository.getTasksByStatus(status)).thenReturn(expected);
        //when
        List<Task> result = taskReportService.getTaskListByStatus(status);
        //then
        assertEquals(expected, result);
        verify(taskRepository).getTasksByStatus(status);
    }

    @Test
    void shouldCountTasksDoneAtWeek() {
        //given
        LocalDate date = LocalDate.of(2020, 1, 1);
        long expectedCount = 3222332232L;
        when(taskRepository.countTaskDoneAtWeek(date)).thenReturn(expectedCount);
        //when
        long result = taskReportService.countTasksDoneAtWeek(date);
        //then
        assertEquals(expectedCount, result);
        verify(taskRepository).countTaskDoneAtWeek(date);
    }
}