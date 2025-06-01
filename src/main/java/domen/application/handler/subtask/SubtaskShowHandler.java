package domen.application.handler.subtask;

import domen.domain.SubtaskService;
import domen.domain.model.Subtask;
import org.slf4j.Logger;

import java.util.Scanner;

public class SubtaskShowHandler extends AbstractSubtaskHandler {
    public SubtaskShowHandler(SubtaskService subtaskService, Scanner scanner, Logger logger) {
        super(scanner, logger, subtaskService);
    }

    @Override
    public String name() {
        return "subtask";
    }

    @Override
    public void handle() {
        logger.info("=== VIEW SUBTASK ===");
        logger.info("Enter task ID:");
        String taskId = scanner.nextLine();
        logger.info("Enter subtask ID:");
        String subtaskId = scanner.nextLine();
        try {
            Subtask subtask = subtaskService.getSubtask(taskId, subtaskId);
            if (subtask != null) {
                logger.info("Subtask ID: {}", subtask.id());
                logger.info("Title: {}", subtask.title());
                logger.info("Description: {}", subtask.description());
                logger.info("Status: {}", subtask.status());
            } else {
                logger.info("Subtask not found.");
            }
        } catch (IllegalArgumentException e) {
            logger.warn("Error: {}", e.getMessage());
        }
    }
}
