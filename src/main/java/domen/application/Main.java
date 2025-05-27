package domen.application;

import domen.domain.*;
import domen.infrastructure.database.SubtaskRepositorySQL;
import domen.infrastructure.database.TaskRepositorySQL;

public class Main {
    public static void main(String[] args) {
        SubtaskRepository subtaskRepository = new SubtaskRepositorySQL();
        TaskRepository taskRepository = new TaskRepositorySQL(subtaskRepository);
        SubtaskService subtaskService = new SubtaskService(subtaskRepository, taskRepository);
        TaskService taskService = new TaskService(taskRepository, subtaskService, subtaskRepository);
        TaskReportService taskReportService = new TaskReportService(taskRepository);

        ConsoleApplication app = new ConsoleApplication(taskService, taskReportService, subtaskService);
        app.run();
    }
}