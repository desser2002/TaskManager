package domen.application.handler.task;

import domen.application.handler.ConsoleHandler;
import domen.domain.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

public class TaskCreateHandler implements ConsoleHandler {
    private final TaskService taskService;
    private final Scanner scanner;
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskCreateHandler.class);

    public TaskCreateHandler(TaskService taskService, Scanner scanner) {
        this.taskService = taskService;
        this.scanner = scanner;
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
