package domen.application.handler.report;

import domen.application.util.TaskPrinter;
import domen.domain.TaskReportService;
import domen.domain.model.Task;
import domen.domain.model.TaskStatus;
import org.slf4j.Logger;

import java.util.List;
import java.util.Scanner;

public class ReportTasksByStatusHandler extends AbstractReportHandler {
    public ReportTasksByStatusHandler(TaskReportService taskReportService, Scanner scanner, Logger logger) {
        super(taskReportService, scanner, logger);
    }

    @Override
    public String name() {
        return "report status";
    }

    @Override
    public void handle() {
        try {
            logger.info("Enter task status (NEW, IN_PROGRESS, DONE):");
            String input = scanner.nextLine().trim().toUpperCase();
            TaskStatus status;
            try {
                status = TaskStatus.valueOf(input);
            } catch (IllegalArgumentException e) {
                logger.warn("Invalid status. Please enter one of: NEW, IN_PROGRESS, DONE.");
                return;
            }
            List<Task> tasks = taskReportService.getTaskListByStatus(status);
            if (tasks.isEmpty()) {
                logger.info("No tasks found with status: {}", status);
                return;
            }
            logger.info("=== Tasks with status: {} ===", status);
            TaskPrinter.printTable(logger, tasks);
        } catch (Exception e) {
            logger.error("Error generating tasks by status report: {}", e.getMessage());
        }
    }
}
