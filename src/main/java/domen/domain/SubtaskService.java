package domen.domain;

import domen.domain.exception.TaskNotFoundException;
import domen.domain.model.Subtask;
import domen.domain.model.TaskStatus;

import java.util.*;

public class SubtaskService {
    SubtaskRepository subtaskRepository;
    TaskRepository taskRepository;

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

    void saveSubtask(String taskId, Subtask subtask) {
        validateTaskExists(taskId);
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

    void syncSubtasks(String taskId, Set<Subtask> incomingSubtasks) {
        validateTaskExists(taskId);
        Set<Subtask> existingSubtasks = subtaskRepository.getSubtasksByTaskId(taskId);
        Map<String, Subtask> existingSubtaskMap = new HashMap<>();
        for (Subtask subtask : existingSubtasks) {
            existingSubtaskMap.put(subtask.id(), subtask);
        }
        Set<String> incomingIds = new HashSet<>();
        for (Subtask subtask : incomingSubtasks) {
            incomingIds.add(subtask.id());
            if (existingSubtaskMap.containsKey(subtask.id())) {
                subtaskRepository.update(subtask);
            } else {
                subtaskRepository.save(subtask, taskId);
            }
        }
        for (Subtask oldSubtask : existingSubtasks) {
            if (!incomingIds.contains(oldSubtask.id())) {
                subtaskRepository.delete(oldSubtask);
            }
        }
    }

    boolean areAllSubtasksDone(String taskId) {
        return getSubtasks(taskId).stream()
                .allMatch(subtask -> subtask.status().equals(TaskStatus.DONE));
    }

    private void validateTaskExists(String taskId) {
        taskRepository.findById(taskId).orElseThrow(() -> new TaskNotFoundException(taskId));
    }
}
