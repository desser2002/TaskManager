package domen.infrastructure.database;

import domen.domain.SubtaskRepository;
import domen.domain.TaskRepository;
import domen.domain.model.Subtask;
import domen.domain.model.Task;
import domen.domain.model.TaskStatus;

import java.sql.*;
import java.util.*;

public class TaskRepositorySQL implements TaskRepository {
    private static final String SAVE_STATEMENT =
            "INSERT INTO task (id, title, description, status, start_date_time, finish_date_time)" +
                    " VALUES (?::uuid,?,?,?::task_status,?::timestamp,?::timestamp)";
    private static final String SELECT_BY_ID_STATEMENT =
            "SELECT id,title,description,status,start_date_time,finish_date_time FROM task WHERE id = ?::uuid";
    private static final String UPDATE_STATEMENT =
            "UPDATE task SET title=?, description=?, status=?, start_date_time=?,finish_date_time=? WHERE id=?";
    private static final String SELECT_ALL_STATEMENT =
            "SELECT id,title,description,status,start_date_time,finish_date_time FROM task";
    private final SubtaskRepository subtaskRepository;
    private final Connection externalConnection;

    public TaskRepositorySQL(SubtaskRepository subtaskRepository) {
        this.subtaskRepository = subtaskRepository;
        this.externalConnection = null;
    }

    public TaskRepositorySQL(SubtaskRepository subtaskRepository, Connection connection) {
        this.subtaskRepository = subtaskRepository;
        this.externalConnection = connection;
    }

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

    @Override
    public void save(Task task) {
        try (PreparedStatement ps = prepareStatementForSaveOrUpdate(task, SAVE_STATEMENT, true)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error during saving task to database", e);
        }
    }

    @Override
    public void update(Task task) {
        try (PreparedStatement ps = prepareStatementForSaveOrUpdate(task, UPDATE_STATEMENT, false)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating task", e);
        }
    }

    @Override
    public Optional<Task> findById(String id) {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(SELECT_BY_ID_STATEMENT)) {
            ps.setObject(1, UUID.fromString(id));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Task task = mapRowToTask(rs);
                    return Optional.of(task);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error during getting task info to database", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Task> getAll() {
        List<Task> tasks = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(SELECT_ALL_STATEMENT);
             ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                Task taskWithSubtasks = mapRowToTaskWithSubtasks(rs);
                tasks.add(taskWithSubtasks);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return tasks;
    }

    private void bindTaskFields(PreparedStatement ps, Task task, int offset) throws SQLException {
        ps.setString(offset, task.title());
        ps.setString(offset + 1, task.description());
        ps.setString(offset + 2, task.status().toString());
        if (task.startDateTime() != null) {
            ps.setTimestamp(offset + 3, Timestamp.valueOf(task.startDateTime()));
        } else {
            ps.setNull(offset + 3, Types.TIMESTAMP);
        }
        if (task.finishDateTime() != null) {
            ps.setTimestamp(offset + 4, Timestamp.valueOf(task.finishDateTime()));
        } else {
            ps.setNull(offset + 4, Types.TIMESTAMP);
        }
    }

    private PreparedStatement prepareStatementForSaveOrUpdate(
            Task task, String sql, boolean idFirst) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement ps = connection.prepareStatement(sql);
        if (idFirst) {
            ps.setObject(1, UUID.fromString(task.id()));
            bindTaskFields(ps, task, 2);
        } else {
            bindTaskFields(ps, task, 1);
            ps.setObject(6, UUID.fromString(task.id()));
        }
        return ps;
    }

    private Task mapRowToTaskWithSubtasks(ResultSet rs) throws SQLException {
        Task task = mapRowToTask(rs);
        return task.copyWithUpdate(
                (LinkedHashSet<Subtask>) subtaskRepository.getSubtasksByTaskId(task.id()));
    }

    private Task mapRowToTask(ResultSet rs) throws SQLException {
        return new Task(
                rs.getObject("id", UUID.class).toString(),
                rs.getString("title"),
                rs.getString("description"),
                TaskStatus.valueOf(rs.getString("status")),
                rs.getTimestamp("start_date_time") != null
                        ? rs.getTimestamp("start_date_time").toLocalDateTime() : null,
                rs.getTimestamp("finish_date_time") != null
                        ? rs.getTimestamp("finish_date_time").toLocalDateTime() : null,
                new LinkedHashSet<>()
        );
    }
}






