package domen.domain;

import domen.domain.model.Subtask;

import java.util.Set;

public interface SubtaskRepository {
    void update(Subtask subtask);

    void save(Subtask subtask, String taskId);

    void delete(Subtask oldSubtask);

    Set<Subtask> getSubtasksByTaskId(String taskId);
}
