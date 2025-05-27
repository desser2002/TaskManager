package domen.application.handler.task;

import domen.domain.TaskService;
import domen.domain.exception.TaskNotFoundException;
import org.slf4j.Logger;

import java.util.Scanner;

public class TaskDeleteHandler extends AbstractTaskHandler {
    public TaskDeleteHandler(TaskService taskService, Scanner scanner, Logger logger) {
        super(taskService, scanner, logger);
    }

    @Override
    public String name() {
        return "task delete";
    }

    @Override
    public void handle() {
        logger.info("==== DELETE TASK ====");
        logger.info("Enter task ID to delete:");
        String id = scanner.nextLine();
        try {
            taskService.delete(id);
            logger.info("Task deleted successfully.");
        } catch (TaskNotFoundException e) {
            logger.warn("Task not found: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Failed to delete task: {}", e.getMessage());
        }
    }

    @Override
    public String getCommandName() {
        return name();
    }
}

