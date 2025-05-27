package domen.application.handler.task;

import domen.application.handler.AbstractConsoleHandler;
import domen.domain.TaskService;
import org.slf4j.Logger;

import java.util.Scanner;

public abstract class AbstractTaskHandler extends AbstractConsoleHandler {
    protected final TaskService taskService;

    protected AbstractTaskHandler(TaskService taskService, Scanner scanner, Logger logger) {
        super(scanner, logger);
        this.taskService = taskService;
    }

    public abstract void handle();
}
