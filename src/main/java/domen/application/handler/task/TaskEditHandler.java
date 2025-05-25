package domen.application.handler.task;

import domen.domain.TaskService;
import domen.domain.exception.TaskNotFoundException;
import domen.domain.model.TaskStatus;
import org.slf4j.Logger;

import java.util.Scanner;

public class TaskEditHandler extends AbstractTaskHandler {
    public TaskEditHandler(TaskService taskService, Scanner scanner, Logger logger) {
        super(taskService, scanner, logger);
    }

    @Override
    public String name() {
        return "task update";
    }

    @Override
    public void handle() {
        logger.info("==== EDIT TASK ====");
        logger.info("Enter task ID:");
        String id = scanner.nextLine();
        logger.info("Enter new title (leave empty to skip):");
        String newTitle = scanner.nextLine();
        logger.info("Enter new description (leave empty to skip):");
        String newDescription = scanner.nextLine();
        logger.info("Enter new status (NEW, IN_PROGRESS, DONE) or leave empty to skip:");
        String statusInput = scanner.nextLine();
        TaskStatus newStatus = null;
        if (!statusInput.isBlank()) {
            try {
                newStatus = TaskStatus.valueOf(statusInput.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                logger.warn("⚠️ Invalid status. Allowed: NEW, IN_PROGRESS, DONE.");
                return;
            }
        }
        try {
            taskService.update(id,
                    newTitle.isBlank() ? null : newTitle,
                    newDescription.isBlank() ? null : newDescription,
                    newStatus);
            logger.info("✅ Task updated successfully.");
        } catch (TaskNotFoundException e) {
            logger.warn("⚠️ Task not found: {}", e.getMessage());
        } catch (IllegalStateException e) {
            logger.warn("⚠️ Update error: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("❌ Failed to update task: {}", e.getMessage());
        }
    }

    @Override
    public String getCommandName() {
        return name();
    }
}

