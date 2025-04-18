package domen.domain;

import domen.domain.exception.TaskNotFoundException;
import domen.domain.model.Task;
import domen.domain.model.TaskStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task create(String title, String description) {
        return buildAndSaveTask(title, description, null, null);
    }

    public Task create(String title, String description, LocalDateTime startDateTime, LocalDateTime finishDateTime) {
        validateTaskDateTime(startDateTime, finishDateTime);
        return buildAndSaveTask(title, description, startDateTime, finishDateTime);
    }

    private Task buildAndSaveTask(String title, String description, LocalDateTime startDateTime, LocalDateTime finishDateTime) {
        if (title == null || title.isBlank())
            throw new IllegalArgumentException("Title cannot be null or empty");
        Task task = new Task(UUID.randomUUID().toString(), title, description, TaskStatus.NEW, startDateTime, finishDateTime);
        taskRepository.save(task);
        return task;
    }

    public Task update(String id, String newTitle, String newDescription, TaskStatus newStatus) {
        Task updatedTask = getTask(id).copyWithUpdate(newTitle, newDescription, newStatus);

        taskRepository.update(updatedTask);
        return updatedTask;
    }

    public Task assignTime(String id, LocalDateTime startDateTime, LocalDateTime finishDateTime) {
        validateTaskDateTime(startDateTime, finishDateTime);
        Task updatedTask = getTask(id).copyWithUpdate(startDateTime, finishDateTime);

        taskRepository.update(updatedTask);
        return updatedTask;
    }

    private Task getTask(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID cannot be null or empty");
        }

        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task with id " + id + " not found"));
    }

    private static void validateTaskDateTime(LocalDateTime startDateTime, LocalDateTime finishDateTime) {
        if (finishDateTime == null) return;
        if (startDateTime != null && startDateTime.isAfter(finishDateTime))
            throw new IllegalArgumentException("Start date cannot be after finish time");
        if (finishDateTime.isBefore(LocalDateTime.now()))
            throw new IllegalArgumentException("Finish date cannot be before current time");
    }

    public List<Task> getAllTasks() {
        return taskRepository.getAll();
    }

    public List<Task> getActiveTasks() {
        return filterTasks(Task::isActive);
    }

    public List<Task> getCompletedTasks() {
        return filterTasks(Task::isCompleted);
    }

    public List<Task> getOverdueTasks() {
        return filterTasks(Task::isOverdue);
    }

    private List<Task> filterTasks(Predicate<Task> predicate) {
        return taskRepository.getAll().stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

}
