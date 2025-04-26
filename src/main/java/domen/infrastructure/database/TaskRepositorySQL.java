package domen.infrastructure.database;

import domen.domain.TaskRepository;
import domen.domain.model.Task;

import java.sql.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TaskRepositorySQL implements TaskRepository {
    private static final String SAVE_STATEMENT = "INSERT INTO task (id, title, description, status, start_date_time, finish_date_time)" +
            " VALUES (?::uuid,?,?,?::task_status,?::timestamp,?::timestamp)";

    private final Connection externalConnection;

    public TaskRepositorySQL() {
        this.externalConnection = null;
    }

    public TaskRepositorySQL(Connection connection) {
        this.externalConnection = connection;
    }
    @Override
    public void save(Task task) {

        try (Connection connection = getConnection();
             PreparedStatement ps = prepareSaveTaskStatement(task, connection)) {

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error during writing task info to database", e);
        }

    }

    @Override
    public Optional<Task> findById(String id) {
        return Optional.empty();
    }

    @Override
    public void update(Task updatedTask) {}

    @Override
    public List<Task> getAll() {
        return List.of();
    }

    private Connection getConnection() throws SQLException {
        if (externalConnection != null) {
            return externalConnection; // тестовый мок
        }
        return DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/TaskManager",
                "admin",
                "admin"
        );
    }

    private PreparedStatement prepareSaveTaskStatement(Task task, Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(SAVE_STATEMENT);

        ps.setObject(1, UUID.fromString(task.id()));
        ps.setString(2, task.title());
        ps.setString(3, task.description());
        ps.setString(4, task.status().toString());
        if (task.startDateTime() != null) {
            ps.setTimestamp(5, Timestamp.valueOf(task.startDateTime()));
        } else {
            ps.setNull(5, java.sql.Types.TIMESTAMP);
        }

        if (task.finishDateTime() != null) {
            ps.setTimestamp(6, Timestamp.valueOf(task.finishDateTime()));
        } else {
            ps.setNull(6, java.sql.Types.TIMESTAMP);
        }

        return ps;
    }
}
