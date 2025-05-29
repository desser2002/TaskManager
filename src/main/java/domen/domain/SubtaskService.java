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

    public Set<Subtask> getSubtasks(String taskId) {
        validateTaskExists(taskId);
        return subtaskRepository.getSubtasksByTaskId(taskId);
    }

    public Subtask getSubtask(String taskId, String subtaskId) {
        return getSubtasks(taskId).stream()
                .filter(subtask -> subtask.id().equals(subtaskId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Subtask not found subtask_id:" + subtaskId));
    }

    public void saveSubtask(String taskId, String title, String description) {
        validateTaskExists(taskId);
        Subtask subtask = new Subtask(UUID.randomUUID().toString(), title, description, TaskStatus.NEW);
        subtaskRepository.save(subtask, taskId);
    }

    public void updateSubtask(String taskId, Subtask subtask) {
        validateTaskExists(taskId);
        subtaskRepository.update(subtask);
    }

    public void deleteSubtask(String taskId, String subtaskId) {
        validateTaskExists(taskId);
        Subtask subtask = getSubtask(taskId, subtaskId);
        subtaskRepository.delete(subtask);
    }

    public void moveSubtaskToAnotherTask(String subtaskId, String newTaskId) {
        if (subtaskId == null || subtaskId.isBlank()) {
            throw new IllegalArgumentException("Subtask ID cannot be null or empty");
        }
        if (newTaskId == null || newTaskId.isBlank()) {
            throw new IllegalArgumentException("Target Task ID cannot be null or empty");
        }
        validateTaskExists(newTaskId);
        subtaskRepository.move(subtaskId, newTaskId);
    }

    boolean areAllSubtasksDone(String taskId) {
        return getSubtasks(taskId).stream()
                .allMatch(subtask -> subtask.status().equals(TaskStatus.DONE));
    }

    private void validateTaskExists(String taskId) {
        taskRepository.findById(taskId).orElseThrow(() -> new TaskNotFoundException(taskId));
    }
}
