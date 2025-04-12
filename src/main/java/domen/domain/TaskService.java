package domen.domain;

import domen.domain.exception.TaskNotFoundException;
import domen.domain.model.Task;
import domen.domain.model.TaskSatus;

public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task create(String title, String description) {
        if (title == null || title.isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
        Task task = new Task(title, description, TaskSatus.NEW);
        taskRepository.save(task);
        return task;
    }

    public Task upadateTask(String id, String newTitle, String newDescription, TaskSatus newStatus) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("ID cannot be null or empty");
        }

        Task existingTask = taskRepository.findById(id);
        if (existingTask == null) {
            throw new TaskNotFoundException("Task with id " + id + " not found");
        }
        Task updatedTask = existingTask.copyWith(newTitle, newDescription, newStatus);
        taskRepository.update(updatedTask);
        return updatedTask;
    }
}
