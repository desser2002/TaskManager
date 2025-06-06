package domen.application.handler;

import org.slf4j.Logger;

import java.util.Scanner;

public abstract class AbstractConsoleHandler implements ConsoleHandler {
    protected final Scanner scanner;
    protected final Logger logger;

    protected AbstractConsoleHandler(Scanner scanner, Logger logger) {
        this.scanner = scanner;
        this.logger = logger;
    }

    @Override
    public String getCommandName() {
        return name();
    }
}
