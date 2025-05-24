package domen.application;

import domen.domain.SubtaskService;
import domen.domain.TaskReportService;
import domen.domain.TaskService;

public class ConsoleApplication {
    private final TaskService taskService;
    private final TaskReportService taskReportService;
    private final SubtaskService subtaskService;

    public ConsoleApplication(TaskService taskService,
                              TaskReportService taskReportService, SubtaskService subtaskService) {
        this.taskService = taskService;
        this.taskReportService = taskReportService;
        this.subtaskService = subtaskService;
    }

    public void run() {
    }
}
