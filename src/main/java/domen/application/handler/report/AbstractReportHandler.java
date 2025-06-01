package domen.application.handler.report;

import domen.application.handler.AbstractConsoleHandler;
import domen.domain.TaskReportService;
import org.slf4j.Logger;

import java.util.Scanner;

public abstract class AbstractReportHandler extends AbstractConsoleHandler {
    protected final TaskReportService taskReportService;

    protected AbstractReportHandler(TaskReportService taskReportService, Scanner scanner, Logger logger) {
        super(scanner, logger);
        this.taskReportService = taskReportService;
    }
}
