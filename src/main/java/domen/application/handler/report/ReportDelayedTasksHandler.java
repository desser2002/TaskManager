package domen.application.handler.report;

import domen.application.util.TaskPrinter;
import domen.domain.TaskReportService;
import domen.domain.model.Task;
import org.slf4j.Logger;

import java.util.List;
import java.util.Scanner;

public class ReportDelayedTasksHandler extends AbstractReportHandler {
    public ReportDelayedTasksHandler(TaskReportService taskReportService, Scanner scanner, Logger logger) {
        super(taskReportService, scanner, logger);
    }

    @Override
    public String name() {
        return "report delayed";
    }

    @Override
    public void handle() {
        try {
            List<Task> delayedTasks = taskReportService.delayedTasks();
            if (delayedTasks.isEmpty()) {
                logger.info("No delayed tasks found.");
                return;
            }
            logger.info("=== Delayed Tasks Report ===");
            TaskPrinter.printTable(logger, delayedTasks);
        } catch (Exception e) {
            logger.error("Error generating delayed tasks report: {}", e.getMessage());
        }
    }
}
