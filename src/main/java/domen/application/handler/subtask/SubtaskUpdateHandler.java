package domen.application.handler.subtask;

import domen.domain.SubtaskService;
import domen.domain.model.Subtask;
import domen.domain.model.TaskStatus;
import org.slf4j.Logger;

import java.util.Scanner;

public class SubtaskUpdateHandler extends AbstractSubtaskHandler {
    public SubtaskUpdateHandler(SubtaskService subtaskService, Scanner scanner, Logger logger) {
        super(scanner, logger, subtaskService);
    }

    @Override
    public String name() {
        return "subtask update";
    }

    @Override
    public void handle() {
        logger.info("=== UPDATING SUBTASK ===");
        logger.info("Enter task ID that contains the subtask:");
        String taskId = scanner.nextLine();
        logger.info("Enter subtask ID to update:");
        String subtaskId = scanner.nextLine();
        logger.info("Enter new subtask title (leave blank to keep current):");
        String title = scanner.nextLine();
        logger.info("Enter new subtask description (leave blank to keep current):");
        String description = scanner.nextLine();
        logger.info("Enter new subtask status (NEW, IN_PROGRESS, DONE):");
        String statusInput = scanner.nextLine();
        try {
            TaskStatus status = TaskStatus.valueOf(statusInput.trim().toUpperCase());
            Subtask updatedSubtask = new Subtask(subtaskId, title, description, status);
            subtaskService.updateSubtask(taskId, updatedSubtask);
            logger.info("Subtask updated successfully.");
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to update subtask: {}", e.getMessage());
        }
    }

    @Override
    public String getCommandName() {
        return name();
    }
}
