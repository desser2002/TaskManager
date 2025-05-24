package domen.domain;

import domen.domain.model.Task;
import domen.domain.model.TaskStatus;

import java.time.LocalDate;
import java.util.List;

public class TaskReportService {
    private final TaskRepository taskRepository;

    public TaskReportService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> delayedTasks() {
        return taskRepository.getDelayedTasks();
    }

    public List<Task> getTaskListByStatus(TaskStatus status) {
        return taskRepository.getTasksByStatus(status);
    }

    public long countTasksDoneAtWeek(LocalDate dateInWeek) {
        return taskRepository.countTaskDoneAtWeek(dateInWeek);
    }
}
