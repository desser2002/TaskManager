package domen.application.util;

import domen.domain.model.Task;
import org.slf4j.Logger;

import java.time.LocalDateTime;
import java.util.List;

public class TaskPrinter {
    private static final String SEPARATOR = "+" + "-".repeat(38) + "+" + "-".repeat(22) + "+" + "-".repeat(12) +
            "+" + "-".repeat(21) + "+" + "-".repeat(21) + "+";
    private static final String FORMAT = "| %-36s | %-20s | %-10s | %-19s | %-19s |";

    public static void printTable(Logger logger, List<Task> tasks) {
        if (tasks.isEmpty()) {
            logger.info("There are no tasks");
            return;
        }
        logger.info(SEPARATOR);
        logger.info(String.format(FORMAT, "ID", "Title", "Status", "Start", "Deadline"));
        logger.info(SEPARATOR);
        for (Task task : tasks) {
            logger.info(String.format(FORMAT,
                    task.id(),
                    truncate(task.title(), 20),
                    task.status(),
                    format(task.startDateTime()),
                    format(task.finishDateTime())
            ));
        }
        logger.info(SEPARATOR);
    }

    private static String truncate(String value, int max) {
        return value.length() <= max ? value : value.substring(0, max - 3) + "...";
    }

    private static String format(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.toString() : "—";
    }
}
