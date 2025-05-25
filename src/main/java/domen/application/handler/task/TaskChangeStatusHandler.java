package domen.application.handler.task;

import domen.domain.TaskService;
import domen.domain.model.TaskStatus;
import org.slf4j.Logger;

import java.util.Scanner;

public class TaskChangeStatusHandler extends AbstractTaskHandler {
    public TaskChangeStatusHandler(TaskService taskService, Scanner scanner, Logger logger) {
        super(taskService, scanner, logger);
    }

    @Override
    public String name() {
        return "task update status";
    }

    @Override
    public void handle() {
        logger.info("=== CHANGE TASK STATUS ===");
        logger.info("Enter task ID:");
        String taskId = scanner.nextLine();
        logger.info("Enter new status (NEW, IN_PROGRESS, DONE):");
        String statusInput = scanner.nextLine();
        try {
            TaskStatus newStatus = TaskStatus.valueOf(statusInput.trim().toUpperCase());
            taskService.update(taskId, null, null, newStatus);
            logger.info("✅ Status changed successfully.");
        } catch (IllegalArgumentException e) {
            logger.warn("⚠️ Invalid status or task not found: {}", e.getMessage());
        }
    }

    @Override
    public String getCommandName() {
        return name();
    }
}

