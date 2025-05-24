package domen.application.handler.task;

import domen.application.handler.ConsoleHandler;
import domen.domain.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class TaskAssignTimeHandler implements ConsoleHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskAssignTimeHandler.class);
    private final TaskService taskService;
    private final Scanner scanner;

    public TaskAssignTimeHandler(TaskService taskService, Scanner scanner) {
        this.taskService = taskService;
        this.scanner = scanner;
    }

    @Override
    public void handle() {
        LOGGER.info("=== ASSIGN TIME TO TASK ===");
        LOGGER.info("Enter task ID:");
        String id = scanner.nextLine();
        LocalDateTime start = readDate("Enter start date (yyyy-MM-dd HH:mm) or leave empty:");
        LocalDateTime finish = readDate("Enter finish date (yyyy-MM-dd HH:mm) or leave empty:");
        try {
            taskService.assignTime(id, start, finish);
            LOGGER.info("Assign time successful.");
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Warning: {}", e.getMessage());
        }
    }

    private LocalDateTime readDate(String message) {
        LOGGER.info(message);
        String input = scanner.nextLine();
        if (input == null || input.isBlank()) {
            return null;
        }
        try {
            return LocalDateTime.parse(input, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        } catch (DateTimeParseException e) {
            LOGGER.warn("Invalid format. Use yyyy-MM-dd HH:mm. Skipping this date.");
            return null;
        }
    }
}
