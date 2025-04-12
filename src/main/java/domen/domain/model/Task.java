package domen.domain.model;

public record Task(String title, String description, TaskSatus status) {

    public Task copyWith(String newTitle, String newDescription, TaskSatus newStatus) {
        return new Task(newTitle != null ? newTitle : this.title,
                newDescription != null ? newDescription : this.description,
                newStatus != null ? newStatus : this.status);

    }
}
