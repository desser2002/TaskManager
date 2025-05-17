package domen.domain;
import domen.domain.model.Subtask;
import domen.domain.model.Task;

import java.sql.Connection;
import java.util.Set;

public interface SubtaskRepository {
    void update(Subtask subtask, Connection connection);

    void save(Subtask subtask, String taskId, Connection connection);

    void delete(Subtask oldSubtask, Connection connection);

    Set<Subtask> getSubtasksByTaskId(String taskId, Connection connection);

    void syncSubtasks(Task task, Connection connection);
}
