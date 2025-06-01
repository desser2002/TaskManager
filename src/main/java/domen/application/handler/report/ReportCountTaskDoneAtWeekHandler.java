package domen.application.handler.report;

import domen.domain.TaskReportService;
import org.slf4j.Logger;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class ReportCountTaskDoneAtWeekHandler extends AbstractReportHandler {
    public ReportCountTaskDoneAtWeekHandler(TaskReportService taskReportService, Scanner scanner, Logger logger) {
        super(taskReportService, scanner, logger);
    }

    @Override
    public String name() {
        return "report weekdone";
    }

    @Override
    public void handle() {
        try {
            logger.info("Enter a date (yyyy-MM-dd) to check tasks done in that week:");
            String input = scanner.nextLine().trim();
            LocalDate date;
            try {
                date = LocalDate.parse(input);
            } catch (DateTimeParseException e) {
                logger.warn("Invalid date format. Please use yyyy-MM-dd.");
                return;
            }
            long count = taskReportService.countTasksDoneAtWeek(date);
            logger.info("Tasks completed during the week of {}: {}", date, count);
        } catch (Exception e) {
            logger.error("Error generating weekly task completion report: {}", e.getMessage());
        }
    }

    @Override
    public String getCommandName() {
        return name();
    }
}
