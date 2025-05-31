package domen.infrastructure.database;

import domen.domain.SubtaskRepository;
import domen.domain.model.Subtask;
import domen.domain.model.TaskStatus;

import java.sql.*;
import java.util.*;

public class SubtaskRepositorySQL implements SubtaskRepository {
    private static final String UPDATE_SUBTASK_STATEMENT =
            "UPDATE subtask SET title = ?, description = ?, status = ? WHERE id = ?";
    private static final String SAVE_SUBTASK_STATEMENT =
            "INSERT INTO subtask (id, title, description, status,task_id) " +
                    "VALUES (?::uuid,?, ?, ?::task_status,?::uuid)";
    private static final String DELETE_SUBTASK_STATEMENT =
            "DELETE FROM subtask WHERE id = ?";
    private static final String SELECT_SUBTASKS_BY_TASK_ID_STATEMENT =
            "SELECT id, title, description, status FROM subtask WHERE task_id = ?::uuid";
    private static final String UPDATE_SUBTASK_TASK_ID = "UPDATE subtask SET task_id = ?::uuid WHERE id = ?::uuid";
    Connection externalConnection;

    private Connection getConnection() throws SQLException {
        if (externalConnection != null) {
            return externalConnection;
        }
        return DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/TaskManager",
                "admin",
                "admin"
        );
    }

    public SubtaskRepositorySQL() {
        this.externalConnection = null;
    }

    public SubtaskRepositorySQL(Connection connection) {
        this.externalConnection = connection;
    }

    @Override
    public void update(Subtask subtask) {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(UPDATE_SUBTASK_STATEMENT)) {
            ps.setString(1, subtask.title());
            ps.setString(2, subtask.description());
            ps.setString(3, subtask.status().toString());
            ps.setObject(4, UUID.fromString(subtask.id()));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error during updating subtask", e);
        }
    }

    @Override
    public void save(Subtask subtask, String taskId) {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(SAVE_SUBTASK_STATEMENT)) {
            ps.setObject(1, UUID.fromString(subtask.id()));
            ps.setString(2, subtask.title());
            ps.setString(3, subtask.description());
            ps.setString(4, subtask.status().toString());
            ps.setObject(5, UUID.fromString(taskId));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error during save subtask", e);
        }
    }

    @Override
    public void delete(Subtask oldSubtask) {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(DELETE_SUBTASK_STATEMENT)) {
            ps.setObject(1, UUID.fromString(oldSubtask.id()));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error during deleting subtask", e);
        }
    }

    @Override
    public Set<Subtask> getSubtasksByTaskId(String taskId) {
        Set<Subtask> subtasks = new LinkedHashSet<>();
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(SELECT_SUBTASKS_BY_TASK_ID_STATEMENT)) {
            ps.setObject(1, UUID.fromString(taskId));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    subtasks.add(mapRowToSubtask(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return subtasks;
    }

    @Override
    public void move(String subtaskId, String newTaskId) {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(UPDATE_SUBTASK_TASK_ID)) {
            ps.setObject(1, UUID.fromString(newTaskId));
            ps.setObject(2, UUID.fromString(subtaskId));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Subtask mapRowToSubtask(ResultSet rs) throws SQLException {
        return new Subtask(
                rs.getObject("id", UUID.class).toString(),
                rs.getString("title"),
                rs.getString("description"),
                TaskStatus.valueOf(rs.getString("status"))
        );
    }
}

