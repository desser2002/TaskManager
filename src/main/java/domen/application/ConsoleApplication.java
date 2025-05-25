package domen.application;

import domen.application.handler.ConsoleHandler;
import domen.application.handler.task.*;
import domen.domain.SubtaskService;
import domen.domain.TaskReportService;
import domen.domain.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ConsoleApplication {
    private static Logger logger = LoggerFactory.getLogger(ConsoleApplication.class);
    private final Map<String, ConsoleHandler> handlers = new HashMap<>();
    private final Scanner scanner = new Scanner(System.in);

    public ConsoleApplication(TaskService taskService,
                              TaskReportService reportService,
                              SubtaskService subtaskService) {
        registerHandlers(taskService, reportService, subtaskService);
    }

    private void registerHandlers(TaskService taskService, TaskReportService reportService, SubtaskService subtaskService) {
        List<ConsoleHandler> list = List.of(
                new TaskCreateHandler(taskService, scanner, logger),
                new TaskShowAllHandler(taskService, scanner, logger),
                new TaskAssignTimeHandler(taskService, scanner),
                new TaskEditHandler(taskService, scanner, logger),
                new TaskDeleteHandler(taskService, scanner, logger),
                new TaskChangeStatusHandler(taskService, scanner, logger)
        );
        for (ConsoleHandler handler : list) {
            handlers.put(handler.getCommandName(), handler);
        }
    }

    public void run() {
        logger.info("=== TASK MANAGER ===");
        while (true) {
            logger.info("Enter command (or 'exit'):");
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("exit")) {
                logger.info("Exiting...");
                break;
            }
            ConsoleHandler handler = handlers.get(input);
            if (handler != null) {
                handler.handle();
            } else {
                logger.warn("Unknown command: {}", input);
                printAvailableCommands();
            }
        }
    }

    private void printAvailableCommands() {
        logger.info("Available commands:");
        for (String cmd : handlers.keySet()) {
            logger.info(" - {}", cmd);
        }
    }
}
