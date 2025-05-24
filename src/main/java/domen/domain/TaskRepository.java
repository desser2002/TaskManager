package domen.domain;

import domen.domain.model.Task;
import domen.domain.model.TaskStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TaskRepository {
    void save(Task task);

    Optional<Task> findById(String id);

    void update(Task updatedTask);

    List<Task> getAll();

    List<Task> getDelayedTasks();

    List<Task> getTasksByStatus(TaskStatus status);

    long countTaskDoneAtWeek(LocalDate dateInWeek);

    void delete(String taskId);
}
