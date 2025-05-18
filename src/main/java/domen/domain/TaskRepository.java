package domen.domain;

import domen.domain.model.Task;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public interface TaskRepository {
    void save(Task task, Connection connection);

    Optional<Task> findById(String id, Connection connection);

    void update(Task updatedTask, Connection connection);

    List<Task> getAll(Connection connection);
}
