package domen.domain;

import domen.domain.exception.TaskNotFoundException;
import domen.domain.model.Subtask;
import domen.domain.model.TaskStatus;

import java.util.*;

public class SubtaskService {
    private final SubtaskRepository subtaskRepository;
    private final TaskRepository taskRepository;

    public SubtaskService(SubtaskRepository subtaskRepository, TaskRepository taskRepository) {
        this.subtaskRepository = subtaskRepository;
        this.taskRepository = taskRepository;
    }

    Set<Subtask> getSubtasks(String taskId) {
        validateTaskExists(taskId);
        return subtaskRepository.getSubtasksByTaskId(taskId);
    }

    Subtask getSubtask(String taskId, String subtaskId) {
        return getSubtasks(taskId).stream()
                .filter(subtask -> subtask.id().equals(subtaskId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Subtask not found subtask_id:" + subtaskId));
    }

    void saveSubtask(String taskId, String title, String description) {
        validateTaskExists(taskId);
        Subtask subtask = new Subtask(UUID.randomUUID().toString(), title, description, TaskStatus.NEW);
        subtaskRepository.save(subtask, taskId);
    }

    void updateSubtask(String taskId, Subtask subtask) {
        validateTaskExists(taskId);
        subtaskRepository.update(subtask);
    }

    void deleteSubtask(String taskId, String subtaskId) {
        validateTaskExists(taskId);
        Subtask subtask = getSubtask(taskId, subtaskId);
        subtaskRepository.delete(subtask);
    }

    boolean areAllSubtasksDone(String taskId) {
        return getSubtasks(taskId).stream()
                .allMatch(subtask -> subtask.status().equals(TaskStatus.DONE));
    }

    private void validateTaskExists(String taskId) {
        taskRepository.findById(taskId).orElseThrow(() -> new TaskNotFoundException(taskId));
    }
}
