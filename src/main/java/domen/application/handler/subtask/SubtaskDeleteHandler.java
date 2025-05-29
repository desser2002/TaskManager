package domen.application.handler.subtask;

import domen.domain.SubtaskService;
import org.slf4j.Logger;

import java.util.Scanner;

public class SubtaskDeleteHandler extends AbstractSubtaskHandler {
    public SubtaskDeleteHandler(SubtaskService subtaskService, Scanner scanner, Logger logger) {
        super(scanner, logger, subtaskService);
    }

    @Override
    public String name() {
        return "subtask delete";
    }

    @Override
    public void handle() {
        logger.info("=== DELETING SUBTASK ===");
        logger.info("Enter task ID that contains the subtask:");
        String taskId = scanner.nextLine();
        logger.info("Enter subtask ID to delete:");
        String subtaskId = scanner.nextLine();
        try {
            subtaskService.deleteSubtask(taskId, subtaskId);
            logger.info("Subtask deleted successfully.");
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to delete subtask: {}", e.getMessage());
        }
    }

    @Override
    public String getCommandName() {
        return name();
    }
}
