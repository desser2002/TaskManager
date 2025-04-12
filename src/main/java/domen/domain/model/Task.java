package domen.domain.model;

import java.util.UUID;

public record Task(String id, String title, String description, TaskStatus status) {


    public Task copyWith(String newTitle, String newDescription, TaskStatus newStatus) {
        return new Task(
                this.id,
                newTitle != null ? newTitle : this.title,
                newDescription != null ? newDescription : this.description,
                newStatus != null ? newStatus : this.status);

    }
}
