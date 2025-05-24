package domen.application.handler.task;

import domen.application.handler.ConsoleHandler;
import domen.domain.TaskService;
import domen.domain.model.TaskStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

public class TaskUpdateStatusHandler implements ConsoleHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskUpdateStatusHandler.class);
    private final TaskService taskService;
    private final Scanner scanner;

    public TaskUpdateStatusHandler(TaskService taskService, Scanner scanner) {
        this.taskService = taskService;
        this.scanner = scanner;
    }

    @Override
    public void handle() {
        LOGGER.info("==== UPDATE TASK STATUS ====");
        LOGGER.info("Enter task ID:");
        String id = scanner.nextLine();
        LOGGER.info("Enter new status (NEW, IN_PROGRESS, DONE):");
        String statusInput = scanner.nextLine().trim().toUpperCase();
        try {
            TaskStatus status = TaskStatus.valueOf(statusInput);
            taskService.update(id, null, null, status);
            LOGGER.info("Task status updated to {}", status);
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Invalid status value or task not found: {}", e.getMessage());
        }
    }
}

