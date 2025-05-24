package domen.application.handler.task;

import domen.domain.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

public class TaskCreateHandler extends AbstractTaskHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskCreateHandler.class);

    protected TaskCreateHandler(TaskService taskService, Scanner scanner, Logger logger) {
        super(taskService, scanner, logger);
    }

    @Override
    public void handle() {
        LOGGER.info("==== Creating new task ====");
        LOGGER.info("Enter task name: ");
        String taskName = scanner.nextLine();
        LOGGER.info("Enter task description (optional, Enter to skip): ");
        String taskDescription = scanner.nextLine();
        LOGGER.info("Enter task priority: ");
        taskService.create(taskName, taskDescription);
        LOGGER.info("Task successfully created: title='{}', description='{}'",
                taskName, taskDescription.isBlank() ? "(empty)" : taskDescription);
    }
}
