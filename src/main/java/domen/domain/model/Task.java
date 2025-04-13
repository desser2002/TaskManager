package domen.domain.model;

import java.time.LocalDateTime;

public record Task(String id, String title, String description, TaskStatus status, LocalDateTime startDateTime,
                   LocalDateTime finishDateTime) {


    public Task copyWith(String newTitle, String newDescription, TaskStatus newStatus, LocalDateTime newStartDateTime, LocalDateTime newFinishDateTime) {
        return new Task(
                this.id,
                newTitle != null ? newTitle : this.title,
                newDescription != null ? newDescription : this.description,
                newStatus != null ? newStatus : this.status,
                newStartDateTime != null ? newStartDateTime : this.startDateTime,
                newFinishDateTime != null ? newFinishDateTime : this.finishDateTime);


    }
}
