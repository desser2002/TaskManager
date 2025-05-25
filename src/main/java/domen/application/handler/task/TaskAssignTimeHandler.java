package domen.application.handler.task;

import domen.domain.TaskService;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class TaskAssignTimeHandler extends AbstractTaskHandler {
    public TaskAssignTimeHandler(TaskService taskService, Scanner scanner) {
        super(taskService, scanner, LoggerFactory.getLogger(TaskAssignTimeHandler.class));
    }

    @Override
    public String name() {
        return "task update time";
    }

    @Override
    public void handle() {
        logger.info("=== ASSIGN TIME TO TASK ===");
        logger.info("Enter task ID:");
        String id = scanner.nextLine();
        LocalDateTime start = readDate("Enter start date (yyyy-MM-dd HH:mm) or leave empty:");
        LocalDateTime finish = readDate("Enter finish date (yyyy-MM-dd HH:mm) or leave empty:");
        try {
            taskService.assignTime(id, start, finish);
            logger.info("✅ Task time assigned successfully.");
        } catch (IllegalArgumentException e) {
            logger.warn("⚠️ {}", e.getMessage());
        }
    }

    private LocalDateTime readDate(String prompt) {
        logger.info(prompt);
        String input = scanner.nextLine();
        if (input == null || input.isBlank()) {
            return null;
        }
        try {
            return LocalDateTime.parse(input, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        } catch (DateTimeParseException e) {
            logger.warn("Invalid date format. Please use yyyy-MM-dd HH:mm.");
            return null;
        }
    }
}
