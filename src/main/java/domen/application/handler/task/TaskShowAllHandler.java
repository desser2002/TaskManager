package domen.application.handler.task;

import domen.application.handler.ConsoleHandler;
import domen.application.util.TaskPrinter;
import domen.domain.TaskService;
import domen.domain.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Scanner;

public class TaskShowAllHandler implements ConsoleHandler {
    private final TaskService taskService;
    private static Logger logger = LoggerFactory.getLogger(TaskCreateHandler.class);

    public TaskShowAllHandler(TaskService taskService, Scanner scanner) {
        this.taskService = taskService;
    }

    @Override
    public void handle() {
        List<Task> tasks = taskService.getAllTasks();
        logger.info("=== LIST OF TASKS ===");
        TaskPrinter.printTable(logger,tasks);
    }
}
