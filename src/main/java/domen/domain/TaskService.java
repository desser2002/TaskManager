package domen.domain;

import domen.domain.exception.TaskNotFoundException;
import domen.domain.model.Subtask;
import domen.domain.model.Task;
import domen.domain.model.TaskStatus;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TaskService {
    private final TaskRepository taskRepository;
    private final SubtaskService subtaskService;
    private final SubtaskRepository subtaskRepository;

    public TaskService(TaskRepository taskRepository,
                       SubtaskService subtaskService, SubtaskRepository subtaskRepository) {
        this.taskRepository = taskRepository;
        this.subtaskService = subtaskService;
        this.subtaskRepository = subtaskRepository;
    }

    public Task create(String title, String description) {
        return buildAndSaveTask(title, description, null, null);
    }

    public Task create(String title, String description, LocalDateTime startDateTime, LocalDateTime finishDateTime) {
        validateTaskDateTime(startDateTime, finishDateTime);
        return buildAndSaveTask(title, description, startDateTime, finishDateTime);
    }

    public Task update(String id, Set<Subtask> newSubtasks) {
        Task task = getTask(id);
        LinkedHashSet<Subtask> subtasks = new LinkedHashSet<>(newSubtasks);
        Task updatedTask = task.copyWithUpdate(subtasks);
        taskRepository.update(updatedTask);
        syncSubtasks(updatedTask.id(), updatedTask.subtasks());
        return updatedTask;
    }

    public Task update(String id, String newTitle, String newDescription, TaskStatus newStatus) {
        Task task = getTask(id);
        if (newStatus == TaskStatus.DONE && !subtaskService.areAllSubtasksDone(task.id())) {
            throw new IllegalStateException("Cannot mark task as DONE when not all subtasks are DONE");
        }
        Task updatedTask = task.copyWithUpdate(newTitle, newDescription, newStatus);
        taskRepository.update(updatedTask);
        return updatedTask;
    }

    public void delete(String id) {
        Task task = getTask(id);
        Set<Subtask> subtasks = subtaskRepository.getSubtasksByTaskId(task.id());
        for (Subtask subtask : subtasks) {
            subtaskRepository.delete(subtask);
        }
        taskRepository.delete(task.id());
    }

    public Task assignTime(String id, LocalDateTime startDateTime, LocalDateTime finishDateTime) {
        validateTaskDateTime(startDateTime, finishDateTime);
        Task updatedTask = getTask(id).copyWithUpdate(startDateTime, finishDateTime);
        taskRepository.update(updatedTask);
        return updatedTask;
    }

    public List<Task> getAllTasks() {
        return taskRepository.getAll();
    }

    public List<Task> getCompletedTasks() {
        return filterTasks(Task::isCompleted);
    }

    public List<Task> getActiveTasks() {
        return filterTasks(Task::isActive);
    }

    public List<Task> getOverdueTasks() {
        return filterTasks(Task::isOverdue);
    }

    private List<Task> filterTasks(Predicate<Task> predicate) {
        return taskRepository.getAll().stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    private Task buildAndSaveTask(String title, String description,
                                  LocalDateTime startDateTime, LocalDateTime finishDateTime) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
        LinkedHashSet<Subtask> subtasks = new LinkedHashSet<>();
        Task task = new Task(UUID.randomUUID().toString(),
                title,
                description,
                TaskStatus.NEW,
                startDateTime,
                finishDateTime,
                subtasks
        );
        taskRepository.save(task);
        return task;
    }

    private Task getTask(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID cannot be null or empty");
        }
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task with id " + id + " not found"));
    }

    private static void validateTaskDateTime(LocalDateTime startDateTime, LocalDateTime finishDateTime) {
        if (finishDateTime == null) {
            return;
        }
        if (startDateTime != null && startDateTime.isAfter(finishDateTime)) {
            throw new IllegalArgumentException("Start date cannot be after finish time");
        }
        if (finishDateTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Finish date cannot be before current time");
        }
    }

    private void syncSubtasks(String taskId, Set<Subtask> incomingSubtasks) {
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
}
