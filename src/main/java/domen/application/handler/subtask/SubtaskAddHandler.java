package domen.application.handler.subtask;

import domen.domain.SubtaskService;
import org.slf4j.Logger;

import java.util.Scanner;

public class SubtaskAddHandler extends AbstractSubtaskHandler {
    public SubtaskAddHandler(SubtaskService subtaskService, Scanner scanner, Logger logger) {
        super(scanner, logger, subtaskService);
    }

    @Override
    public String name() {
        return "subtask add";
    }

    @Override
    public void handle() {
        logger.info("=== ADDING SUBTASK ===");
        logger.info("Enter task ID to attach subtask:");
        String taskId = scanner.nextLine();
        logger.info("Enter subtask title:");
        String title = scanner.nextLine();
        logger.info("Enter subtask description (optional, press Enter to skip):");
        String description = scanner.nextLine();
        try {
            subtaskService.saveSubtask(taskId, title, description);
            logger.info("Subtask added successfully.");
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to add subtask: {}", e.getMessage());
        }
    }
}
