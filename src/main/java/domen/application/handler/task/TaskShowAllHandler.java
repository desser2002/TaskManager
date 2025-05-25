package domen.application.handler.task;

import domen.application.util.TaskPrinter;
import domen.domain.TaskService;
import domen.domain.model.Task;
import org.slf4j.Logger;

import java.util.List;
import java.util.Scanner;

public class TaskShowAllHandler extends AbstractTaskHandler {
    protected TaskShowAllHandler(TaskService taskService, Scanner scanner, Logger logger) {
        super(taskService, scanner, logger);
    }

    @Override
    public String name() {
        return "task all";
    }

    @Override
    public void handle() {
        List<Task> tasks = taskService.getAllTasks();
        logger.info("=== LIST OF TASKS ===");
        TaskPrinter.printTable(logger, tasks);
    }
}
