package domen.domain.model;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDateTime;


class TaskTest {

    @ParameterizedTest
    @CsvSource({
            "NEW, true",
            "IN_PROGRESS, true",
            "DONE, false"
    })
    void isActive_shouldReturnExpectedValue(TaskStatus status, boolean expected) {
        Task task = new Task("id", "title", "desc", status, null, null,null);
        assertEquals(expected, task.isActive());
    }

    @ParameterizedTest
    @CsvSource({
            "NEW, false",
            "IN_PROGRESS, false",
            "DONE, true"
    })
    void isCompleted_shouldReturnExpectedValue(TaskStatus status, boolean expected) {
        Task task = new Task("id", "title", "desc", status, null, null,null);
        assertEquals(expected, task.isCompleted());
    }

    @Test
    void shouldReturnTrueIfFinishTimeInPastAndNotCompleted() {
        LocalDateTime past = LocalDateTime.now().minusDays(1);
        Task task = new Task("1", "Title", "Desc", TaskStatus.NEW, null, past,null);

        assertTrue(task.isOverdue());
    }

    @Test
    void shouldReturnFalseIfFinishedButAlreadyCompleted() {
        LocalDateTime past = LocalDateTime.now().minusDays(1);
        Task task = new Task("2", "Title", "Desc", TaskStatus.DONE, null, past,null);

        assertFalse(task.isOverdue());
    }

    @Test
    void shouldReturnFalseIfFinishTimeInFuture() {
        LocalDateTime future = LocalDateTime.now().plusDays(1);
        Task task = new Task("3", "Title", "Desc", TaskStatus.NEW, null, future,null);

        assertFalse(task.isOverdue());
    }

    @Test
    void shouldReturnFalseIfFinishTimeIsNull() {
        Task task = new Task("4", "Title", "Desc", TaskStatus.NEW, null, null,null);

        assertFalse(task.isOverdue());
    }

}