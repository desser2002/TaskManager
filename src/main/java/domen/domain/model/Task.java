package domen.domain.model;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

public record Task(String id, String title, String description, TaskStatus status, LocalDateTime startDateTime,
                   LocalDateTime finishDateTime, LinkedHashSet<Subtask> subtasks) {
    public Task copyWithUpdate(String newTitle, String newDescription, TaskStatus newStatus) {
        return new Task(
                this.id,
                newTitle != null ? newTitle : this.title,
                newDescription != null ? newDescription : this.description,
                newStatus != null ? newStatus : this.status,
                this.startDateTime,
                this.finishDateTime,
                this.subtasks);
    }

    public Task copyWithUpdate(LocalDateTime newStartDateTime, LocalDateTime newFinishDateTime) {
        return new Task(
                this.id,
                this.title,
                this.description,
                this.status,
                newStartDateTime != null ? newStartDateTime : this.startDateTime,
                newFinishDateTime != null ? newFinishDateTime : this.finishDateTime,
                this.subtasks
        );
    }

    public Task copyWithUpdate(LinkedHashSet<Subtask> updatedSubtasks) {
        return new Task(
                this.id,
                this.title,
                this.description,
                this.status,
                this.startDateTime,
                this.finishDateTime,
                updatedSubtasks);
    }

    public boolean isActive() {
        return status == TaskStatus.NEW || status == TaskStatus.IN_PROGRESS;
    }

    public boolean isCompleted() {
        return status == TaskStatus.DONE;
    }

    public boolean isOverdue() {
        return finishDateTime != null && !isCompleted() && finishDateTime.isBefore(LocalDateTime.now());
    }

    public Task addSubtask(Subtask subtask) {
        LinkedHashSet<Subtask> updatedSubtasks = new LinkedHashSet<>(this.subtasks);
        updatedSubtasks.add(subtask);
        return copyWithUpdate(updatedSubtasks);
    }

    public Task updateSubtask(Subtask updatedSubtask) {
        LinkedHashSet<Subtask> updatedSubtasks = subtasks.stream()
                .map(s -> s.id().equals(updatedSubtask.id()) ? updatedSubtask : s)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        return copyWithUpdate(updatedSubtasks);
    }
}
