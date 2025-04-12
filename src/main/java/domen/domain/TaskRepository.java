package domen.domain;

import domen.domain.model.Task;

import java.util.Optional;

public interface TaskRepository {
    void save(Task task);

    Optional<Task> findById(String id);

    void update(Task updatedTask);
}
