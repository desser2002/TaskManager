package domen.application.handler.task;

import domen.domain.TaskService;
import domen.domain.exception.TaskNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

public class TaskDeleteHandler extends AbstractTaskHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskDeleteHandler.class);

    protected TaskDeleteHandler(TaskService taskService, Scanner scanner, Logger logger) {
        super(taskService, scanner, logger);
    }

    @Override
    public void handle() {
        LOGGER.info("==== DELETE TASK ====");
        LOGGER.info("Enter task ID to delete:");
        String id = scanner.nextLine();
        try {
            taskService.delete(id);
            LOGGER.info("Task deleted successfully.");
        } catch (TaskNotFoundException e) {
            LOGGER.warn("Task not found: {}", e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to delete task: {}", e.getMessage());
        }
    }
}

