package domen.domain;

import domen.domain.model.Task;

public interface TaskRepository {
    void save(Task task);
}
