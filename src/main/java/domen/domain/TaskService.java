package domen.domain;

import domen.domain.exception.TaskNotFoundException;
import domen.domain.model.Subtask;
import domen.domain.model.Task;
import domen.domain.model.TaskStatus;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TaskService {
    private final TaskRepository taskRepository;
    private final SubtaskService subtaskService;

    public TaskService(TaskRepository taskRepository, SubtaskService subtaskService) {
        this.taskRepository = taskRepository;
        this.subtaskService = subtaskService;
    }

    public Task create(String title, String description, Connection connection) {
        return buildAndSaveTask(title, description, null, null, connection);
    }

    public Task create(String title, String description, LocalDateTime startDateTime, LocalDateTime finishDateTime, Connection connection) {
        validateTaskDateTime(startDateTime, finishDateTime);
        return buildAndSaveTask(title, description, startDateTime, finishDateTime, connection);
    }

    public Task update(String id, String newTitle, String newDescription, TaskStatus newStatus, Connection connection) {
        Task task = getTask(id, connection);
        if (newStatus == TaskStatus.DONE && !subtaskService.areAllSubtasksDone(task.id(), connection)) {
            throw new IllegalStateException("Cannot mark task as DONE when not all subtasks are DONE");
        }
        Task updatedTask = task.copyWithUpdate(newTitle, newDescription, newStatus);
        taskRepository.update(updatedTask, connection);
        subtaskService.syncSubtasks(updatedTask.id(), updatedTask.subtasks(), connection);
        return updatedTask;
    }

    public Task assignTime(String id, LocalDateTime startDateTime, LocalDateTime finishDateTime, Connection connection) {
        validateTaskDateTime(startDateTime, finishDateTime);
        Task updatedTask = getTask(id, connection).copyWithUpdate(startDateTime, finishDateTime);
        taskRepository.update(updatedTask, connection);
        return updatedTask;
    }

    public List<Task> getAllTasks(Connection connection) {
        return taskRepository.getAll(connection);
    }

    public List<Task> getCompletedTasks(Connection connection) {
        return filterTasks(Task::isCompleted, connection);
    }

    public List<Task> getOverdueTasks(Connection connection) {
        return filterTasks(Task::isOverdue, connection);
    }

    private List<Task> filterTasks(Predicate<Task> predicate, Connection connection) {
        return taskRepository.getAll(connection).stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    private Task buildAndSaveTask(String title, String description,
                                  LocalDateTime startDateTime, LocalDateTime finishDateTime, Connection connection) {
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
        taskRepository.save(task, connection);
        return task;
    }

    private Task getTask(String id, Connection connection) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID cannot be null or empty");
        }
        return taskRepository.findById(id, connection)
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
}
