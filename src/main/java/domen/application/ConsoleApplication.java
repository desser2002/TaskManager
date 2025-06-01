package domen.application;

import domen.application.handler.ConsoleHandler;
import domen.application.handler.report.ReportCountTaskDoneAtWeekHandler;
import domen.application.handler.report.ReportDelayedTasksHandler;
import domen.application.handler.report.ReportTasksByStatusHandler;
import domen.application.handler.subtask.*;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleApplication.class);
    private final Map<String, ConsoleHandler> handlers = new HashMap<>();
    private final Scanner scanner = new Scanner(System.in);

    public ConsoleApplication(TaskService taskService,
                              TaskReportService reportService,
                              SubtaskService subtaskService) {
        registerHandlers(taskService, reportService, subtaskService);
    }

    private void registerHandlers(TaskService taskService,
                                  TaskReportService reportService, SubtaskService subtaskService) {
        registerTaskHandlers(taskService);
        registerSubtaskHandlers(subtaskService);
        registerReportHandlers(reportService);
    }

    private void registerTaskHandlers(TaskService taskService) {
        List<ConsoleHandler> taskHandlers = List.of(
                new TaskCreateHandler(taskService, scanner, LOGGER),
                new TaskShowAllHandler(taskService, scanner, LOGGER),
                new TaskAssignTimeHandler(taskService, scanner),
                new TaskEditHandler(taskService, scanner, LOGGER),
                new TaskDeleteHandler(taskService, scanner, LOGGER),
                new TaskChangeStatusHandler(taskService, scanner, LOGGER),
                new TaskUpdateStatusHandler(taskService, scanner, LOGGER)
        );
        registerHandlerList(taskHandlers);
    }

    private void registerSubtaskHandlers(SubtaskService subtaskService) {
        List<ConsoleHandler> subtaskHandlers = List.of(
                new SubtaskAddHandler(subtaskService, scanner, LOGGER),
                new SubtaskDeleteHandler(subtaskService, scanner, LOGGER),
                new SubtaskShowByTaskIdHandler(subtaskService, scanner, LOGGER),
                new SubtaskShowHandler(subtaskService, scanner, LOGGER),
                new SubtaskUpdateHandler(subtaskService, scanner, LOGGER),
                new SubtaskMoveHandler(subtaskService, scanner, LOGGER)
        );
        registerHandlerList(subtaskHandlers);
    }

    private void registerReportHandlers(TaskReportService reportService) {
        List<ConsoleHandler> reportHandlers = List.of(
                new ReportDelayedTasksHandler(reportService, scanner, LOGGER),
                new ReportTasksByStatusHandler(reportService, scanner, LOGGER),
                new ReportCountTaskDoneAtWeekHandler(reportService, scanner, LOGGER)
        );
        registerHandlerList(reportHandlers);
    }

    private void registerHandlerList(List<ConsoleHandler> handlerList) {
        for (ConsoleHandler handler : handlerList) {
            handlers.put(handler.getCommandName(), handler);
        }
    }

    public void run() {
        LOGGER.info("=== TASK MANAGER ===");
        while (true) {
            LOGGER.info("Enter command (or 'exit'):");
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("exit")) {
                LOGGER.info("Exiting...");
                break;
            }
            ConsoleHandler handler = handlers.get(input);
            if (handler != null) {
                handler.handle();
            } else {
                LOGGER.warn("Unknown command: {}", input);
                printAvailableCommands();
            }
        }
    }

    private void printAvailableCommands() {
        LOGGER.info("Available commands:");
        for (String cmd : handlers.keySet()) {
            LOGGER.info(" - {}", cmd);
        }
    }
}
