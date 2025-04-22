package domen.domain.model;

import java.util.Objects;

public record Subtask(String id, String title, String description, TaskStatus status) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Subtask subtask)) return false;
        return id.equals(subtask.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
