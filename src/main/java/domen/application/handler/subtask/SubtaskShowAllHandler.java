package domen.application.handler.subtask;

import domen.domain.SubtaskService;
import domen.domain.model.Subtask;
import org.slf4j.Logger;

import java.util.Scanner;
import java.util.Set;

public class SubtaskShowAllHandler extends AbstractSubtaskHandler {
    public SubtaskShowAllHandler(SubtaskService subtaskService, Scanner scanner, Logger logger) {
        super(scanner, logger, subtaskService);
    }

    @Override
    public String name() {
        return "subtask all";
    }

    @Override
    public void handle() {
        logger.info("=== LIST SUBTASKS FOR TASK ===");
        logger.info("Enter task ID:");
        String taskId = scanner.nextLine();
        try {
            Set<Subtask> subtasks = subtaskService.getSubtasks(taskId);
            if (subtasks.isEmpty()) {
                logger.info("No subtasks found for this task.");
            } else {
                for (Subtask subtask : subtasks) {
                    logger.info("ID: {}, Title: {}, Status: {}", subtask.id(), subtask.title(), subtask.status());
                }
            }
        } catch (IllegalArgumentException e) {
            logger.warn("Error: {}", e.getMessage());
        }
    }

    @Override
    public String getCommandName() {
        return name();
    }
}
