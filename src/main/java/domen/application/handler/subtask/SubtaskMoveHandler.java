package domen.application.handler.subtask;

import domen.domain.SubtaskService;
import org.slf4j.Logger;

import java.util.Scanner;

public class SubtaskMoveHandler extends AbstractSubtaskHandler {
    public SubtaskMoveHandler(SubtaskService subtaskService, Scanner scanner, Logger logger) {
        super(scanner, logger, subtaskService);
    }

    @Override
    public String name() {
        return "subtask move";
    }

    @Override
    public void handle() {
        try {
            logger.info("Enter the ID of the subtask to move:");
            String subtaskId = scanner.nextLine().trim();
            logger.info("Enter the ID of the new task to assign this subtask to:");
            String newTaskId = scanner.nextLine().trim();
            subtaskService.moveSubtaskToAnotherTask(subtaskId, newTaskId);
            logger.info("Subtask successfully moved to new task.");
        } catch (Exception e) {
            logger.error("Error while moving subtask: {}", e.getMessage());
        }
    }

    @Override
    public String getCommandName() {
        return name();
    }
}
