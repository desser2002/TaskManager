package domen.application.util;

import domen.domain.model.Subtask;
import org.slf4j.Logger;

import java.util.List;

public class SubtaskPrinter {
    private static final String SEPARATOR = "+" + "-".repeat(36) + "+" + "-".repeat(20) + "+" +
            "-".repeat(30) + "+" + "-".repeat(10) + "+" + "-".repeat(12) + "+";
    private static final String FORMAT = "| %-34s | %-18s | %-28s | %-8s | %-10s |";

    public static void printTable(Logger logger, List<Subtask> subtasks, String taskId) {
        if (subtasks.isEmpty()) {
            logger.info("There are no subtasks");
            return;
        }

        logger.info(SEPARATOR);
        logger.info(String.format(FORMAT, "ID", "Title", "Description", "Status", "Task ID"));
        logger.info(SEPARATOR);

        for (Subtask subtask : subtasks) {
            logger.info(String.format(FORMAT,
                    subtask.id(),
                    truncate(subtask.title(), 18),
                    truncate(subtask.description(), 28),
                    subtask.status(),
                    taskId
            ));
        }

        logger.info(SEPARATOR);
    }

    private static String truncate(String value, int max) {
        return value.length() <= max ? value : value.substring(0, max - 3) + "...";
    }
}
