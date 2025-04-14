package domen.domain.model;

import java.time.LocalDateTime;

public record Task(String id, String title, String description, TaskStatus status, LocalDateTime startDateTime,
                   LocalDateTime finishDateTime) {


    public Task copyWithUpdate(String newTitle, String newDescription, TaskStatus newStatus) {
        return new Task(
                this.id,
                newTitle != null ? newTitle : this.title,
                newDescription != null ? newDescription : this.description,
                newStatus != null ? newStatus : this.status,
                this.startDateTime,
                this.finishDateTime);


    }

    public Task copyWithUpdate(LocalDateTime newStartDateTime, LocalDateTime newFinishDateTime) {
        return new Task(
                this.id,
                this.title,
                this.description,
                this.status,
                newStartDateTime != null ? newStartDateTime : this.startDateTime,
                newFinishDateTime != null ? newFinishDateTime : this.finishDateTime);
    }
}
