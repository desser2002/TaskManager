package domen.application.handler.task;

import domen.domain.TaskService;
import org.slf4j.Logger;

import java.util.Scanner;

public class TaskCreateHandler extends AbstractTaskHandler {
    protected TaskCreateHandler(TaskService taskService, Scanner scanner, Logger logger) {
        super(taskService, scanner, logger);
    }

    @Override
    public String name() {
        return "task create";
    }

    @Override
    public void handle() {
        logger.info("==== Creating new task ====");
        logger.info("Enter task name: ");
        String taskName = scanner.nextLine();
        logger.info("Enter task description (optional, Enter to skip): ");
        String taskDescription = scanner.nextLine();
        logger.info("Enter task priority: ");
        taskService.create(taskName, taskDescription);
        logger.info("Task successfully created: title='{}', description='{}'",
                taskName, taskDescription.isBlank() ? "(empty)" : taskDescription);
    }
}
