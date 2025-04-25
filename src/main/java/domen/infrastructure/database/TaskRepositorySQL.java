package domen.infrastructure.database;

import domen.domain.TaskRepository;
import domen.domain.model.Task;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class TaskRepositorySQL implements TaskRepository {
    @Override
    public void save(Task task) {
        String saveStatement = "INSERT INTO task (id, title, description, status, start_date_time, finish_date_time) VALUES (?::uuid,?,?,?::task_status,?::timestamp,?::timestamp)";

        try (Connection connection = getConnection();
             var preparedStatement = connection.prepareStatement(saveStatement)) {

            mapTaskToStatement(task, preparedStatement);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public Optional<Task> findById(String id) {
        return Optional.empty();
    }

    @Override
    public void update(Task updatedTask) {

    }

    @Override
    public List<Task> getAll() {
        return List.of();
    }

    public static Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/TaskManager", "admin", "admin");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    private void mapTaskToStatement(Task task, java.sql.PreparedStatement ps) throws SQLException {
        ps.setString(1, task.id());
        ps.setString(2, task.title());
        ps.setString(3, task.description()!=null ? task.description() : null);
        ps.setString(4, task.status().toString());
        ps.setString(5, task.startDateTime() != null ? task.startDateTime().toString() : null);
        ps.setString(6, task.finishDateTime() != null ? task.finishDateTime().toString() : null);
    }
}
