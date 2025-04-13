package domen.domain;

import domen.domain.exception.TaskNotFoundException;
import domen.domain.model.Task;
import domen.domain.model.TaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task create(String title, String description, LocalDateTime startDateTime, LocalDateTime finishDateTime) {
        if (title == null || title.isEmpty())
            throw new IllegalArgumentException("Title cannot be null or empty");

        validateTaskDateTime(startDateTime, finishDateTime);

        Task task = new Task(UUID.randomUUID().toString(), title, description, TaskStatus.NEW, startDateTime, finishDateTime);
        taskRepository.save(task);
        return task;
    }

    public Task update(String id, String newTitle, String newDescription, TaskStatus newStatus, LocalDateTime newStartDateTime, LocalDateTime newFinishDateTime) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("ID cannot be null or empty");
        }


        Task updatedTask = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task with id " + id + " not found"))
                .copyWith(newTitle, newDescription, newStatus, newStartDateTime, newFinishDateTime);

        LocalDateTime updatedStartDateTime = newStartDateTime != null ? newStartDateTime : updatedTask.startDateTime();
        LocalDateTime updatedFinishDateTime = newFinishDateTime != null ? newFinishDateTime : updatedTask.finishDateTime();

        validateTaskDateTime(updatedStartDateTime, updatedFinishDateTime);

        taskRepository.update(updatedTask);
        return updatedTask;
    }

    private static void validateTaskDateTime(LocalDateTime startDateTime, LocalDateTime finishDateTime) {
        if (finishDateTime == null) return;
        if (startDateTime != null && startDateTime.isAfter(finishDateTime))
            throw new IllegalArgumentException("Start date cannot be after finish time");
        if (finishDateTime.isBefore(LocalDateTime.now()))
            throw new IllegalArgumentException("Finish date cannot be before current time");
    }
}
