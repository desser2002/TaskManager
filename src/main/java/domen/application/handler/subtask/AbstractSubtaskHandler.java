package domen.application.handler.subtask;

import domen.application.handler.AbstractConsoleHandler;
import domen.domain.SubtaskService;
import org.slf4j.Logger;

import java.util.Scanner;

public abstract class AbstractSubtaskHandler extends AbstractConsoleHandler {
    protected SubtaskService subtaskService;

    protected AbstractSubtaskHandler(Scanner scanner, Logger logger, SubtaskService subtaskService) {
        super(scanner, logger);
        this.subtaskService = subtaskService;
    }
}
