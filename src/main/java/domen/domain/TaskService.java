package domen.domain;

import domen.domain.model.Task;

public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task create(String title) {
        if (title == null || title.isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
        Task task = new Task(title);
        taskRepository.save(task);
        return task;
    }
}
