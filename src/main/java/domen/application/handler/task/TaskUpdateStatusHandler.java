package domen.application.handler.task;

import domen.domain.TaskService;
import domen.domain.model.TaskStatus;
import org.slf4j.Logger;

import java.util.Scanner;

public class TaskUpdateStatusHandler extends AbstractTaskHandler {
    protected TaskUpdateStatusHandler(TaskService taskService, Scanner scanner, Logger logger) {
        super(taskService, scanner, logger);
    }

    @Override
    public String name() {
        return "task update status";
    }

    @Override
    public void handle() {
        logger.info("==== UPDATE TASK STATUS ====");
        logger.info("Enter task ID:");
        String id = scanner.nextLine();
        logger.info("Enter new status (NEW, IN_PROGRESS, DONE):");
        String statusInput = scanner.nextLine().trim().toUpperCase();
        try {
            TaskStatus status = TaskStatus.valueOf(statusInput);
            taskService.update(id, null, null, status);
            logger.info("Task status updated to {}", status);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid status value or task not found: {}", e.getMessage());
        }
    }

    @Override
    public String getCommandName() {
        return name();
    }
}

