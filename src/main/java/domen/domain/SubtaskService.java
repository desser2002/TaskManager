package domen.domain;

import domen.domain.exception.TaskNotFoundException;
import domen.domain.model.Subtask;
import domen.domain.model.TaskStatus;

import java.sql.Connection;
import java.util.*;

public class SubtaskService {
    SubtaskRepository subtaskRepository;
    TaskRepository taskRepository;

    public SubtaskService(SubtaskRepository subtaskRepository, TaskRepository taskRepository) {
        this.subtaskRepository = subtaskRepository;
        this.taskRepository = taskRepository;
    }

    Set<Subtask> getSubtasks(String taskId, Connection connection) {
        validateTaskExists(taskId, connection);
        return subtaskRepository.getSubtasksByTaskId(taskId, connection);
    }

    Subtask getSubtask(String taskId, String subtaskId, Connection connection) {
        return getSubtasks(taskId, connection).stream()
                .filter(subtask -> subtask.id().equals(subtaskId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Subtask not found subtask_id:" + subtaskId));
    }

    void addSubtask(String taskId, Subtask subtask, Connection connection) {
        validateTaskExists(taskId, connection);
        subtaskRepository.save(subtask, taskId, connection);
    }

    void updateSubtask(String taskId, Subtask subtask, Connection connection) {
        validateTaskExists(taskId, connection);
        subtaskRepository.update(subtask, connection);
    }

    void deleteSubtask(String taskId, String subtaskId, Connection connection) {
        validateTaskExists(taskId, connection);
        Subtask subtask = getSubtask(taskId, subtaskId, connection);
        subtaskRepository.delete(subtask, connection);
    }

    void syncSubtasks(String taskId, Set<Subtask> incomingSubtasks, Connection connection) {
        validateTaskExists(taskId, connection);
        Set<Subtask> existingSubtasks = subtaskRepository.getSubtasksByTaskId(taskId, connection);
        Map<String, Subtask> existingSubtaskMap = new HashMap<>();
        for (Subtask subtask : existingSubtasks) {
            existingSubtaskMap.put(subtask.id(), subtask);
        }
        Set<String> incomingIds = new HashSet<>();
        for (Subtask subtask : incomingSubtasks) {
            incomingIds.add(subtask.id());
            if (existingSubtaskMap.containsKey(subtask.id())) {
                subtaskRepository.update(subtask, connection);
            } else {
                subtaskRepository.save(subtask, taskId, connection);
            }
        }
        for (Subtask oldSubtask : existingSubtasks) {
            if (!incomingIds.contains(oldSubtask.id())) {
                subtaskRepository.delete(oldSubtask, connection);
            }
        }
    }

    boolean areAllSubtasksDone(String taskId, Connection connection) {
        return getSubtasks(taskId, connection).stream()
                .allMatch(subtask -> subtask.status().equals(TaskStatus.DONE));
    }

    private void validateTaskExists(String taskId, Connection connection) {
        taskRepository.findById(taskId, connection).orElseThrow(() -> new TaskNotFoundException(taskId));
    }
}
